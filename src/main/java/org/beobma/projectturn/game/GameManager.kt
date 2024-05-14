@file:Suppress("DEPRECATION")

package org.beobma.projectturn.game

import org.beobma.projectturn.ProjectTurn
import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.CardPack
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.event.BattleStartEvent
import org.beobma.projectturn.event.TurnEndEvent
import org.beobma.projectturn.game.Field.*
import org.beobma.projectturn.info.GameInfoType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.eventList
import org.beobma.projectturn.info.Setup.Companion.startCardList
import org.beobma.projectturn.localization.Localization
import org.beobma.projectturn.stats.PlayerStats
import org.beobma.projectturn.text.TextManager
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitRunnable
import kotlin.random.Random


class GameManager {
    private val info = Info()

    fun firstStart() {
        val game = info.getGame() ?: return
        Info.starting = false
        Info.gaming = true
        game.players.forEach {
            val stats = PlayerStats(it, 20.0,20.0,0, 0, 3, startCardList)

            game.gamePlayerStats[it] = stats
            it.scoreboard.getObjective("maxMana")!!.getScore(it.name).score = game.gamePlayerStats[it]!!.maxMana
            it.health = 20.0
            it.maxHealth = 20.0
        }

        when (game.gameDifficulty) {
            Difficulty.Easy -> {
                game.gameField = Forest
            }

            Difficulty.Normal -> {
                game.gameField = Cave
            }

            Difficulty.Hard -> {
                game.gameField = Sea
            }
        }
        game.nextTileType = TileType.Start
        mapMoveEnd()
    }

    fun playerLocationReTake() {
        val game = info.getGame() ?: return
        val players: MutableList<Player> = game.players.toMutableList()
        val deadPlayer: MutableList<Player> = mutableListOf()
        players.forEach {
            if (game.gamePlayerStats[it]?.isDead() == true) {
                deadPlayer.add(it)
            }
        }
        deadPlayer.forEach {
            players.remove(it)
        }

        val numPlayers = players.size
        val playerOrigin: Location?

        when (game.gameField) {
            Forest -> {
                playerOrigin = Location(Bukkit.getWorld("world"), -5.5, -40.0, 0.5, -90F, 0F)
            }

            Cave -> TODO()
            Sea -> TODO()
            else -> {
                game.stop()
                return
            }
        }
        if (numPlayers % 2 == 1) {
            val middleIndex = numPlayers / 2
            for ((index, player) in players.withIndex()) {
                val offset = index - middleIndex
                player.teleport(playerOrigin.clone().add(0.0, 0.0, offset.toDouble()))
            }
        } else {
            for ((index, player) in players.withIndex()) {
                val offset = if (index < numPlayers / 2) {
                    -(numPlayers / 2 - index) + 1
                } else {
                    index - numPlayers / 2 + 1
                }
                player.teleport(playerOrigin.clone().add(0.0, 0.0, offset.toDouble()))
            }
        }
    }

    fun enemyLocationReTake() {
        val game = info.getGame() ?: return
        val enemyOrigin: Location?

        when (game.gameField) {
            Forest -> {
                enemyOrigin = Location(Bukkit.getWorld("world"), 6.5, -40.0, 0.5, 90F, 0F)
            }

            Cave -> TODO()
            Sea -> TODO()
            else -> {
                game.stop()
                return
            }
        }

        val enemys = game.gameEnemy
        val numEnemys = game.gameEnemy.size

        if (numEnemys % 2 == 1) {
            val middleIndex = numEnemys / 2
            for ((index, enemy) in enemys.withIndex()) {
                val offset = (index - middleIndex) * 3 // Adjust distance to 3 blocks
                enemy.teleport(enemyOrigin.clone().add(0.0, 0.0, offset.toDouble()))
            }
        } else {
            for ((index, enemy) in enemys.withIndex()) {
                val offset = if (index < numEnemys / 2) {
                    -(numEnemys / 2 - index) * 3 + 1
                } else {
                    (index - numEnemys / 2) * 3 + 1
                }
                enemy.teleport(enemyOrigin.clone().add(0.0, 0.0, offset.toDouble()))
            }
        }
    }


    //맵 이동 후
    fun mapMoveEnd() {
        val game = info.getGame() ?: return

        object : BukkitRunnable() {
            override fun run() {
                when (game.nextTileType) {
                    TileType.Start -> {
                        mapChose()
                        return
                    }

                    TileType.Battle -> {
                        when (game.gameField) {
                            Forest -> {
                                EnemyManager().spawnNormalEnemy(Forest)
                            }

                            Cave -> TODO()
                            Sea -> TODO()
                            else -> {
                                game.stop()
                                return
                            }
                        }
                        playerLocationReTake()
                        enemyLocationReTake()
                        startTurn()
                        Info().setGameInfo(GameInfoType.IsBattle)
                        ProjectTurn.instance.server.pluginManager.callEvent(BattleStartEvent())
                    }

                    TileType.HardBattle -> {
                        when (game.gameField) {
                            Forest -> {
                                EnemyManager().spawnHardEnemy(Forest)
                            }

                            Cave -> TODO()
                            Sea -> TODO()
                            else -> {
                                game.stop()
                                return
                            }
                        }
                        playerLocationReTake()
                        enemyLocationReTake()
                        startTurn()
                        Info().setGameInfo(GameInfoType.IsHardBattle)
                        ProjectTurn.instance.server.pluginManager.callEvent(BattleStartEvent())
                    }

                    TileType.Event -> {
                        val event = eventList.random()

                        event.start()
                        playerLocationReTake()
                    }

                    TileType.Rest -> {
                        playerLocationReTake()

                        game.players.forEach {
                            game.gamePlayerStats[it]?.heal(5, it)
                            if (game.gamePlayerStats[it]?.isDead() == true) {
                                game.gamePlayerStats[it]?.resurrection()
                            }
                        }
                        object : BukkitRunnable() {
                            override fun run() {
                                mapChose()
                            }
                        }.runTaskLater(ProjectTurn.instance, 60L)
                    }

                    TileType.End -> {

                    }
                }


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
        when (game.gameStep) {
            1 -> {
                var int = Random.nextInt(1, 4)
                when (int) {
                    1 -> {
                        inventory.setItem(1, Localization().eventTile)
                    }

                    2 -> {
                        inventory.setItem(1, Localization().battleTile)
                    }

                    3 -> {
                        inventory.setItem(1, Localization().battleTile)
                    }
                }
                if (inventory.getItem(1) != Localization().battleTile) {
                    inventory.setItem(10, Localization().battleTile)

                } else {
                    int = Random.nextInt(1, 4)

                    when (int) {
                        1 -> {
                            inventory.setItem(10, Localization().battleTile)
                        }

                        2 -> {
                            inventory.setItem(10, Localization().eventTile)
                        }

                        3 -> {
                            inventory.setItem(10, Localization().battleTile)
                        }
                    }
                }
                if (inventory.getItem(10) != Localization().eventTile) {
                    inventory.setItem(19, Localization().eventTile)

                } else {
                    int = Random.nextInt(1, 4)

                    when (int) {
                        1 -> {
                            inventory.setItem(19, Localization().eventTile)
                        }

                        2 -> {
                            inventory.setItem(19, Localization().battleTile)
                        }

                        3 -> {
                            inventory.setItem(19, Localization().battleTile)
                        }
                    }
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
                var int = Random.nextInt(1, 4)
                when (int) {
                    1 -> {
                        inventory.setItem(5, Localization().eventTile)
                    }

                    2 -> {
                        inventory.setItem(5, Localization().battleTile)
                    }

                    3 -> {
                        inventory.setItem(5, Localization().hardBattleTile)
                    }
                }
                if (inventory.getItem(5) == Localization().hardBattleTile) {
                    inventory.setItem(14, Localization().eventTile)

                } else {
                    int = Random.nextInt(1, 4)

                    when (int) {
                        1 -> {
                            inventory.setItem(14, Localization().battleTile)
                        }

                        2 -> {
                            inventory.setItem(14, Localization().eventTile)
                        }

                        3 -> {
                            inventory.setItem(14, Localization().battleTile)
                        }
                    }
                }
                if (inventory.getItem(14) == Localization().eventTile) {
                    inventory.setItem(23, Localization().battleTile)

                } else {
                    int = Random.nextInt(1, 4)

                    when (int) {
                        1 -> {
                            inventory.setItem(23, Localization().eventTile)
                        }

                        2 -> {
                            inventory.setItem(23, Localization().battleTile)
                        }

                        3 -> {
                            inventory.setItem(23, Localization().battleTile)
                        }
                    }
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

    //전체 턴 시작
    fun startTurn() {
        val game = info.getGame() ?: return
        val speed: MutableMap<Int, MutableList<Entity>> = mutableMapOf()
        game.players.forEach {
            it.scoreboardTags.add("is_battle")
            it.sendMessage("${ChatColor.GRAY}전체 턴을 시작합니다.")
            it.sendMessage("${ChatColor.GRAY}속도에 따라 턴 순서를 결정합니다...")
        }

        object : BukkitRunnable() {
            override fun run() {
                game.players.forEach {
                    it.sendMessage("${ChatColor.GRAY}턴 순서가 결정되었습니다.")
                    if (!it.scoreboardTags.contains("death_Player")) {
                        val playerSpeed = game.gamePlayerStats[it]?.relativeSpeed
                        if (playerSpeed != null) {
                            speed.getOrPut(playerSpeed) { mutableListOf() }.add(it)
                        }
                    }
                }

                game.gameEnemy.forEach {
                    val enemySpeed = game.gameEnemyStats[it]?.relativeSpeed
                    if (enemySpeed != null) {
                        speed.getOrPut(enemySpeed) { mutableListOf() }.add(it)
                    }
                }

                val sortedSpeed = speed.toSortedMap(compareByDescending { it })
                game.gameTurnOrder = sortedSpeed.flatMap { entry -> entry.value }.toMutableList()
                val firstEntity = game.gameTurnOrder.firstOrNull()
                if (firstEntity != null) {
                    firstEntity.turnStart()
                    game.gameTurnOrder.remove(firstEntity)
                } else {
                    gameEndTurn()
                }
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }

    //전체 턴 종료
    fun gameEndTurn() {
        val game = Info().getGame() ?: return
        TextManager().gameNotification("${ChatColor.GRAY}모든 대상의 턴이 종료되었습니다.")
        TextManager().gameNotification("${ChatColor.GRAY}턴 종료 처리중입니다...")
        game.countTimer.forEach {
            if (it.entity.isDead) {
                it.countEnd()
            } else {
                it.countDown()
            }
        }
        object : BukkitRunnable() {
            override fun run() {
                startTurn()
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }

    //전투 종료
    fun battleEnd() {
        TextManager().gameNotification("${ChatColor.GRAY}모든 적이 사망하여 전투가 종료됩니다.")

        val game = Info().getGame() ?: return
        var type = GameInfoType.IsNot
        if (Info().getGameInfo() == GameInfoType.IsBattle) {
            type = GameInfoType.IsBattle
        }
        else if (Info().getGameInfo() == GameInfoType.IsHardBattle) {
            type = GameInfoType.IsHardBattle
        }

        game.players.forEach {
            it.scoreboardTags.remove("is_battle")
            game.gamePlayerStats[it]?.throwCardAll()
            game.gamePlayerStats[it]?.cemetryResetDeck()
            game.gamePlayerStats[it]?.exceptResetDeck()
        }
        game.countTimer.forEach {
            game.countTimer.remove(it)
        }

        object : BukkitRunnable() {
            override fun run() {
                game.players.forEach {
                    if (type == GameInfoType.IsBattle) {
                        it.nomalReward()
                    }
                    else if (type == GameInfoType.IsHardBattle) {
                        it.eliteReward()
                    }
                    Info().setGameInfo(GameInfoType.IsNot)
                }
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }

    //엔티티의 턴 시작
    fun Entity.turnStart() {
        val game = info.getGame() ?: return
        val entity = this
        entity.sendMessage("${ChatColor.BOLD}당신의 턴입니다.")
        entity.sendMessage("${ChatColor.BOLD}점프하면 턴을 종료합니다.")
        entity.isGlowing = true
        entity.scoreboardTags.add("this_Turn")
        if (entity is Player) {
            entity.playSound(entity.location, Sound.BLOCK_NOTE_BLOCK_GUITAR, 1.0F, 1.0F)
            game.gamePlayerStats[entity]?.cardDraw(5)
        }
        else {
            object : BukkitRunnable() {
                override fun run() {
                    game.gameEnemyStats[entity]?.action()
                }
            }.runTaskLater(ProjectTurn.instance, 60L)
        }
    }

    //엔티티의 턴 종료
    fun Entity.turnEnd() {
        val game = info.getGame() ?: return
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
        TextManager().gameNotification("${ChatColor.GRAY}생존한 아군이 존재하지 않습니다.")
        TextManager().gameNotification("${ChatColor.GRAY}게임에서 패배했습니다.")
        TextManager().gameNotification("${ChatColor.GRAY}잠시후 로비로 돌아갑니다...")

        object : BukkitRunnable() {
            override fun run() {
                game?.stop()
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }

    fun Player.nomalReward() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "일반 보상") // 3줄짜리 인벤토리
        val game = Info().getGame() ?: return
        for (i in 0 until inventory.size) {
            inventory.setItem(i, Localization().nullPane)
        }
        val cardPack = game.gameCardPack.random()
        player!!.sendMessage("${ChatColor.BOLD}일반 보상을 준비중입니다...")
        object : BukkitRunnable() {
            override fun run() {
                normalHandleCardOperations(11, inventory, cardPack)
                normalHandleCardOperations(13, inventory, cardPack)
                normalHandleCardOperations(15, inventory, cardPack)
                player!!.scoreboardTags.add("rewardChose")
                game.gamePlayerStats[player!!]?.loadDeckToInventory()
                player!!.openInventory(inventory)
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }

    fun Player.eliteReward() {
        val inventory: Inventory = Bukkit.createInventory(null, 27, "정예 보상") // 3줄짜리 인벤토리
        val game = Info().getGame() ?: return
        for (i in 0 until inventory.size) {
            inventory.setItem(i, Localization().nullPane)
        }
        val cardPack = game.gameCardPack.random()
        player!!.sendMessage("${ChatColor.BOLD}정예 보상을 준비중입니다...")
        object : BukkitRunnable() {
            override fun run() {
                hardHandleCardOperations(11, inventory, cardPack)
                hardHandleCardOperations(13, inventory, cardPack)
                hardHandleCardOperations(15, inventory, cardPack)
                player!!.scoreboardTags.add("rewardChose")
                game.gamePlayerStats[player!!]?.loadDeckToInventory()
                player!!.openInventory(inventory)
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }

    private fun normalHandleCardOperations(inventorySlot: Int, inventory: Inventory, cardPack: CardPack) {
        val cardPackList = cardPack.cardList
        val cardShuffledList = cardPackList.shuffled()
        val cardList: MutableList<Card> = cardShuffledList.filter { it.rarity == RarityType.Common || it.rarity == RarityType.Uncommon || it.rarity == RarityType.Rare }
            .toMutableList()
        cardList.shuffle()
        val card = cardList.firstOrNull()
        inventory.setItem(inventorySlot, card?.toItem())
    }

    private fun hardHandleCardOperations(inventorySlot: Int, inventory: Inventory, cardPack: CardPack) {
        val cardPackList = cardPack.cardList
        val cardShuffledList = cardPackList.shuffled()
        val cardList: MutableList<Card> = cardShuffledList.filter { it.rarity == RarityType.Legend }
            .toMutableList()
        cardList.shuffle()
        val card = cardList.firstOrNull()
        inventory.setItem(inventorySlot, card?.toItem())
    }
}