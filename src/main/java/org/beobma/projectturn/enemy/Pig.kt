package org.beobma.projectturn.enemy

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats

class Pig {
    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return


        val target = game.playerRandomTarget()

        game.gamePlayerStats[target]?.damage(2, enemy.enemy)
    }
}