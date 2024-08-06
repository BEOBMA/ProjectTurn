package org.beobma.projectturn.enemy

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats
import org.beobma.projectturn.util.Utill.Companion.calculatePercentage

class Squid {
    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return


        if (enemy.health < enemy.maxHealh.calculatePercentage(50.0)) {
            game.players.forEach {
                game.gamePlayerStats[it]?.damage(14, enemy.enemy)
            }
        }
        else {
            game.players.forEach {
                game.gamePlayerStats[it]?.damage(7, enemy.enemy)
            }
        }
    }
}