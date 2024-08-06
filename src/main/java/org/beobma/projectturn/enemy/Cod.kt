package org.beobma.projectturn.enemy

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats

class Cod {
    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return


        val target = game.playerRandomTarget()

        game.gamePlayerStats[target]?.damage(5, enemy.enemy)
    }
}