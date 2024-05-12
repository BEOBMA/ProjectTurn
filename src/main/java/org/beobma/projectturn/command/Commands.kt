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
        if (player == null) return
        if (players.isEmpty()) {
            player?.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 참가자가 1명 이하이므로 게임을 시작할 수 없습니다.")
            return
        }
        if (Info().isStarting()) {
            player?.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이미 게임 준비중입니다.")
            return
        }
        if (Info().isGaming()) {
            player?.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이미 게임중입니다.")
            return
        }

        Game(players, gameType, gameDifficulty).start()
    }

    fun Player.gameStop() {
        if (player == null) return

        val info = Info()
        if (!info.isStarting() && !info.isGaming()) {
            player?.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 게임 진행중이 아닙니다.")
            return
        }

        Info.game?.stop()
    }
}