package org.beobma.projectturn.enemy

import org.beobma.projectturn.game.TurnCount
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats
import org.beobma.projectturn.util.Utill.Companion.calculatePercentage

class Wolf {

    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return

        if (game.gameEnemy.size != 4) {
            var i = 0
            game.gameEnemy.forEach {
                if (game.gameEnemyStats[it]!!.health <= game.gameEnemyStats[it]!!.maxHealh.calculatePercentage(75.0) ) {
                    i++
                }
            }
            if (i > 1) {
                game.gameEnemy.forEach {
                    game.gameEnemyStats[it]!!.heal(3, enemy.enemy)
                }
            } else {
                val target = game.playerRandomTarget()

                game.gamePlayerStats[target]?.damage(4, enemy.enemy)
            }
        } else {
            val target = game.playerRandomTarget()

            game.gamePlayerStats[target]?.damage(3, enemy.enemy)
        }
    }

    fun dead(enemy: EnemyStats) {
        val game = Info().getGame() ?: return

        game.gameEnemy.forEach {
            TurnCount(it, 2, "WolfDeadSpeedUpTag", "속도 1 증가").countStart()
            game.gameEnemyStats[it]!!.relativeSpeed += 1
        }
    }
}