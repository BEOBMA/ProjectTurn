@file:Suppress("DEPRECATION")

package org.beobma.projectturn.game

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.beobma.projectturn.ProjectTurn
import org.beobma.projectturn.event.*
import org.beobma.projectturn.gameevent.list.Chaos
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.cardAllList
import org.beobma.projectturn.localization.Localization
import org.beobma.projectturn.text.TextManager
import org.beobma.projectturn.util.Utill.Companion.isTeam
import org.beobma.projectturn.util.Utill.Companion.isTurn
import org.beobma.projectturn.util.Utill.Companion.toCard
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable

class Event : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val info = Info()

        if (!info.isGaming()) return
        val game = info.getGame() ?: return
        if (player !in game.players) return

        val from = event.from
        val to = event.to
        if (from.x == to.x && from.y == to.y && from.z == to.z) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler
    fun onCreatureSpawn(event: CreatureSpawnEvent) {
        val entity = event.entity
        entity.setAI(false)
    }

    @EventHandler
    fun onClickItem(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val clickItem = event.currentItem ?: return
        val info = Info()
        if (!info.isGaming()) return
        val game = info.getGame() ?: return
        val loc = Localization()

        when (clickItem) {
            loc.nextPage -> handleNextPage(game, player)
            loc.previousPage -> handlePreviousPage(game, player)
            loc.nextPageChest -> handleNextPageChest(game, player, event)
            loc.previousPageChest -> handlePreviousPageChest(game, player, event)
            else -> handleTileSelection(game, player, clickItem, event)
        }

        event.isCancelled = true
    }

    private fun handleNextPageChest(game: Game, player: Player, event: InventoryClickEvent) {
        val inventoryDeck = game.gamePlayerStats[player]?.inventoryDeck ?: return
        val startIndex = 0
        val nonHotbarSlots = 17
        game.gamePlayerStats[player]?.backInventory = event.clickedInventory!!

        inventoryDeck.take(nonHotbarSlots).forEachIndexed { index, card ->
            event.clickedInventory!!.setItem(startIndex + index, card.toItem())
            inventoryDeck.remove(card)
        }
        event.clickedInventory!!.setItem(18, Localization().previousPageChest)
        if (inventoryDeck.size <= nonHotbarSlots) {
            event.clickedInventory!!.setItem(26, null)
        }
    }

    private fun handlePreviousPageChest(game: Game, player: Player, event: InventoryClickEvent) {
        val newInventory = game.gamePlayerStats[player]?.backInventory ?: return
        newInventory.contents.forEachIndexed { index, item ->
            event.clickedInventory!!.setItem(index, item)
        }
    }

    private fun handleNextPage(game: Game, player: Player) {
        val inventoryDeck = game.gamePlayerStats[player]?.inventoryDeck ?: return
        val startIndex = 9
        val nonHotbarSlots = 27
        game.gamePlayerStats[player]?.backInventory = player.inventory

        inventoryDeck.take(nonHotbarSlots).forEachIndexed { index, card ->
            player.inventory.setItem(startIndex + index, card.toItem())
            inventoryDeck.remove(card)
        }
        player.inventory.setItem(0, Localization().previousPage)
        if (inventoryDeck.size <= nonHotbarSlots) {
            player.inventory.setItem(8, null)
        }
    }

    private fun handlePreviousPage(game: Game, player: Player) {
        val newInventory = game.gamePlayerStats[player]?.backInventory ?: return
        newInventory.contents.forEachIndexed { index, item ->
            player.inventory.setItem(index, item)
        }
    }

    private fun handleTileSelection(game: Game, player: Player, clickItem: ItemStack, event: InventoryClickEvent) {
        if (player.scoreboardTags.contains("mapChose")) {
            handleMapChoice(game, player, clickItem)
        } else if (player.scoreboardTags.contains("rewardChose")) {
            handleRewardChoice(game, player, clickItem, event)
        } else if (player.scoreboardTags.contains("cardChose")) {
            handleCardChoice(game, player, clickItem, event)
        } else if (player.scoreboardTags.contains("event")) {
            handleEventChoice(game, player, clickItem, event)
        }
    }

    private fun handleEventChoice(game: Game, player: Player, clickItem: ItemStack, event: InventoryClickEvent) {
        if (player.scoreboardTags.contains("chaos")) Chaos().choice(clickItem, player, game)
    }

    private fun handleCardChoice(game: Game, player: Player, clickItem: ItemStack, event: InventoryClickEvent) {
        if (clickItem.toCard() !in cardAllList) {
            event.isCancelled = true
            return
        }
        //카드 선택 이벤트에서 발생
    }

    private fun handleMapChoice(game: Game, player: Player, clickItem: ItemStack) {
        val loc = Localization()
        when (clickItem) {
            loc.battleTile -> game.nextTileType = TileType.Battle
            loc.eventTile -> game.nextTileType = TileType.Event
            loc.hardBattleTile -> game.nextTileType = TileType.HardBattle
            loc.endTile -> {
                if (game.gameStep != 8) return
                game.nextTileType = TileType.End
            }
            else -> return
        }

        game.players.forEach {
            it.scoreboardTags.remove("mapChose")
            it.closeInventory()
        }
        GameManager().mapMoveEnd()
    }

    private fun handleRewardChoice(game: Game, player: Player, clickItem: ItemStack, event: InventoryClickEvent) {
        if (clickItem == Localization().nullPane || event.clickedInventory?.type != InventoryType.CHEST) {
            event.isCancelled = true
            return
        }
        game.gamePlayerStats[player]?.deck?.add(clickItem.toCard())
        game.gamePlayerStats[player]?.deckShuppleShuffled()
        game.gamePlayerStats[player]?.inventoryDeck?.clear()
        player.scoreboardTags.remove("rewardChose")
        player.inventory.clear()
        player.closeInventory()

        if (game.players.none { it.scoreboardTags.contains("rewardChose") }) {
            GameManager().mapChose()
        }
    }


    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player
        val game = Info().getGame() ?: return
        if (!Info().isGaming()) return
        if (player !in game.players) return

        if (player.scoreboardTags.contains("mapChose")) {
            object : BukkitRunnable() {
                override fun run() {
                    player.openInventory(game.mapInventory!!)
                }
            }.runTaskLater(ProjectTurn.instance, 10L)
        }
        else if (player.scoreboardTags.contains("rewardChose")) {
            player.sendMessage("${ChatColor.BOLD}[!] 보상 획득을 포기하셨습니다.")
            player.scoreboardTags.remove("rewardChose")
            game.gamePlayerStats[player]?.inventoryDeck?.clear()

            var i = 0
            game.players.forEach {
                if (it.scoreboardTags.contains("rewardChose")) {
                    i++
                }
            }
            if (i == 0) {
                GameManager().mapChose()
            }
        }
        else if (player.scoreboardTags.contains("event")) {
            object : BukkitRunnable() {
                override fun run() {
                    player.openInventory(game.eventInventory!!)
                }
            }.runTaskLater(ProjectTurn.instance, 10L)
        }
        game.gamePlayerStats[player]?.inventoryDeck?.clear()
        game.gamePlayerStats[player]?.backInventory?.clear()
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val whoClicked = event.whoClicked
        val game = Info().getGame() ?: return

        if (whoClicked is Player) {
            if (Info().isGaming()) {
                if (whoClicked in game.players) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player
        val game = Info().getGame() ?: return

        if (Info().isGaming()) {
            if (player in game.players) {
                if (player.scoreboardTags.contains("this_Turn")) {
                    GameManager().run {
                        player.turnEnd()
                    }
                }
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item
        val game = Info().getGame() ?: return

        if (!Info().isGaming()) return
        if (player !in game.players) return
        if (!player.isTurn()) return
        if (item == null) return
        if (!event.action.name.contains("RIGHT")) return
        val card = cardAllList.keys.find {
            it.name == item.toCard().name.trim()
        } ?: return
        val playerStats = game.gamePlayerStats[player] ?: return
        if (card.cost > playerStats.getMana()) return

        try {
            val clazz = Class.forName("org.beobma.projectturn.card.list.${cardAllList[card]}")
            val instance = clazz.getDeclaredConstructor().newInstance()
            val method = clazz.getDeclaredMethod("using", Player::class.java, ItemStack::class.java)

            method.invoke(instance, player, item)
        } catch (e: ClassNotFoundException) {
            println("Class not found: $e")
        } catch (e: Exception) {
            println("An error occurred: $e")
        }
    }

    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        if (Info().isGaming()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        val damagedEntity = event.entity
        val damagerEntity = event.damager

        if (damagerEntity is Player && damagedEntity is Player) event.isCancelled = true
    }

    @EventHandler
    fun onSwapHands(event: PlayerSwapHandItemsEvent) {
        if (Info().isGaming()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBattleStart(event: BattleStartEvent) {
        val game = Info().getGame() ?: return

        game.players.forEach {
            if (game.gamePlayerStats[it] != null) {
                game.gamePlayerStats[it]!!.setMana(game.gamePlayerStats[it]!!.maxMana)
                game.gamePlayerStats[it]!!.deckShuppleShuffled()
            }
        }


        //전투 시작시 발동 효과 처리
    }

    @EventHandler
    fun onEntityTurnEnd(event: TurnEndEvent) {
        val player = event.entity
        val game = Info().getGame() ?: return

        if (player.isTeam()) {
            game.gamePlayerStats[player]?.throwCardAll()
            game.gamePlayerStats[player]?.addMana(1)
        }
        //턴 종료시 발동 효과 처리
    }

    @EventHandler
    fun onAllTurnEnd(event: AllTurnEndEvent) {
        val game = Info().getGame() ?: return

        game.gameEnemy.forEach {
            game.gameEnemyStats[it]?.defense = 0
        }

        game.players.forEach {
            game.gamePlayerStats[it]?.defense = 0
        }

        //전체 턴 종료시 발동 효과 처리
    }

    @EventHandler
    fun onCardUsing(event: CardUsingEvent) {
        val game = Info().getGame() ?: return

        //아무 카드의 효과 발동시 발동 효과 처리

    }

    @EventHandler
    fun onCardDamage(event: DamageEvent) {
        val game = Info().getGame() ?: return
        val victim = event.victim
        val attacker = event.attacker
        val damage = event.damage

        if (attacker.isTeam() && victim.isTeam()) {
            TextManager().gameNotification("${ChatColor.RED}${ChatColor.BOLD}${attacker.name}이(가) ${victim.name}에게 ${damage}의 피해를 입혔습니다.")
        }
        else if (attacker.isTeam() && !victim.isTeam()) {
            TextManager().gameNotification("${ChatColor.RED}${ChatColor.BOLD}${attacker.name}이(가) ${game.gameEnemyStats[victim]?.name}에게 ${damage}의 피해를 입혔습니다.")
        }
        else if (!attacker.isTeam() && victim.isTeam()) {
            TextManager().gameNotification("${ChatColor.RED}${ChatColor.BOLD}${game.gameEnemyStats[attacker]?.name}이(가) ${victim.name}에게 ${damage}의 피해를 입혔습니다.")
        }
        else {
            TextManager().gameNotification("${ChatColor.RED}${ChatColor.BOLD}${game.gameEnemyStats[attacker]?.name}이(가) ${game.gameEnemyStats[victim]?.name}에게 ${damage}의 피해를 입혔습니다.")
        }
    }

    @EventHandler
    fun onCardHeal(event: HealEvent) {
        val game = Info().getGame() ?: return
        val healer = event.healer
        val target = event.target
        val damage = event.damage

        if (healer.isTeam() && target.isTeam()) {
            TextManager().gameNotification("${ChatColor.GREEN}${ChatColor.BOLD}${healer.name}이(가) ${target.name}을(를) $damage 만큼 회복시켰습니다.")
        }
        else if (healer.isTeam() && !target.isTeam()) {
            TextManager().gameNotification("${ChatColor.GREEN}${ChatColor.BOLD}${healer.name}이(가) ${game.gameEnemyStats[target]?.name}을(를) $damage 만큼 회복시켰습니다.")
        }
        else if (!healer.isTeam() && target.isTeam()) {
            TextManager().gameNotification("${ChatColor.GREEN}${ChatColor.BOLD}${game.gameEnemyStats[healer]?.name}이(가) ${target.name}을(를) $damage 만큼 회복시켰습니다.")
        }
        else {
            TextManager().gameNotification("${ChatColor.GREEN}${ChatColor.BOLD}${game.gameEnemyStats[healer]?.name}이(가) ${game.gameEnemyStats[target]?.name}을(를) $damage 만큼 회복시켰습니다.")
        }
    }

    @EventHandler
    fun onEntityDeath(event: DeathEvent) {
        val game = Info().getGame() ?: return
        val entity = event.entity

        if (entity.isTeam()) {
            TextManager().gameNotification("${ChatColor.DARK_RED}${ChatColor.BOLD}${entity.name}이(가) 사망했습니다.")
        } else {
            TextManager().gameNotification("${ChatColor.DARK_RED}${ChatColor.BOLD}${game.gameEnemyStats[entity]?.name}이(가) 사망했습니다.")
        }
    }
}