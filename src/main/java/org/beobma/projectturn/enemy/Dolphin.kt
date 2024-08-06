package org.beobma.projectturn.enemy

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats
import org.beobma.projectturn.util.Utill.Companion.calculatePercentage

class Dolphin {
    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return

        game.gameEnemyStats.forEach {
            it.value.heal(10, enemy.enemy)
        }
    }
}