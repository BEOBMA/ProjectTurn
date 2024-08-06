@file:Suppress("DEPRECATION")

package org.beobma.projectturn.text

import org.beobma.projectturn.info.Info
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player

class TextManager {
    fun boldText(i: String): String {
        return "${ChatColor.BOLD}${i}${ChatColor.RESET}"
    }

    fun gameNotification(i: String) {
        val game = Info().getGame() ?: return

        game.players.forEach {
            it.sendMessage(i)
        }
    }

    companion object{
        fun targetingFailText(): String {
            return "${org.bukkit.ChatColor.RED}${org.bukkit.ChatColor.BOLD}[!] 카드의 효과와 바라보는 대상의 효과 관계가 비정상적입니다."
        }

        fun manaText(): String {
            return "${ChatColor.BLUE}${ChatColor.BOLD}마나${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun remnantText(): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}잔존${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun extinctionText(): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}소멸${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun sameCardDisappearsText(): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}동일 카드 소멸${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun burnText(): String {
            return "${ChatColor.RED}${ChatColor.BOLD}화상${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun fireShieldText(): String {
            return "${ChatColor.RED}${ChatColor.BOLD}화염 방어막${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun bleedingText(): String {
            return "${ChatColor.DARK_RED}${ChatColor.BOLD}출혈${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun excessiveBleedingText(): String {
            return "${ChatColor.DARK_RED}${ChatColor.BOLD}과다 출혈${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun machineNestingText(): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}기계 중첩${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun powerUpText(): String {
            return "${ChatColor.GOLD}${ChatColor.BOLD}기본 위력 증가${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun torsionText(): String {
            return "${ChatColor.LIGHT_PURPLE}${ChatColor.BOLD}비틀림${ChatColor.RESET}${ChatColor.GRAY}"
        }

        fun Player.targetingFailSound() {
            player!!.playSound(player!!.location, Sound.BLOCK_NOTE_BLOCK_BASS, 1.0F, 0.5F)
        }
    }
}