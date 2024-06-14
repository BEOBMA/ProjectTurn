@file:Suppress("DEPRECATION")

package org.beobma.projectturn.command

import net.md_5.bungee.api.ChatColor
import org.beobma.projectturn.card.Card
import org.beobma.projectturn.game.Difficulty
import org.beobma.projectturn.game.GameType
import org.beobma.projectturn.info.GameInfoType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.DevelopersPowers
import org.beobma.projectturn.info.Setup.Companion.cardAllList
import org.beobma.projectturn.localization.Dictionary
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.util.*

class Commando : Listener, CommandExecutor, TabCompleter {
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (cmd.name.equals("pt", ignoreCase = true) && args.isNotEmpty()) {
            if (sender !is Player) {
                sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이 명령어는 플레이어만 사용할 수 있습니다.")
                return false
            }

            val commandManager = Commands()

            when (args[0].lowercase(Locale.getDefault())) {
                "start" -> {
                    if (!sender.isOp) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이 명령어를 사용할 권한이 없습니다.")
                        return false
                    }

                    if (args.size < 3) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 올바른 인수를 제공해 주세요.")
                        return false
                    }

                    val gameType = GameType.entries.find { it.name.equals(args[1], ignoreCase = true) }
                    val gameDifficulty = Difficulty.entries.find { it.name.equals(args[2], ignoreCase = true) }

                    if (gameType == null || gameDifficulty == null) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 유효한 게임 타입이나 난이도를 입력해 주세요.")
                        return false
                    }

                    val world = Bukkit.getWorld("world")
                    if (world?.seed != 8971917449433682803) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이 플러그인은 전용 맵과 함께 사용해야 합니다.")
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 게임을 강제로 종료합니다.")
                        return true
                    }

                    commandManager.run {
                        sender.gameStart(Bukkit.getOnlinePlayers().toList(), gameType, gameDifficulty)
                    }
                    return true
                }

                "stop" -> {
                    if (!sender.isOp) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 이 명령어를 사용할 권한이 없습니다.")
                        return false
                    }
                    commandManager.run { sender.gameStop() }
                    return true
                }

                "dictionary", "사전" -> {
                    if (args.size < 2) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 사전에서 검색할 용어를 제공해 주세요.")
                        return false
                    }

                    val key = args.drop(1).joinToString(" ").trim()
                    val dictionaryList = Dictionary().dictionaryList

                    val definition = dictionaryList[key]
                    if (definition != null) {
                        sender.sendMessage(definition)
                    } else {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 용어 '$key' 가 사전에 없습니다.")
                    }
                    return true
                }

                "getCard" -> {
                    if (args.size < 2) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 필수 인수가 누락되었습니다.")
                        return false
                    }
                    val game = Info().getGame() ?: run {
                        return false
                    }

                    val key = args.drop(1).joinToString(" ").trim()

                    val card = cardAllList.keys.find { it.name.trim() == key }
                    if (card !is Card) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 해당 이름을 가진 카드가 존재하지 않습니다.")
                        return false
                    }
                    game.gamePlayerStats[sender]?.addCard(card)
                }

                "info", "정보" -> {
                    val game = Info().getGame() ?: run {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 활성화된 게임이 없습니다.")
                        return false
                    }

                    if (sender !in game.players) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 현재 게임에 참여하고 있지 않습니다.")
                        return false
                    }
                    if (args.size != 2) {
                        sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 하위 명령어를 제공해 주세요 (deck(덱), cemetery(묘지), except(제외)).")
                        return false
                    }

                    when (args[1].lowercase(Locale.getDefault())) {
                        "deck", "덱" -> game.gamePlayerStats[sender]?.deckCheckToInventory()
                        "cemetery", "묘지" -> game.gamePlayerStats[sender]?.cemeteryCheckToInventory()
                        "except", "제외" -> game.gamePlayerStats[sender]?.exceptCheckToInventory()
                        "relics", "유물" -> game.gamePlayerStats[sender]?.relicsCheckToInventory()
                        else -> {
                            sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 알 수 없는 하위 명령어: ${args[1]}.")
                            return false
                        }
                    }
                    return true
                }

                else -> {
                    sender.sendMessage("${ChatColor.RED}${ChatColor.BOLD}[!] 알 수 없는 명령어: ${args[0]}.")
                    return false
                }
            }
        }
        return false
    }

    override fun onTabComplete(
        sender: CommandSender, command: Command, alias: String, args: Array<String>
    ): List<String> {
        if (command.name.equals("pt", ignoreCase = true)) {
            return when (args.size) {
                1 -> listOf("start", "stop", "dictionary", "info", "정보", "사전")
                2 -> when (args[0].lowercase(Locale.getDefault())) {
                    "start" -> GameType.entries.map { it.name }
                    "dictionary", "사전" -> Dictionary().dictionaryList.keys.toList()
                    "info", "정보" -> listOf("deck", "덱", "cemetery", "묘지", "except", "제외", "relics", "유물")
                    else -> emptyList()
                }
                3 -> if (args[0].equals("start", ignoreCase = true)) Difficulty.entries.map { it.name } else emptyList()
                else -> emptyList()
            }
        }
        return emptyList()
    }
}
