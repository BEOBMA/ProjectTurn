@file:Suppress("DEPRECATION")

package org.beobma.projectturn.game

import org.beobma.projectturn.ProjectTurn
import org.beobma.projectturn.card.CardPack
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.event.BattleStartEvent
import org.beobma.projectturn.event.TurnEndEvent
import org.beobma.projectturn.event.TurnStartEvent
import org.beobma.projectturn.game.Field.*
import org.beobma.projectturn.game.TileType.*
import org.beobma.projectturn.game.TileType.Event
import org.beobma.projectturn.info.GameInfoType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.eventList
import org.beobma.projectturn.info.Setup.Companion.relicsAllList
import org.beobma.projectturn.info.Setup.Companion.sectorAllList
import org.beobma.projectturn.info.Setup.Companion.startCardList
import org.beobma.projectturn.localization.Localization
import org.beobma.projectturn.relics.Relics
import org.beobma.projectturn.stats.PlayerStats
import org.beobma.projectturn.text.TextManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random


class GameManager {
    private val info = Info()

    fun firstStart() {
        val game = Info().getGame() ?: return

        Info.starting = false
        Info.gaming = true

        game.players.forEach { player ->
            game.gamePlayerStats[player] = PlayerStats(player, 20.0, 20.0, 0, 0, 3, startCardList)
            player.scoreboard.getObjective("maxMana")?.getScore(player.name)?.score = game.gamePlayerStats[player]!!.maxMana
            player.health = 20.0
            player.maxHealth = 20.0
        }

        sectorAllList.keys.forEach {
            game.gameSector.add(it)
        }
        game.gameField = when (game.gameDifficulty) {
            Difficulty.Easy -> Forest
            Difficulty.Normal -> Cave
            Difficulty.Hard -> Sea
        }

        game.gameSector.remove(game.gameField)

        game.nextTileType = Start
        mapMoveEnd()
    }
    fun playerLocationReTake() {
        val game = Info().getGame() ?: return

        val players = game.players.filter { game.gamePlayerStats[it]?.isDead() == false }
        val playerOrigin = when (game.gameField) {
            Forest -> Location(Bukkit.getWorld("world"), -5.5, -40.0, 0.5, -90F, 0F)
            Cave, Sea -> TODO()
            else -> {
                game.stop()
                return
            }
        }

        val numPlayers = players.size
        val middleIndex = numPlayers / 2
        for ((index, player) in players.withIndex()) {
            val offset = if (numPlayers % 2 == 1) index - middleIndex else if (index < middleIndex) -(middleIndex - index) + 1 else index - middleIndex + 1
            player.teleport(playerOrigin.clone().add(0.0, 0.0, offset.toDouble()))
        }
    }
    fun enemyLocationReTake() {
        val game = Info().getGame() ?: return

        val enemyOrigin = when (game.gameField) {
            Forest -> Location(Bukkit.getWorld("world"), 6.5, -40.0, 0.5, 90F, 0F)
            Cave, Sea -> TODO()
            else -> {
                game.stop()
                return
            }
        }

        val enemies = game.gameEnemy
        val numEnemies = enemies.size
        val middleIndex = numEnemies / 2
        for ((index, enemy) in enemies.withIndex()) {
            val offset = if (numEnemies % 2 == 1) (index - middleIndex) * 3 else if (index < middleIndex) -(middleIndex - index) * 3 + 1 else (index - middleIndex) * 3 + 1
            enemy.teleport(enemyOrigin.clone().add(0.0, 0.0, offset.toDouble()))
        }
    }
    fun mapMoveEnd() {
        val game = Info().getGame() ?: return

        object : BukkitRunnable() {
            override fun run() {
                when (game.nextTileType) {
                    Start -> {
                        mapChose()
                    }

                    Battle, HardBattle, Boss -> {
                        handleBattle(game)
                    }

                    Event -> {
                        eventList.random().start()
                        playerLocationReTake()
                    }

                    Rest -> {
                        handleRest(game)
                    }

                    TileType.End -> {
                        sectorChoise()
                    }
                }
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }
    private fun handleBattle(game: Game) {
        val enemyManager = EnemyManager()
        val field = game.gameField ?: run {
            game.stop()
            return
        }

        when (game.nextTileType) {
            Battle -> enemyManager.spawnNormalEnemy(field)
            HardBattle -> enemyManager.spawnHardEnemy(field)
            else -> enemyManager.spawnBossEnemy(field)
        }

        playerLocationReTake()
        enemyLocationReTake()
        startTurn()

        Info().setGameInfo(
            when (game.nextTileType) {
                Battle -> GameInfoType.IsBattle
                HardBattle -> GameInfoType.IsHardBattle
                else -> GameInfoType.IsBoss
            }
        )

        ProjectTurn.instance.server.pluginManager.callEvent(BattleStartEvent())
    }
    private fun handleRest(game: Game) {
        playerLocationReTake()
        game.players.forEach { player ->
            game.gamePlayerStats[player]?.let { stats ->
                stats.heal(5, player)
                if (stats.isDead()) {
                    stats.resurrection()
                }
            }
        }
        object : BukkitRunnable() {
            override fun run() {
                mapChose()
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }
    fun mapChose() {
        val game = info.getGame() ?: return
        val inventory: Inventory = Bukkit.createInventory(null, 27, "${game.gameField?.name}") // 3줄짜리 인벤토리

        game.gameStep++
        for (i in 0 until inventory.size) {
            inventory.setItem(i, Localization().nullPane)
        }
        inventory.setItem(9, Localization().startTile)
        inventory.setItem(17, Localization().endTile)

        fun setRandomTile(slot: Int, tile1: ItemStack, tile2: ItemStack) {
            val randomTile = when (Random.nextInt(1, 4)) {
                1 -> tile1
                else -> tile2
            }
            inventory.setItem(slot, randomTile)
        }

        when (game.gameStep) {
            1 -> {
                setRandomTile(1, Localization().eventTile, Localization().battleTile)
                if (inventory.getItem(1) != Localization().battleTile) {
                    inventory.setItem(10, Localization().battleTile)
                } else {
                    setRandomTile(10, Localization().battleTile, Localization().eventTile)
                }
                if (inventory.getItem(10) != Localization().eventTile) {
                    inventory.setItem(19, Localization().eventTile)
                } else {
                    setRandomTile(19, Localization().eventTile, Localization().battleTile)
                }
            }

            2 -> {
                inventory.setItem(2, Localization().battleTile)
                inventory.setItem(11, Localization().battleTile)
                inventory.setItem(20, Localization().battleTile)
            }

            3 -> {
                inventory.setItem(3, Localization().eventTile)
                inventory.setItem(12, Localization().eventTile)
                inventory.setItem(21, Localization().eventTile)
            }

            4 -> {
                inventory.setItem(13, Localization().hardBattleTile)
            }

            5 -> {
                setRandomTile(5, Localization().eventTile, Localization().battleTile)
                if (inventory.getItem(5) == Localization().hardBattleTile) {
                    inventory.setItem(14, Localization().eventTile)
                } else {
                    setRandomTile(14, Localization().battleTile, Localization().eventTile)
                }
                if (inventory.getItem(14) == Localization().eventTile) {
                    inventory.setItem(23, Localization().battleTile)
                } else {
                    setRandomTile(23, Localization().eventTile, Localization().battleTile)
                }
            }

            6 -> {
                inventory.setItem(15, Localization().restTile)
            }

            7 -> {
                inventory.setItem(16, Localization().bossTile)
            }
        }
        game.mapInventory = inventory
        game.players.forEach {
            it.scoreboardTags.add("mapChose")
            it.openInventory(inventory)
        }
    }
    fun startTurn() {
        val game = info.getGame() ?: return
        val speed: MutableMap<Int, MutableList<Entity>> = mutableMapOf()

        game.players.forEach {
            it.scoreboardTags.add("is_battle")
            it.sendMessage("${ChatColor.GRAY}전체 턴을 시작합니다.")
            it.sendMessage("${ChatColor.GRAY}속도에 따라 턴 순서를 결정합니다...")
        }

        fun addEntitySpeed(entity: Entity, speedMap: MutableMap<Int, MutableList<Entity>>, speed: Int?) {
            speed?.let {
                speedMap.getOrPut(it) { mutableListOf() }.add(entity)
            }
        }

        object : BukkitRunnable() {
            override fun run() {
                game.players.forEach {
                    it.sendMessage("${ChatColor.GRAY}턴 순서가 결정되었습니다.")
                    if (!it.scoreboardTags.contains("death_Player")) {
                        val playerSpeed = game.gamePlayerStats[it]?.relativeSpeed
                        addEntitySpeed(it, speed, playerSpeed)
                    }
                }

                game.gameEnemy.forEach {
                    val enemySpeed = game.gameEnemyStats[it]?.relativeSpeed
                    addEntitySpeed(it, speed, enemySpeed)
                }

                val sortedSpeed = speed.toSortedMap(compareByDescending { it })
                game.gameTurnOrder = sortedSpeed.values.flatten().toMutableList()

                game.gameTurnOrder.firstOrNull()?.let { firstEntity ->
                    firstEntity.turnStart()
                    game.gameTurnOrder.remove(firstEntity)
                } ?: gameEndTurn()
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }
    fun gameEndTurn() {
        val game = info.getGame() ?: return
        val textManager = TextManager()

        textManager.gameNotification("${ChatColor.GRAY}모든 대상의 턴이 종료되었습니다.")
        textManager.gameNotification("${ChatColor.GRAY}턴 종료 처리중입니다...")

        game.countTimer.forEach { timer ->
            if (timer.entity.isDead) {
                timer.countEnd()
            } else {
                timer.countDown()
            }
        }

        object : BukkitRunnable() {
            override fun run() {
                startTurn()
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }
    fun battleEnd() {
        val textManager = TextManager()
        textManager.gameNotification("${ChatColor.GRAY}모든 적이 사망하여 전투가 종료됩니다.")

        val game = info.getGame() ?: return
        val currentGameInfo = info.getGameInfo()
        val type = when (currentGameInfo) {
            GameInfoType.IsBattle -> GameInfoType.IsBattle
            GameInfoType.IsHardBattle -> GameInfoType.IsHardBattle
            GameInfoType.IsBoss -> GameInfoType.IsBoss
            else -> GameInfoType.IsNot
        }

        game.players.forEach {
            it.scoreboardTags.remove("is_battle")
            game.gamePlayerStats[it]?.apply {
                throwCardAll()
                cemetryResetDeck()
                exceptResetDeck()
                resetMachineNesting()
                statusEffect = mutableListOf()
            }
            game.gamePlayerStats[it]!!.battleEndFunctionList.forEach { p ->
                p.invoke()
            }
        }

        game.countTimer.clear()

        object : BukkitRunnable() {
            override fun run() {
                game.players.forEach {
                    when (type) {
                        GameInfoType.IsBattle -> it.normalReward()
                        GameInfoType.IsHardBattle -> it.eliteReward()
                        GameInfoType.IsBoss -> it.relicsReward()
                        else -> Unit
                    }
                    info.setGameInfo(GameInfoType.IsNot)
                }
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }
    fun Entity.turnStart() {
        val game = Info().getGame() ?: return

        sendMessage("${ChatColor.BOLD}당신의 턴입니다.")
        sendMessage("${ChatColor.BOLD}점프하면 턴을 종료합니다.")
        isGlowing = true
        scoreboardTags.add("this_Turn")

        ProjectTurn.instance.server.pluginManager.callEvent(TurnStartEvent(this))
        if (this is Player) {
            playSound(location, Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0F, 1.0F)
            if (game.gamePlayerStats[this]!!.relics.contains(Relics("신비한 카드덱", listOf("${ChatColor.GRAY}턴 시작시 카드를 1장 더 뽑습니다.")))) {
                game.gamePlayerStats[this]?.cardDraw(6)
            }
            else {
                game.gamePlayerStats[this]?.cardDraw(5)
            }
        } else {
            object : BukkitRunnable() {
                override fun run() {
                    game.gameEnemyStats[this@turnStart]?.action()
                }
            }.runTaskLater(ProjectTurn.instance, 60L)
        }
    }
    fun Entity.turnEnd() {
        val game = Info().getGame() ?: return
        val entity = this

        ProjectTurn.instance.server.pluginManager.callEvent(TurnEndEvent(entity))
        entity.scoreboardTags.remove("this_Turn")
        entity.isGlowing = false

        object : BukkitRunnable() {
            override fun run() {
                val firstEntry = game.gameTurnOrder.firstOrNull()
                if (firstEntry != null) {
                    firstEntry.turnStart()
                    game.gameTurnOrder.remove(firstEntry)
                } else {
                    gameEndTurn()
                }
                entity.sendMessage("${ChatColor.BOLD}턴을 종료했습니다.")
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }
    fun gameOver() {
        val game = Info().getGame()
        val textManager = TextManager()

        textManager.apply {
            gameNotification("${ChatColor.GRAY}생존한 아군이 존재하지 않습니다.")
            gameNotification("${ChatColor.GRAY}게임에서 패배했습니다.")
            gameNotification("${ChatColor.GRAY}잠시후 로비로 돌아갑니다...")
        }

        object : BukkitRunnable() {
            override fun run() {
                game?.stop()
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }
    fun Player.normalReward() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "일반 보상") // 3줄짜리 인벤토리
        val game = Info().getGame() ?: return

        fillInventoryWithNullPane(inventory)

        val cardPack = game.gameCardPack.random()
        player!!.scoreboardTags.remove("event")
        player!!.sendMessage("${ChatColor.BOLD}일반 보상을 준비중입니다...")

        object : BukkitRunnable() {
            override fun run() {
                handleCardOperations(11, inventory, cardPack, RarityType.Common, RarityType.Uncommon, RarityType.Rare)
                handleCardOperations(13, inventory, cardPack, RarityType.Common, RarityType.Uncommon, RarityType.Rare)
                handleCardOperations(15, inventory, cardPack, RarityType.Common, RarityType.Uncommon, RarityType.Rare)
                player!!.scoreboardTags.add("rewardChose")
                game.gamePlayerStats[player!!]?.loadDeckToInventory()
                player!!.openInventory(inventory)
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }
    fun Player.eliteReward() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "정예 보상") // 3줄짜리 인벤토리
        val game = Info().getGame() ?: return

        fillInventoryWithNullPane(inventory)

        val cardPack = game.gameCardPack.random()
        player!!.sendMessage("${ChatColor.BOLD}정예 보상을 준비중입니다...")

        object : BukkitRunnable() {
            override fun run() {
                handleCardOperations(11, inventory, cardPack, RarityType.Legend)
                handleCardOperations(13, inventory, cardPack, RarityType.Legend)
                handleCardOperations(15, inventory, cardPack, RarityType.Legend)
                player!!.scoreboardTags.add("rewardChose")
                game.gamePlayerStats[player!!]?.loadDeckToInventory()
                player!!.openInventory(inventory)
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }
    fun Player.relicsReward() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "유물 보상") // 3줄짜리 인벤토리
        val game = Info().getGame() ?: return

        fillInventoryWithNullPane(inventory)

        player!!.sendMessage("${ChatColor.BOLD}유물 보상을 준비중입니다...")

        object : BukkitRunnable() {
            override fun run() {
                handleRelicsOperations(11, inventory)
                handleRelicsOperations(13, inventory)
                handleRelicsOperations(15, inventory)
                player!!.scoreboardTags.add("rewardChose")
                game.gamePlayerStats[player!!]?.loadDeckToInventory()
                player!!.openInventory(inventory)
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }
    fun sectorChoise() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "다음 구역 선택") // 3줄짜리 인벤토리
        val game = Info().getGame() ?: return

        game.gameSectorInt++
        game.eventInventory = inventory
        fillInventoryWithNullPane(inventory)

        TextManager().gameNotification("${ChatColor.BOLD}구역 소탕에 성공했습니다. 곧 다음 구역을 선택합니다.")

        object : BukkitRunnable() {
            override fun run() {
                if (game.gameSectorInt == 4) {
                    inventory.setItem(13, sectorAllList[Field.End])
                } else {
                    handleSectorOperations(11, inventory)
                    handleSectorOperations(13, inventory)
                    handleSectorOperations(15, inventory)
                }
                game.players.forEach {
                    it.scoreboardTags.add("sectorChose")
                    it.openInventory(inventory)
                }
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }

    private fun fillInventoryWithNullPane(inventory: Inventory) {
        for (i in 0 until inventory.size) {
            inventory.setItem(i, Localization().nullPane)
        }
    }
    private fun handleCardOperations(inventorySlot: Int, inventory: Inventory, cardPack: CardPack, vararg rarities: RarityType) {
        val cardList = cardPack.cardList.filter { it.rarity in rarities }.shuffled().firstOrNull()
        inventory.setItem(inventorySlot, cardList?.toItem())
    }
    private fun handleRelicsOperations(inventorySlot: Int, inventory: Inventory) {
        val relics = relicsAllList.keys.random()
        inventory.setItem(inventorySlot, relics.toItem())
    }
    private fun handleSectorOperations(inventorySlot: Int, inventory: Inventory) {
        val game = Info().getGame() ?: return
        val sector = game.gameSector.random()
        inventory.setItem(inventorySlot, sectorAllList[sector])
        game.gameSector.remove(sector)
    }
}