@file:Suppress("DEPRECATION")

package org.beobma.projectturn.command

import org.beobma.projectturn.game.Difficulty
import org.beobma.projectturn.game.Game
import org.beobma.projectturn.game.GameType
import org.beobma.projectturn.info.Info
import org.bukkit.ChatColor
import org.bukkit.entity.Player

@Suppress("DEPRECATION")
class Commands {
    fun Player.gameStart(players: List<Player>, gameType: GameType, gameDifficulty: Difficulty) {
        if (players.isEmpty()) {
            sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 참가자가 1명 이하이므로 게임을 시작할 수 없습니다.")
            return
        }

        val info = Info()
        when {
            info.isStarting() -> {
                sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이미 게임 준비중입니다.")
            }
            info.isGaming() -> {
                sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이미 게임중입니다.")
            }
            else -> {
                Game(players, gameType, gameDifficulty).start()
            }
        }
    }

    fun Player.gameStop() {
        val info = Info()
        if (!info.isStarting() && !info.isGaming()) {
            sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 게임 진행중이 아닙니다.")
            return
        }

        Info.game?.stop()
    }
}
