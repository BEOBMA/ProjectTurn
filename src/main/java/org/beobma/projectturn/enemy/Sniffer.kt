package org.beobma.projectturn.enemy

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats
import org.beobma.projectturn.text.TextManager
import org.bukkit.ChatColor
import kotlin.random.Random

class Sniffer {
    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return


        if (!enemy.enemy.scoreboardTags.contains("ReTurn")) {
            enemy.addDefense(20)
            game.gameTurnOrder.add(0, enemy.enemy)
            enemy.enemy.scoreboardTags.add("ReTurn")
            TextManager().gameNotification("${ChatColor.BOLD}${ChatColor.DARK_RED}${enemy.name} 이(가) 재행동합니다.")
        }
        else {
            enemy.enemy.scoreboardTags.remove("ReTurn")
            game.players.first().scoreboard.getObjective("marker")!!.getScore(enemy.name).score += 1
            when (game.players.first().scoreboard.getObjective("marker")!!.getScore(enemy.name).score) {
                5 -> {
                    TextManager().gameNotification("${ChatColor.BOLD}${ChatColor.DARK_RED}${enemy.name}의 충전이 완료되어 다음 턴에 강력한 피해를 입힙니다.")
                }
                6 -> {
                    game.players.forEach {
                        game.gamePlayerStats[it]?.damage(20, enemy.enemy)
                    }
                    game.players.first().scoreboard.getObjective("marker")!!.getScore(enemy.name).score = 0
                }
                else -> {
                    TextManager().gameNotification("${ChatColor.BOLD}${ChatColor.RED}${enemy.name}의 충전 스택이 쌓였습니다.")
                }
            }
        }
    }
}