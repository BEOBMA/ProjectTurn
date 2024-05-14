package org.beobma.projectturn.enemy

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats
import org.beobma.projectturn.util.Utill.Companion.calculatePercentage

class Cow {

    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return

        if (enemy.health <= enemy.maxHealh.calculatePercentage(25.0)) {
            enemy.heal(5, enemy.enemy)
        } else {
            val target = game.playerRandomTarget()

            game.gamePlayerStats[target]?.damage(3, enemy.enemy)
        }
    }
}