package org.beobma.projectturn.enemy

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats

class Chicken {
    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return

        if (enemy.health <= 5.0) {
            val target = game.players.random()

            game.gamePlayerStats[target]?.damage(4, enemy.enemy)
        } else if (enemy.defense < 5) {
            enemy.addDefense(5)
        } else {
            val target = game.players.random()

            game.gamePlayerStats[target]?.damage(2, enemy.enemy)
        }
    }
}