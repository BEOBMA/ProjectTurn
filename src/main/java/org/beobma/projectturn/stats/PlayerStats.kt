package org.beobma.projectturn.stats

import org.beobma.projectturn.ProjectTurn
import org.beobma.projectturn.card.Card
import org.beobma.projectturn.event.CardUsingEvent
import org.beobma.projectturn.event.DamageEvent
import org.beobma.projectturn.event.DeathEvent
import org.beobma.projectturn.event.HealEvent
import org.beobma.projectturn.game.GameManager
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.localization.Localization
import org.beobma.projectturn.text.TextManager
import org.beobma.projectturn.util.Utill.Companion.toCard
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.PlayerInventory

@Suppress("DEPRECATION")
data class PlayerStats(
    val player: Player,
    var health: Double,
    var maxHealth: Double,
    var defense: Int,
    var basicPower: Int,
    var relativeSpeed: Int,
    var deck: MutableList<Card> = mutableListOf(),
    val cemetry: MutableList<Card> = mutableListOf(),
    val except: MutableList<Card> = mutableListOf(),
    var maxMana: Int = 3,
    var inventoryDeck: MutableList<Card> = mutableListOf(),
    var backInventory: Inventory? = null
) {
    fun isDead(): Boolean {
        return player.scoreboardTags.contains("death_Player")
    }

    /**
     * 마나를 추가합니다.
     * @param p1 추가할 마나
     */
    fun addMana(p1: Int) {
        if (getMana() + p1 >= maxMana) {
            setMana(maxMana)
            return
        }
        player.scoreboard.getObjective("mana")!!.getScore(player.name).score += p1
    }

    /**
     * 마나를 반환합니다.
     */
    fun getMana(): Int {
        return player.scoreboard.getObjective("mana")!!.getScore(player.name).score
    }

    /**
     * 마나를 설정합니다. 최대 마나를 무시합니다.
     * @param p1 설정할 마나
     */
    fun setMana(p1: Int) {
        player.scoreboard.getObjective("mana")!!.getScore(player.name).score = p1
    }

    /**
     * 마나를 제거합니다.
     * @param p1 제거할 마나
     */
    fun removeMana(p1: Int) {
        player.scoreboard.getObjective("mana")!!.getScore(player.name).score -= p1
    }

    /**
     * 최대 마나를 추가합니다.
     * @param i 추가할 최대 마나
     */
    fun addMaxMana(i: Int) {
        maxMana += i
        player.scoreboard.getObjective("maxMana")!!.getScore(player.name).score = maxMana
    }

    /**
     * 플레이어가 피해를 입습니다.
     * @param damage 입은 피해
     * @param enemy 피해를 입힌 엔티티
     */
    fun damage(damage: Int, enemy: Entity) {
        val game = Info().getGame() ?: return
        var finalDamage = damage

        val enemyBasicPower = game.gameEnemyStats[enemy]?.basicPower ?: 0
        finalDamage += enemyBasicPower

        when {
            finalDamage > defense -> {
                if (defense != 0) {
                    Info.world.playSound(player.location, Sound.ITEM_SHIELD_BREAK, 1.0F, 1.0F)
                }
                finalDamage -= defense
                defense = 0
                player.scoreboard.getObjective("defense")!!.getScore(player.name).score = 0
            }

            finalDamage == defense -> {
                Info.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                finalDamage = 0
                defense = 0
                player.scoreboard.getObjective("defense")!!.getScore(player.name).score = 0
            }

            else -> {
                Info.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                defense -= finalDamage
                player.scoreboard.getObjective("defense")!!.getScore(player.name).score -= finalDamage
                finalDamage = 0
            }
        }

        if (finalDamage <= 0) {
            return
        }

        val damageEvent = DamageEvent(player, enemy, finalDamage)
        ProjectTurn.instance.server.pluginManager.callEvent(damageEvent)
        if (damageEvent.isCancelled) {
            return
        }

        health -= finalDamage
        if (health <= 0) {
            this.death()
        } else {
            player.damage(0.1)
            player.health = health
        }
    }


    /**
     * 방어력을 얻습니다.
     * @param damage 방어력
     */
    fun addDefense(damage: Int) {
        defense += damage

        player.scoreboard.getObjective("defense")!!.getScore(player.name).score += damage
    }

    /**
     * 체력을 회복합니다.
     * @param damage 회복할 체력
     */
    fun heal(damage: Int, entity: Entity) {
        val healEvent = HealEvent(entity, player, damage)
        var finalDamage = damage
        ProjectTurn.instance.server.pluginManager.callEvent(healEvent)
        if (healEvent.isCancelled) {
            return
        }
        finalDamage += healEvent.damage
        health += finalDamage

        if (health > maxHealth) {
            health = maxHealth
        }
    }

    /**
     * 사망 처리합니다.
     */
    fun death() {
        val game = Info().getGame() ?: return

        val deathEvent = DeathEvent(player)
        ProjectTurn.instance.server.pluginManager.callEvent(deathEvent)
        if (deathEvent.isCancelled) {
            return
        }

        this.player.scoreboardTags.add("death_Player")
        this.health = 0.0
        player.gameMode = GameMode.SPECTATOR
        player.teleport(Location(player.world,0.5, -30.0, 0.5))

        var int = 0
        game.players.forEach {
            if (it.scoreboardTags.contains("death_Player")) {
                int++
            }
        }

        if (int == game.players.size) {
            GameManager().gameOver()
        } else {
            GameManager().playerLocationReTake()
            game.gameTurnOrder.remove(player)
        }
    }

    /**
     * 부활 처리합니다.
     */
    fun resurrection() {
        val game = Info().getGame() ?: return

        this.player.scoreboardTags.remove("death_Player")
        player.gameMode = GameMode.ADVENTURE

        player.health = this.health
        GameManager().playerLocationReTake()
    }

    fun useCard(item: ItemStack, card: Card) {
        val game = Info().getGame() ?: return

        ProjectTurn.instance.server.pluginManager.callEvent(
            CardUsingEvent(
                player, card, item
            )
        )

        player.inventory.remove(item)
        game.gamePlayerStats[player]?.removeMana(card.cost)
    }

    fun cardDraw(n: Int) {
        repeat(n) {
            if (isHotbarFull(player)) {
                return
            }

            val drawCard = deck.removeFirstOrNull()
            if (drawCard == null) {
                cemetryResetDeck()
                if (deck.isEmpty()) {
                    return
                }
                val newDrawCard = deck.removeFirstOrNull() ?: return
                val card = newDrawCard.toItem()
                Info().getGame()?.drawCardInt = Info().getGame()!!.drawCardInt + 1
                card.apply {
                    itemMeta = itemMeta.apply {
                        setCustomModelData(Info().getGame()?.drawCardInt)
                    }
                }
                player.inventory.addItem(card)

                player.sendMessage("${ChatColor.GRAY}${TextManager().boldText(newDrawCard.name)}${ChatColor.GRAY}을 뽑았습니다.")
            } else {
                val item = drawCard.toItem()
                Info().getGame()?.drawCardInt = Info().getGame()!!.drawCardInt + 1
                item.apply {
                    itemMeta = itemMeta.apply {
                        setCustomModelData(Info().getGame()?.drawCardInt)
                    }
                }
                player.inventory.addItem(item)
            }
        }
    }

    fun addCard(p1: Card) {
        if (isHotbarFull(player)) {
            return
        }

        val item = p1.toItem()
        Info().getGame()?.drawCardInt = Info().getGame()!!.drawCardInt + 1
        item.apply {
            itemMeta = itemMeta.apply {
                setCustomModelData(Info().getGame()?.drawCardInt)
            }
        }
        player.inventory.addItem(item)

        player.sendMessage("${ChatColor.GRAY}${TextManager().boldText(p1.name)}${ChatColor.GRAY}을 뽑았습니다.")
    }


    fun deckShuppleShuffled() {
        deck.shuffle()
    }

    fun throwCardAll() {
        val items = getPlayerHotbarItems(player)

        items.forEach {
            throwCard(it)
        }
    }

    fun throwCard(card: ItemStack) {
        val item = card.toCard()

        cemetry.add(item)
        player.inventory.removeItem(card)
        player.sendMessage("${ChatColor.BOLD}${item.name} 카드를 버렸습니다.")
    }

    private fun getPlayerHotbarItems(player: Player): List<ItemStack> {
        val items: MutableList<ItemStack> = mutableListOf()
        for (i in 0..8) { // 0부터 8까지 반복
            player.inventory.getItem(i)?.let { items.add(it) }
        }
        return items

    }


    private fun isHotbarFull(player: Player): Boolean {
        return (0..8).all { slot -> player.inventory.getItem(slot) != null && player.inventory.getItem(slot)!!.type.isAir.not() }
    }

    fun cemetryResetDeck() {
        val tempCemetry = ArrayList(cemetry)
        tempCemetry.forEach {
            cemetry.remove(it)
            deck.add(it)
        }
        deckShuppleShuffled()
    }

    fun exceptResetDeck() {
        val tempCemetry = ArrayList(except)
        tempCemetry.forEach {
            cemetry.remove(it)
            deck.add(it)
        }
        deckShuppleShuffled()
    }


    fun loadDeckToInventory() {
        val inventory = player.inventory
        val game = Info().getGame() ?: return

        inventoryDeck = deck.toMutableList()
        val inv = inventoryDeck
        val startIndex = 9
        val nonHotbarSlots = 35

        inv.take(nonHotbarSlots).forEachIndexed { index, card ->
            inventory.setItem(startIndex + index, card.toItem())
            inv.remove(card)
        }

        if (27 < inv.size) {
            inventory.setItem(8, Localization().nextPage)
        }
    }

    fun deckCheckToInventory() {
        val game = Info().getGame() ?: return
        val inventory: Inventory = Bukkit.createInventory(null, 27, "현재 덱") // 3줄짜리 인벤토리


        inventoryDeck = deck.toMutableList()
        val inv = inventoryDeck
        val startIndex = 0
        val nonHotbarSlots = 17

        inv.take(nonHotbarSlots).forEachIndexed { index, card ->
            inventory.setItem(startIndex + index, card.toItem())
            inv.remove(card)
        }

        if (18 < inventoryDeck.size) {
            inventory.setItem(26, Localization().nextPageChest)
        }
        player.openInventory(inventory)
    }

    fun cemetryCheckToInventory() {
        val game = Info().getGame() ?: return
        val inventory: Inventory = Bukkit.createInventory(null, 27, "묘지로 보내진 카드들") // 3줄짜리 인벤토리


        inventoryDeck = cemetry.toMutableList()
        val inv = inventoryDeck
        val startIndex = 0
        val nonHotbarSlots = 17

        inv.take(nonHotbarSlots).forEachIndexed { index, card ->
            inventory.setItem(startIndex + index, card.toItem())
            inv.remove(card)
        }

        if (18 < inventoryDeck.size) {
            inventory.setItem(26, Localization().nextPageChest)
        }
        player.openInventory(inventory)
    }

    fun exceptCheckToInventory() {
        val game = Info().getGame() ?: return
        val inventory: Inventory = Bukkit.createInventory(null, 27, "제외된 카드들") // 3줄짜리 인벤토리


        inventoryDeck = except.toMutableList()
        val inv = inventoryDeck
        val startIndex = 0
        val nonHotbarSlots = 17

        inv.take(nonHotbarSlots).forEachIndexed { index, card ->
            inventory.setItem(startIndex + index, card.toItem())
            inv.remove(card)
        }

        if (18 < inventoryDeck.size) {
            inventory.setItem(26, Localization().nextPageChest)
        }
        player.openInventory(inventory)
    }
}