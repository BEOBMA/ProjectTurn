package org.beobma.projectturn.enemy

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats

class Skeleton {
    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return

        if (enemy.defense > 10) {
            val target = game.playerRandomTarget()

            game.gamePlayerStats[target]?.damage(7, enemy.enemy)
        }
        else {
            game.gameEnemy.forEach {
                game.gameEnemyStats[it]!!.addDefense(10)
            }
        }
    }
}