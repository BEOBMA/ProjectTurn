package org.beobma.projectturn.enemy

import org.beobma.projectturn.info.Info
import org.beobma.projectturn.stats.EnemyStats
import org.beobma.projectturn.text.TextManager
import org.bukkit.ChatColor

class Creeper {
    fun action(enemy: EnemyStats) {
        val game = Info().getGame() ?: return


        if (enemy.enemy.scoreboardTags.contains("CreeperNextEx")) {
            game.players.forEach {
                game.gamePlayerStats[it]?.damage(15, enemy.enemy)
            }
            enemy.death()
        } else {
            enemy.enemy.scoreboardTags.add("CreeperNextEx")
            TextManager().gameNotification("${ChatColor.GOLD}${ChatColor.BOLD}다음 턴에 ${enemy.name}이(가) 자폭합니다.")
        }
    }
}