@file:Suppress("DEPRECATION")

package org.beobma.projectturn.game

import com.destroystokyo.paper.event.player.PlayerJumpEvent
import org.beobma.projectturn.ProjectTurn
import org.beobma.projectturn.event.*
import org.beobma.projectturn.gameevent.list.Chaos
import org.beobma.projectturn.gameevent.list.PiggyBank
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.cardAllList
import org.beobma.projectturn.info.Setup.Companion.itemStackToFieldMap
import org.beobma.projectturn.localization.Localization
import org.beobma.projectturn.text.TextManager
import org.beobma.projectturn.util.Utill.Companion.isTeam
import org.beobma.projectturn.util.Utill.Companion.isTurn
import org.beobma.projectturn.util.Utill.Companion.toCard
import org.beobma.projectturn.util.Utill.Companion.toRelics
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.entity.EntityCombustEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import java.lang.reflect.InvocationTargetException

class Event : Listener {

    @EventHandler
    fun onEntityCombust(event: EntityCombustEvent) {
        event.isCancelled = true
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val player = event.player
        val game = Info().getGame() ?: return

        if (player in game.players) {
            if (game.players.size <= 1) {
                game.stop()
            } else {
                val list = game.players.toMutableList()

                list.remove(player)

                game.players = list.toList()
                player.isGlowing = false
                player.inventory.clear()
                player.gameMode = GameMode.ADVENTURE
                val tags = player.scoreboardTags.toList()
                tags.forEach { tag ->
                    player.removeScoreboardTag(tag)
                }
                val scoreboard = player.scoreboard
                listOf("mana", "maxMana", "defense", "MachineNesting").forEach { obj ->
                    scoreboard.getObjective(obj)?.getScore(player.name)?.score = 0
                }
                player.teleport(Location(player.world, 0.5, -60.0, 0.5))
            }
        }
    }

    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        val player = event.player
        val info = Info()

        val game = info.takeIf { it.isGaming() }?.getGame() ?: return
        if (player !in game.players) return

        if (event.from.x != event.to.x || event.from.y != event.to.y || event.from.z != event.to.z) {
            event.isCancelled = true
        }
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
        val game = Info().takeIf { it.isGaming() }?.getGame() ?: return
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
        val playerStats = game.gamePlayerStats[player] ?: return
        val inventoryDeck = playerStats.inventoryDeck
        val startIndex = 0
        val nonHotbarSlots = 17
        playerStats.backInventory = event.clickedInventory!!

        inventoryDeck.take(nonHotbarSlots).forEachIndexed { index, card ->
            event.clickedInventory!!.setItem(startIndex + index, card.toItem())
        }
        inventoryDeck.removeAll(inventoryDeck.take(nonHotbarSlots))
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
        val playerStats = game.gamePlayerStats[player] ?: return
        val inventoryDeck = playerStats.inventoryDeck
        val startIndex = 9
        val nonHotbarSlots = 27
        playerStats.backInventory = player.inventory

        inventoryDeck.take(nonHotbarSlots).forEachIndexed { index, card ->
            player.inventory.setItem(startIndex + index, card.toItem())
        }
        inventoryDeck.removeAll(inventoryDeck.take(nonHotbarSlots))
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
        when {
            player.scoreboardTags.contains("mapChose") -> handleMapChoice(game, player, clickItem)
            player.scoreboardTags.contains("rewardChose") -> handleRewardChoice(game, player, clickItem, event)
            player.scoreboardTags.contains("cardChose") -> handleCardChoice(game, player, clickItem, event)
            player.scoreboardTags.contains("event") -> handleEventChoice(game, player, clickItem, event)
            player.scoreboardTags.contains("sectorChose") -> handleSectorChoice(game, player, clickItem, event)
            player.scoreboardTags.contains("choiceCard") -> handleDeckCardrChoice(game, player, clickItem, event)
        }
    }
    private fun handleDeckCardrChoice(game: Game, player: Player, clickItem: ItemStack, event: InventoryClickEvent) {
        if (event.clickedInventory?.type != InventoryType.CHEST) {
            event.isCancelled = true
            return
        }
        val playerStats = game.gamePlayerStats[player] ?: return
        game.gameField = itemStackToFieldMap[clickItem]
        game.nextTileType = TileType.Start
        playerStats.inventoryDeck.clear()
        player.scoreboardTags.remove("sectorChose")
        game.players.forEach {
            game.gamePlayerStats[it]?.resurrection()
            game.gamePlayerStats[it]?.heal(game.gamePlayerStats[it]!!.maxHealth.toInt(), it)
            player.inventory.clear()
            player.closeInventory()
        }
        game.eventInventory = null
        GameManager().mapMoveEnd()
    }

    private fun handleSectorChoice(game: Game, player: Player, clickItem: ItemStack, event: InventoryClickEvent) {
        if (clickItem == Localization().nullPane || event.clickedInventory?.type != InventoryType.CHEST) {
            event.isCancelled = true
            return
        }
        val playerStats = game.gamePlayerStats[player] ?: return
        game.gameField = itemStackToFieldMap[clickItem]
        game.nextTileType = TileType.Start
        playerStats.inventoryDeck.clear()
        player.scoreboardTags.remove("sectorChose")
        game.players.forEach {
            game.gamePlayerStats[it]?.resurrection()
            game.gamePlayerStats[it]?.heal(game.gamePlayerStats[it]!!.maxHealth.toInt(), it)
            player.inventory.clear()
            player.closeInventory()
        }
        game.eventInventory = null
        GameManager().mapMoveEnd()
    }

    private fun handleEventChoice(game: Game, player: Player, clickItem: ItemStack, event: InventoryClickEvent) {
        if (player.scoreboardTags.contains("chaos")) Chaos().choice(clickItem, player, game)
        if (player.scoreboardTags.contains("piggybank")) PiggyBank().choice(clickItem, player, game)
    }

    private fun handleCardChoice(game: Game, player: Player, clickItem: ItemStack, event: InventoryClickEvent) {
        if (clickItem.toCard() !in cardAllList) {
            event.isCancelled = true
            return
        }
        // 카드 선택 이벤트에서 발생
    }

    private fun handleMapChoice(game: Game, player: Player, clickItem: ItemStack) {
        val loc = Localization()
        val nextTileType = when (clickItem) {
            loc.battleTile -> TileType.Battle
            loc.eventTile -> TileType.Event
            loc.hardBattleTile -> TileType.HardBattle
            loc.restTile -> TileType.Rest
            loc.bossTile -> TileType.Boss
            loc.endTile -> {
                if (game.gameStep != 8) return
                TileType.End
            }

            else -> return
        }

        game.nextTileType = nextTileType
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
        val playerStats = game.gamePlayerStats[player] ?: return
        when (clickItem.type) {
            Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE -> playerStats.deck.add(clickItem.toCard())
            Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE -> playerStats.relics.add(clickItem.toRelics())
            else -> return
        }
        playerStats.deckShuffle()
        playerStats.inventoryDeck.clear()
        player.scoreboardTags.remove("rewardChose")
        player.scoreboardTags.remove("event")
        player.inventory.clear()
        player.closeInventory()

        if (game.players.none { it.scoreboardTags.contains("rewardChose") }) {
            GameManager().mapChose()
        }
    }


    @EventHandler
    fun onInventoryClose(event: InventoryCloseEvent) {
        val player = event.player as? Player ?: return
        val game = Info().takeIf { it.isGaming() }?.getGame() ?: return

        if (player !in game.players) return

        when {
            player.scoreboardTags.contains("mapChose") -> reopenInventoryLater(player, game.mapInventory, "mapChose")
            player.scoreboardTags.contains("rewardChose") -> handleRewardChoice(player, game)
            player.scoreboardTags.contains("event") -> reopenInventoryLater(player, game.eventInventory, "event")
            player.scoreboardTags.contains("sectorChose") -> reopenInventoryLater(
                player, game.eventInventory, "sectorChose"
            )
        }

        game.gamePlayerStats[player]?.apply {
            inventoryDeck.clear()
            backInventory?.clear()
        }
    }

    private fun reopenInventoryLater(player: Player, inventory: Inventory?, tag: String) {
        object : BukkitRunnable() {
            override fun run() {
                if (player.scoreboardTags.contains(tag)) {
                    player.openInventory(inventory!!)
                }
            }
        }.runTaskLater(ProjectTurn.instance, 10L)
    }

    private fun handleRewardChoice(player: Player, game: Game) {
        player.sendMessage("${ChatColor.BOLD}[!] 보상 획득을 포기하셨습니다.")
        player.scoreboardTags.remove("rewardChose")
        player.scoreboardTags.remove("event")
        game.gamePlayerStats[player]?.inventoryDeck?.clear()

        if (game.players.none { it.scoreboardTags.contains("rewardChose") }) {
            GameManager().mapChose()
        }
    }


    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val game = Info().takeIf { it.isGaming() }?.getGame() ?: return

        if (player in game.players) {
            event.isCancelled = true
        }
    }


    @EventHandler
    fun onPlayerJump(event: PlayerJumpEvent) {
        val player = event.player
        val game = Info().takeIf { it.isGaming() }?.getGame() ?: return

        if (player in game.players) {
            if (player.scoreboardTags.contains("this_Turn")) {
                GameManager().run {
                    player.turnEnd()
                }
            }
            event.isCancelled = true
        }
    }


    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val player = event.player
        val item = event.item ?: return
        val game = Info().takeIf { it.isGaming() }?.getGame() ?: return

        if (player !in game.players || !player.isTurn() || !event.action.name.contains("RIGHT")) return

        val card = cardAllList.keys.find { it.name == item.toCard().name.trim() } ?: return
        val playerStats = game.gamePlayerStats[player] ?: return
        if (card.cost > playerStats.getMana()) return

        try {
            val clazz = Class.forName("org.beobma.projectturn.card.list.${cardAllList[card]}")
            val instance = clazz.getDeclaredConstructor().newInstance()
            val method = clazz.getDeclaredMethod("using", Player::class.java, ItemStack::class.java)
            method.invoke(instance, player, item)
        } catch (e: ClassNotFoundException) {
            println("Class not found: ${e.message}")
        } catch (e: NoSuchMethodException) {
            println("Method not found: ${e.message}")
        } catch (e: IllegalAccessException) {
            println("Illegal access: ${e.message}")
        } catch (e: InvocationTargetException) {
            println("Invocation target exception: ${e.message}")
        } catch (e: InstantiationException) {
            println("Instantiation exception: ${e.message}")
        } catch (e: Exception) {
            println("An error occurred: ${e.message}")
        }
    }


    @EventHandler
    fun onPlayerDropItem(event: PlayerDropItemEvent) {
        if (Info().isGaming()) event.isCancelled = true
    }

    @EventHandler
    fun onPlayerDamage(event: EntityDamageByEntityEvent) {
        if (event.damager is Player && event.entity is Player) {
            event.isCancelled = true
        }
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

        game.players.forEach { player ->
            game.gamePlayerStats[player]?.let { stats ->
                stats.setMana(stats.maxMana)
                stats.deckShuffle()
            }
        }

        // 전투 시작시 발동 효과 처리
    }


    @EventHandler
    fun onEntityTurnEnd(event: TurnEndEvent) {
        val player = event.entity
        val game = Info().getGame() ?: return

        if (player.isTeam()) {
            game.gamePlayerStats[player]?.apply {
                throwCardAll()
                addMana(1)
            }
        }
        // 턴 종료 시 발동 효과 처리
    }

    @EventHandler
    fun onEntityTurnStart(event: TurnStartEvent) {
        val player = event.entity
        val game = Info().getGame() ?: return


        game.gameEnemyStats[player]?.let { it.defense = 0 }
        game.gamePlayerStats[player]?.let { it.defense = 0 }


        // 턴 시작시 효과 처리
        if (player is Player) {
            if (game.gamePlayerStats[player]!!.statusEffect.isNotEmpty()) {
                game.gamePlayerStats[player]!!.statusEffect.forEach {
                    it.manifestation()
                }
            }
        } else {
            if (game.gameEnemyStats[player]!!.statusEffect.isNotEmpty()) {
                game.gameEnemyStats[player]!!.statusEffect.forEach {
                    it.manifestation()
                }
            }
        }
    }


    @EventHandler
    fun onAllTurnEnd(event: AllTurnEndEvent) {
        val game = Info().getGame() ?: return

        game.gameEnemy.forEach { entity ->
            game.gameEnemyStats[entity]?.let { it.defense = 0 }
        }

        game.players.forEach { player ->
            game.gamePlayerStats[player]?.let { it.defense = 0 }
        }

        // 전체 턴 종료 시 발동 효과 처리
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
        val textManager = TextManager()

        val attackerName = if (attacker.isTeam()) attacker.name else game.gameEnemyStats[attacker]?.name ?: "Unknown"
        val victimName = if (victim.isTeam()) victim.name else game.gameEnemyStats[victim]?.name ?: "Unknown"

        if (event.message) textManager.gameNotification("${ChatColor.RED}${ChatColor.BOLD}${attackerName}이(가) ${victimName}에게 ${damage}의 피해를 입혔습니다.")

        if (victim is Player) {
            if (game.gamePlayerStats[victim]!!.statusEffect.isNotEmpty()) {
                game.gamePlayerStats[victim]!!.statusEffect.forEach {
                    if (it.type == StatusEffectType.FireShield) {
                        val fire = game.gamePlayerStats[victim]!!.getFireShield()

                        if (fire > 0) {
                            game.gameEnemyStats[attacker]!!.damage(fire, victim)
                        }
                    }
                }
            }
        }
        else {
            if (game.gamePlayerStats[victim]!!.statusEffect.isNotEmpty()) {
                game.gamePlayerStats[victim]!!.statusEffect.forEach {
                    if (it.type == StatusEffectType.Torsion) {
                        val torsion = game.gameEnemyStats[victim]!!.getTorsion()

                        event.damage += torsion

                    }
                }
            }
        }
    }


    @EventHandler
    fun onCardHeal(event: HealEvent) {
        val game = Info().getGame() ?: return
        val healer = event.healer
        val target = event.target
        val damage = event.damage
        val textManager = TextManager()

        val healerName = if (healer.isTeam()) healer.name else game.gameEnemyStats[healer]?.name ?: "Unknown"
        val targetName = if (target.isTeam()) target.name else game.gameEnemyStats[target]?.name ?: "Unknown"

        textManager.gameNotification("${ChatColor.GREEN}${ChatColor.BOLD}${healerName}이(가) ${targetName}을(를) $damage 만큼 회복시켰습니다.")
    }


    @EventHandler
    fun onEntityDeath(event: DeathEvent) {
        val game = Info().getGame() ?: return
        val entity = event.entity
        val textManager = TextManager()

        val entityName = if (entity.isTeam()) entity.name else game.gameEnemyStats[entity]?.name ?: "Unknown"

        textManager.gameNotification("${ChatColor.DARK_RED}${ChatColor.BOLD}${entityName}이(가) 사망했습니다.")
    }

    @EventHandler
    fun onEntityStatusEffectDamage(event: StatusEffectDamageEvent) {
        val game = Info().getGame() ?: return
        val victim = event.victim
        val attacker = event.attacker
        val textManager = TextManager()

        val entityName = if (victim.isTeam()) victim.name else game.gameEnemyStats[victim]?.name ?: "Unknown"

        textManager.gameNotification("${ChatColor.GOLD}${ChatColor.BOLD}${entityName}이(가) 상태이상 피해를 받아 ${event.damage} 의 피해를 입었습니다.")
    }
}