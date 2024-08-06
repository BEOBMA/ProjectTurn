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

class Chaos {
    private val yes = ItemStack(Material.GREEN_CONCRETE, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GREEN}나는 혼돈을 좋아한다.")
            lore = listOf(
                "${ChatColor.GRAY}자신의 덱에 존재하는 모든 카드를 완전 무작위 카드로 변경한다."
            )
        }
    }
    private val no = ItemStack(Material.RED_CONCRETE, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.RED}나는 혼돈을 싫어한다.")
            lore = listOf(
                "${ChatColor.GRAY}아무 효과도 없다."
            )
        }
    }
    private val chaos = ItemStack(Material.SCULK_SENSOR, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}대혼돈")
            lore = listOf(
                "${ChatColor.GRAY}모든 플레이어는 이하의 선택지 중 하나를 선택해야 한다."
            )
        }
    }

    fun start() {
        val game = Info().getGame() ?: return
        val inventory: Inventory = Bukkit.createInventory(null, 27, "대혼돈")
        inventory.setItem(4, chaos)

        inventory.setItem(11, yes)
        inventory.setItem(15, no)

        game.eventInventory = inventory
        game.players.forEach {
            it.openInventory(inventory)
            it.scoreboardTags.add("event")
            it.scoreboardTags.add("chaos")
        }
    }

    fun choice(clickItem: ItemStack, player: Player, game: Game) {
        if (clickItem == chaos) return
        if (clickItem == yes) {
            val cardDummyList:MutableList<Card> = mutableListOf()

            game.gamePlayerStats[player]?.deck?.forEach { _ ->
                cardDummyList.add(cardPackList.random().cardList.random())
            }

            game.gamePlayerStats[player]?.deck?.clear()
            game.gamePlayerStats[player]?.deck = cardDummyList

        }
        player.closeInventory()
        player.scoreboardTags.remove("event")
        player.scoreboardTags.remove("chaos")
        if (game.players.none { it.scoreboardTags.contains("event") }) {
            GameManager().mapChose()
            game.eventInventory = null
        }
    }
}