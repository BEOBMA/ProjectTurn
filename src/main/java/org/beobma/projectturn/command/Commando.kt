@file:Suppress("DEPRECATION")

package org.beobma.projectturn.command

import net.md_5.bungee.api.ChatColor
import org.beobma.projectturn.game.Difficulty
import org.beobma.projectturn.game.GameType
import org.beobma.projectturn.info.GameInfoType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Info.Companion.gaming
import org.beobma.projectturn.info.Info.Companion.starting
import org.beobma.projectturn.info.Setup.Companion.DevelopersPowers
import org.beobma.projectturn.localization.Dictionary
import org.beobma.projectturn.text.TextManager
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

@Suppress("DEPRECATION")
class Commando : Listener, CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (cmd.name.equals("pt", ignoreCase = true) && args.isNotEmpty()) {
            if (sender !is Player) return false
            when (args[0].lowercase(Locale.getDefault())) {
                "start" -> {
                    if (!sender.isOp) return false
                    val commandManager = Commands()
                    val players = Bukkit.getOnlinePlayers()
                    val gameType = GameType.entries.find { it.name.equals(args[1], ignoreCase = true) }
                    if (gameType == null) return false
                    val gameDifficulty = Difficulty.entries.find { it.name.equals(args[2], ignoreCase = true) }
                    if (gameDifficulty == null) return false
                    val world = Bukkit.getServer().getWorld("world")
                    if (world?.seed != 8971917449433682803) {
                        sender.sendMessage("${org.bukkit.ChatColor.RED}${org.bukkit.ChatColor.BOLD}[!] 해당 플러그인은 전용 맵과 함께 작동해야 합니다.")
                        sender.sendMessage("${org.bukkit.ChatColor.RED}${org.bukkit.ChatColor.BOLD}[!] 게임을 강제로 종료합니다.")
                        return true
                    }

                    commandManager.run {
                        sender.gameStart(players.toList(), gameType, gameDifficulty)
                    }
                }

                "stop" -> {
                    if (!sender.isOp) return false
                    val commandManager = Commands()

                    commandManager.run {
                        sender.gameStop()
                    }
                }

                "dictionary", "사전" -> {
                    val commandManager = Commands()
                    val key = args.drop(1)
                        .filter { it.isNotEmpty() }
                        .joinToString(separator = " ")

                    Dictionary().dictionaryList.keys.forEach {
                        if (it.trim() == key.trim()) {
                            sender.sendMessage(Dictionary().dictionaryList[it]!!)
                        }
                    }
                }

                "cheat" -> {
                    val game = Info().getGame() ?: return false

                    if (sender !in game.players) return false
                    if (Info().getGameInfo() != GameInfoType.IsBattle && Info().getGameInfo() != GameInfoType.IsHardBattle) return false
                    game.gamePlayerStats[sender]?.addCard(DevelopersPowers)
                }

                "info", "정보" -> {
                    val game = Info().getGame() ?: return false

                    if (sender !in game.players) return false
                    if (args.size != 2) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 하위 명령어가 필요합니다.")
                        return false
                    }
                    when(args[1]) {
                        "deck", "덱"  -> {
                            game.gamePlayerStats[sender]?.deckCheckToInventory()
                        }
                        "cemetry", "묘지" -> {
                            game.gamePlayerStats[sender]?.cemetryCheckToInventory()
                        }
                        "except", "제외" -> {
                            game.gamePlayerStats[sender]?.exceptCheckToInventory()
                        }
                    }
                }

                else -> {
                    sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 알 수 없는 명령어입니다.")
                    return false
                }
            }
        }
        return false
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<String>
    ): List<String> {
        if (args.size == 1 && command.name.equals("pt", ignoreCase = true)) {
            return listOf("start", "stop", "dictionary", "info", "정보", "사전")
        }
        else if (args.size == 2 && args[0].equals("start", ignoreCase = true)) {
            return GameType.entries.map { it.name }
        }
        else if (args.size == 2 && args[0].equals("dictionary", ignoreCase = true)) {
            return Dictionary().dictionaryList.keys.toList()
        }
        else if (args.size == 2 && args[0].equals("사전", ignoreCase = true)) {
            return Dictionary().dictionaryList.keys.toList()
        }
        else if (args.size == 3 && args[0].equals("start", ignoreCase = true)) {
            return Difficulty.entries.map { it.name }
        }
        else if (args.size == 2 && (args[0].equals("info", ignoreCase = true))) {
            return listOf("deck", "덱", "cemetry", "묘지", "except", "제외")
        }
        else if (args.size == 2 && (args[0].equals("정보", ignoreCase = true))) {
            return listOf("deck", "덱", "cemetry", "묘지", "except", "제외")
        }
        return emptyList()
    }
}