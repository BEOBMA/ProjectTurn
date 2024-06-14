package org.beobma.projectturn.gameevent.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.game.Game
import org.beobma.projectturn.game.GameManager
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.cardPackList
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class PiggyBank {
    private val yes = ItemStack(Material.GREEN_CONCRETE, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GREEN}저금한다.")
            lore = listOf(
                "${ChatColor.GRAY}다음 번 이 이벤트가 발생하면 유물 보상을 얻는다."
            )
        }
    }
    private val no = ItemStack(Material.RED_CONCRETE, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.RED}저금하지 않는다.")
            lore = listOf(
                "${ChatColor.GRAY}카드 보상을 얻는다."
            )
        }
    }
    private val chaos = ItemStack(Material.GOLD_INGOT, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}저금통")
            lore = listOf(
                "${ChatColor.GRAY}모든 플레이어는 이하의 선택지 중 하나를 선택해야 한다."
            )
        }
    }

    fun start() {
        val game = Info().getGame() ?: return
        val inventory: Inventory = Bukkit.createInventory(null, 27, "저금통")
        inventory.setItem(4, chaos)

        inventory.setItem(11, yes)
        inventory.setItem(15, no)

        game.eventInventory = inventory
        game.players.forEach {
            if (it.scoreboardTags.contains("piggybank_stack")) {
                it.scoreboardTags.remove("piggybank_stack")

                GameManager().run {
                    it.relicsReward()
                }
            }
            else {
                it.openInventory(inventory)
                it.scoreboardTags.add("event")
                it.scoreboardTags.add("piggybank")
            }
        }
    }

    fun choice(clickItem: ItemStack, player: Player, game: Game) {
        if (clickItem == chaos) return
        if (clickItem == yes) {
            player.scoreboardTags.add("piggybank_stack")

        } else if (clickItem == no) {
            GameManager().run {
                player.normalReward()
            }
        }
        player.scoreboardTags.remove("piggybank")
        if (game.players.none { it.scoreboardTags.contains("event") }) {
            if (game.players.none { it.scoreboardTags.contains("rewardChose") }) {
                GameManager().mapChose()
                game.eventInventory = null
            }
        }
    }
}