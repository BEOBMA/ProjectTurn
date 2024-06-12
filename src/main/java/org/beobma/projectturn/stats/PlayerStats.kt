package org.beobma.projectturn.stats

import org.beobma.projectturn.ProjectTurn
import org.beobma.projectturn.game.StatusEffect
import org.beobma.projectturn.game.StatusEffectType
import org.beobma.projectturn.card.Card
import org.beobma.projectturn.event.*
import org.beobma.projectturn.game.GameManager
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.localization.Localization
import org.beobma.projectturn.relics.Relics
import org.beobma.projectturn.text.TextManager
import org.beobma.projectturn.util.Utill.Companion.toCard
import org.bukkit.*
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

@Suppress("DEPRECATION")
data class PlayerStats(
    val player: Player,
    var health: Double,
    var maxHealth: Double,
    var defense: Int,
    var basicPower: Int,
    var relativeSpeed: Int,
    var deck: MutableList<Card> = mutableListOf(),
    var cemetry: MutableList<Card> = mutableListOf(),
    val except: MutableList<Card> = mutableListOf(),
    var maxMana: Int = 3,
    var inventoryDeck: MutableList<Card> = mutableListOf(),
    var backInventory: Inventory? = null,
    val relics: MutableList<Relics> = mutableListOf(),
    var statusEffect: MutableList<StatusEffect> = mutableListOf(),
    val battleEndFunctionList: MutableList<() -> Unit> = mutableListOf()
) {

    fun sameCardDisappears(originCard: Card) {
        val game = Info().getGame() ?: return
        val cardList = mutableListOf<Card>()
        game.gamePlayerStats[player]?.deck?.forEach {
            if (it == originCard) {
                game.gamePlayerStats[player]?.except?.add(it)
            } else {
                cardList.add(it)
            }
        }
        game.gamePlayerStats[player]?.deck = cardList

        val cardList1 = mutableListOf<Card>()
        game.gamePlayerStats[player]?.cemetry?.forEach {
            if (it == originCard) {
                game.gamePlayerStats[player]?.except?.add(it)
            } else {
                cardList1.add(it)
            }
        }
        game.gamePlayerStats[player]?.cemetry = cardList1

        val removeCards = player.inventory.filter { it.toCard() == originCard }
        player.inventory.removeAll { it.toCard() == originCard }

        removeCards.forEach {
            game.gamePlayerStats[player]?.except?.add(it.toCard())
        }
    }

    fun isDead(): Boolean {
        return player.scoreboardTags.contains("death_Player")
    }

    fun addMachineNesting(p1: Int) {
        this.player.scoreboard.getObjective("MachineNesting")!!.getScore(this.player.name).score += p1
        this.player.sendMessage("${ChatColor.BOLD}현재 기계 중첩: ${getMachineNesting()}")
    }

    fun getMachineNesting(): Int {
        return this.player.scoreboard.getObjective("MachineNesting")!!.getScore(this.player.name).score
    }

    fun removeMachineNesting(p1: Int) {
        this.player.scoreboard.getObjective("MachineNesting")!!.getScore(this.player.name).score -= p1
    }

    fun resetMachineNesting() {
        this.player.scoreboard.getObjective("MachineNesting")!!.getScore(this.player.name).score = 0
    }

    fun addFireShield(p1: Int, caster: Entity) {
        statusEffect.add(StatusEffect(StatusEffectType.FireShield, p1, 1, this.player, caster))
    }

    fun getFireShield(): Int {
        return statusEffect.count { it.type == StatusEffectType.FireShield }
    }

    fun addBurn(p1: Int, caster: Entity) {
        statusEffect.add(StatusEffect(StatusEffectType.Burn, p1, 1, this.player, caster))
    }

    fun getBurn(): Int {
        return statusEffect.count { it.type == StatusEffectType.Burn }
    }

    fun addMana(p1: Int) {
        val newMana = (getMana() + p1).coerceAtMost(maxMana)
        setMana(newMana)
    }

    fun getMana(): Int {
        return player.scoreboard.getObjective("mana")!!.getScore(player.name).score
    }

    fun setMana(p1: Int) {
        player.scoreboard.getObjective("mana")!!.getScore(player.name).score = p1
    }

    fun removeMana(p1: Int) {
        setMana(getMana() - p1)
    }

    fun addMaxMana(i: Int) {
        maxMana += i
        player.scoreboard.getObjective("maxMana")!!.getScore(player.name).score = maxMana
    }

    fun damage(damage: Int, enemy: Entity) {
        val game = Info().getGame() ?: return
        val enemyBasicPower = game.gameEnemyStats[enemy]?.basicPower ?: 0
        var finalDamage = damage + enemyBasicPower

        finalDamage = when {
            finalDamage > defense -> {
                if (defense != 0) Info.world.playSound(player.location, Sound.ITEM_SHIELD_BREAK, 1.0F, 1.0F)
                finalDamage - defense
            }
            finalDamage == defense -> {
                Info.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                0
            }
            else -> {
                Info.world.playSound(player.location, Sound.ITEM_SHIELD_BLOCK, 1.0F, 1.0F)
                defense -= finalDamage
                0
            }
        }

        defense = 0.coerceAtLeast(defense - finalDamage)
        player.scoreboard.getObjective("defense")!!.getScore(player.name).score = defense

        if (finalDamage <= 0) return

        val damageEvent = DamageEvent(player, enemy, finalDamage)
        ProjectTurn.instance.server.pluginManager.callEvent(damageEvent)
        if (damageEvent.isCancelled) return

        health -= finalDamage
        if (health <= 0) {
            this.death()
        } else {
            player.damage(0.1)
            player.health = health
        }
    }

    fun addDefense(damage: Int) {
        defense += damage
        player.scoreboard.getObjective("defense")!!.getScore(player.name).score += damage
    }

    fun heal(damage: Int, entity: Entity) {
        val healEvent = HealEvent(entity, player, damage)
        ProjectTurn.instance.server.pluginManager.callEvent(healEvent)
        if (healEvent.isCancelled) return

        val finalDamage = healEvent.damage + damage
        health = (health + finalDamage).coerceAtMost(maxHealth)
    }

    fun death() {
        val game = Info().getGame() ?: return

        val deathEvent = DeathEvent(player)
        ProjectTurn.instance.server.pluginManager.callEvent(deathEvent)
        if (deathEvent.isCancelled) return

        player.scoreboardTags.add("death_Player")
        health = 0.0
        player.gameMode = GameMode.SPECTATOR
        player.teleport(Location(player.world, 0.5, -30.0, 0.5))

        if (game.players.all { it.scoreboardTags.contains("death_Player") }) {
            GameManager().gameOver()
        } else {
            GameManager().playerLocationReTake()
            game.gameTurnOrder.remove(player)
        }
    }

    fun resurrection() {
        val game = Info().getGame() ?: return

        player.scoreboardTags.remove("death_Player")
        player.gameMode = GameMode.ADVENTURE
        player.health = this.health
        GameManager().playerLocationReTake()
    }

    fun useCard(item: ItemStack, card: Card, cardTypeBoolean: Boolean = true) {
        val game = Info().getGame() ?: return

        ProjectTurn.instance.server.pluginManager.callEvent(
            CardUsingEvent(player, card, item)
        )

        if (cardTypeBoolean) {
            player.inventory.remove(item)
            game.gamePlayerStats[player]?.cemetry?.add(card)
        }

        game.gamePlayerStats[player]?.removeMana(card.cost)
    }

    fun cardDraw(n: Int) {
        repeat(n) {
            if (isHotbarFull(player)) return

            val drawCard = deck.removeFirstOrNull() ?: run {
                cemetryResetDeck()
                deck.removeFirstOrNull()
            } ?: return

            val item = drawCard.toItem().apply {
                itemMeta = itemMeta.apply {
                    setCustomModelData((Info().getGame()?.drawCardInt ?: 0) + 1)
                }
            }

            player.inventory.addItem(item)
            player.sendMessage("${ChatColor.GRAY}${TextManager().boldText(drawCard.name)}${ChatColor.GRAY}을 뽑았습니다.")
        }
    }

    fun addCard(card: Card) {
        if (isHotbarFull(player)) return

        val item = card.toItem().apply {
            itemMeta = itemMeta.apply {
                setCustomModelData((Info().getGame()?.drawCardInt ?: 0) + 1)
            }
        }

        player.inventory.addItem(item)
        player.sendMessage("${ChatColor.GRAY}${TextManager().boldText(card.name)}${ChatColor.GRAY}을 뽑았습니다.")
    }

    fun deckShuffle() {
        deck.shuffle()
    }

    fun throwCardAll() {
        getPlayerHotbarItems(player).forEach { throwCard(it) }
    }

    fun throwCard(card: ItemStack) {
        cemetry.add(card.toCard())
        player.inventory.removeItem(card)
        player.sendMessage("${ChatColor.BOLD}${card.toCard().name} 카드를 버렸습니다.")
    }

    private fun getPlayerHotbarItems(player: Player): List<ItemStack> {
        return (0..8).mapNotNull { player.inventory.getItem(it) }
    }

    private fun isHotbarFull(player: Player): Boolean {
        return (0..8).all { slot -> player.inventory.getItem(slot)?.type?.isAir == false }
    }

    fun cemetryResetDeck() {
        cemetry.forEach {
            deck.add(it)
        }
        cemetry.clear()
        deckShuffle()
    }

    fun exceptResetDeck() {
        except.forEach {
            deck.add(it)
        }
        except.clear()
        deckShuffle()
    }

    fun loadDeckToInventory() {
        inventoryDeck = deck.toMutableList()
        val nonHotbarSlots = 35

        inventoryDeck.take(nonHotbarSlots).forEachIndexed { index, card ->
            player.inventory.setItem(index + 9, card.toItem())
        }

        if (inventoryDeck.size > 27) {
            player.inventory.setItem(8, Localization().nextPage)
        }
    }

    fun deckCheckToInventory() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "현재 덱 (순서 랜덤)")

        inventoryDeck = deck.shuffled().toMutableList()

        inventoryDeck.take(18).forEachIndexed { index, card ->
            inventory.setItem(index, card.toItem())
        }

        if (inventoryDeck.size > 18) {
            inventory.setItem(26, Localization().nextPageChest)
        }

        player.openInventory(inventory)
    }

    fun cemeteryCheckToInventory() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "묘지로 보내진 카드들")

        inventoryDeck = cemetry.toMutableList()

        inventoryDeck.take(18).forEachIndexed { index, card ->
            inventory.setItem(index, card.toItem())
        }

        if (inventoryDeck.size > 18) {
            inventory.setItem(26, Localization().nextPageChest)
        }

        player.openInventory(inventory)
    }

    fun exceptCheckToInventory() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "제외된 카드들")

        inventoryDeck = except.toMutableList()

        inventoryDeck.take(18).forEachIndexed { index, card ->
            inventory.setItem(index, card.toItem())
        }

        if (inventoryDeck.size > 18) {
            inventory.setItem(26, Localization().nextPageChest)
        }

        player.openInventory(inventory)
    }

    fun relicsCheckToInventory() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "유물")

        relics.take(18).forEachIndexed { index, relic ->
            inventory.setItem(index, relic.toItem())
        }

        if (relics.size > 18) {
            inventory.setItem(26, Localization().nextPageChest)
        }

        player.openInventory(inventory)
    }

    fun deckCheck(deck: MutableList<Card>) {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "카드 선택")

        inventoryDeck = deck.shuffled().toMutableList()

        inventoryDeck.take(18).forEachIndexed { index, card ->
            inventory.setItem(index, card.toItem())
        }

        player.scoreboardTags.add("choiceCard")
        player.openInventory(inventory)
    }
}
