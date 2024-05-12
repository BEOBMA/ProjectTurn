package org.beobma.projectturn.enemy

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats

class Cow {

    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return

        if (enemy.health <= 15.0) {
            enemy.heal(5, enemy.enemy)
        } else {
            val target = game.players.random()

            game.gamePlayerStats[target]?.damage(3, enemy.enemy)
        }
    }
}