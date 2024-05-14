@file:Suppress("DEPRECATION")

package org.beobma.projectturn.game

import org.beobma.projectturn.ProjectTurn
import org.beobma.projectturn.card.CardPack
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.cardPackList
import org.beobma.projectturn.stats.EnemyStats
import org.beobma.projectturn.stats.PlayerStats
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.scheduler.BukkitRunnable

data class Game(
    var players: List<Player>,
    val gameType: GameType,
    val gameDifficulty: Difficulty,
    val gameCardPack: MutableList<CardPack> = mutableListOf(),
    var gameField: Field? = null,
    var gameDetailsField: DetailsField = DetailsField.Null,
    var gameEnemy: MutableList<Entity> = mutableListOf(),
    var gameTurnOrder: MutableList<Entity> = mutableListOf(),
    var gamePlayerStats: MutableMap<Player, PlayerStats> = mutableMapOf(),
    var gameEnemyStats: MutableMap<Entity, EnemyStats> = mutableMapOf(),
    var gameStep: Int = 0,
    var nextTileType: TileType = TileType.Start,
    var mapInventory: Inventory? = null,
    var eventInventory: Inventory? = null,
    var drawCardInt: Int = 0,
    val countTimer: MutableList<TurnCount> = mutableListOf()
) {
    fun start() {
        Info.game = this
        Info.starting = true

        Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 잠시 후 게임을 준비합니다.")

        object : BukkitRunnable() {
            override fun run() {
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 해당 플러그인과 맵은 BEOBMA에 의해 개발되었으며 모든 저작권은 BEOBMA가 소유합니다.")
                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 해당 플러그인에 대한 무단 수정, 배포 등을 금지합니다.")
                object : BukkitRunnable() {
                    override fun run() {
                        Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 잠시 후 게임에 등장할 카드 팩을 뽑습니다.")
                        object : BukkitRunnable() {
                            override fun run() {
                                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 등장할 카드 팩:")
                                var cardPackListA = cardPackList
                                cardPackListA = cardPackListA.shuffled()
                                gameCardPack.add(cardPackListA[0])
                                gameCardPack.add(cardPackListA[1])
                                gameCardPack.add(cardPackListA[2])
                                gameCardPack.add(cardPackListA[3])
                                gameCardPack.add(cardPackListA[4])
                                Bukkit.broadcastMessage(cardPackListA[0].name)
                                Bukkit.broadcastMessage(cardPackListA[1].name)
                                Bukkit.broadcastMessage(cardPackListA[2].name)
                                Bukkit.broadcastMessage(cardPackListA[3].name)
                                Bukkit.broadcastMessage(cardPackListA[4].name)
                                object : BukkitRunnable() {
                                    override fun run() {
                                        Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 특정 이벤트로 이 외의 카드 팩 또한 게임에 등장할 수 있습니다.")
                                        object : BukkitRunnable() {
                                            override fun run() {
                                                Bukkit.broadcastMessage("${ChatColor.BOLD}[!] 잠시 후 게임이 시작됩니다.")
                                                object : BukkitRunnable() {
                                                    override fun run() {
                                                        GameManager().firstStart()
                                                    }
                                                }.runTaskLater(ProjectTurn.instance, 60L)
                                            }
                                        }.runTaskLater(ProjectTurn.instance, 60L)
                                    }
                                }.runTaskLater(ProjectTurn.instance, 40L)
                            }
                        }.runTaskLater(ProjectTurn.instance, 60L)
                    }
                }.runTaskLater(ProjectTurn.instance, 60L)
            }
        }.runTaskLater(ProjectTurn.instance, 60L)
    }

    fun stop() {
        val game = Info().getGame() ?: return
        val players = game.players
        val enemy = game.gameEnemy
        ProjectTurn.instance.server.scheduler.cancelTasks(ProjectTurn.instance)
        players.forEach { player ->
            player.isGlowing = false
            player.inventory.clear()
            player.gameMode = GameMode.ADVENTURE
            player.scoreboard.getObjective("mana")!!.getScore(player.name).score = 0
            player.scoreboard.getObjective("maxMana")!!.getScore(player.name).score = 0
            player.scoreboard.getObjective("defense")!!.getScore(player.name).score = 0
            player.teleport(Location(player.world,0.5, -60.0, 0.5))
            
            val tagsCopy = ArrayList(player.scoreboardTags)
            tagsCopy.forEach { tag ->
                player.scoreboardTags.remove(tag)
            }
        }
        enemy.forEach {
            it.remove()
        }
        Info.game = null
        Info.starting = false
        Info.gaming = false
    }

    fun playerRandomTarget(): Player {
        var player: Player
        do {
            player = this.players.random()
        } while (player.scoreboardTags.contains("death_Player"))

        return player
    }
}

enum class GameType {
    Nomal
}
enum class Difficulty {
    Easy, Normal, Hard
}
enum class Field {
    Forest, Cave, Sea
}
enum class DetailsField {
    Null
}
enum class TileType {
    Start, Battle, HardBattle, Event, Rest, End
}