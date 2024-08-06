@file:Suppress("DEPRECATION")

package org.beobma.projectturn.localization

import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Localization {
    val nullPane = ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}비어 있음")
        }
    }

    val previousPage = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}이전 페이지")
            lore = arrayListOf(
                "${ChatColor.GRAY}현재 보유한 카드가 인벤토리 칸보다 많을 경우",
                "${ChatColor.GRAY} 다음 페이지 또는 이전 페이지를 볼 수 있습니다."
            )
        }
    }

    val nextPage = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}다음 페이지")
            lore = arrayListOf(
                "${ChatColor.GRAY}현재 보유한 카드가 인벤토리 칸보다 많을 경우",
                "${ChatColor.GRAY} 다음 페이지 또는 이전 페이지를 볼 수 있습니다."
            )
        }
    }

    val previousPageChest = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}이전 페이지")
            lore = arrayListOf(
                "${ChatColor.GRAY}현재 보유한 카드가 상자 칸보다 많을 경우",
                "${ChatColor.GRAY} 다음 페이지 또는 이전 페이지를 볼 수 있습니다."
            )
        }
    }

    val nextPageChest = ItemStack(Material.PAPER, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}다음 페이지")
            lore = arrayListOf(
                "${ChatColor.GRAY}현재 보유한 카드가 상자 칸보다 많을 경우",
                "${ChatColor.GRAY} 다음 페이지 또는 이전 페이지를 볼 수 있습니다."
            )
        }
    }

    val startTile = ItemStack(Material.GOLD_BLOCK, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}시작")
            lore = arrayListOf(
                "${ChatColor.BOLD}${ChatColor.GRAY}여정을 시작하는 칸입니다."
            )
        }
    }

    val battleTile = ItemStack(Material.RED_CONCRETE, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}전투")
            lore = arrayListOf(
                "${ChatColor.BOLD}${ChatColor.GRAY}일반적인 적이 등장하는 칸입니다."
            )
        }
    }

    val hardBattleTile = ItemStack(Material.BLACK_GLAZED_TERRACOTTA, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}강적 조우")
            lore = arrayListOf(
                "${ChatColor.BOLD}${ChatColor.GRAY}강적이 등장하는 칸입니다."
            )
        }
    }

    val eventTile = ItemStack(Material.BLACK_GLAZED_TERRACOTTA, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}이벤트")
            lore = arrayListOf(
                "${ChatColor.BOLD}${ChatColor.GRAY}무작위 이벤트가 등장하는 칸입니다."
            )
        }
    }

    val restTile = ItemStack(Material.GREEN_CONCRETE, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}휴식")
            lore = arrayListOf(
                "${ChatColor.BOLD}${ChatColor.GRAY}체력을 회복하며 쉬어가는 칸입니다."
            )
        }
    }

    val bossTile = ItemStack(Material.RED_GLAZED_TERRACOTTA, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}보스")
            lore = arrayListOf(
                "${ChatColor.BOLD}${ChatColor.GRAY}해당 구역의 보스와 전투하는 칸입니다."
            )
        }
    }

    val endTile = ItemStack(Material.EMERALD_BLOCK, 1).apply {
        itemMeta = itemMeta.apply {
            setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}끝")
            lore = arrayListOf(
                "${ChatColor.BOLD}${ChatColor.GRAY}이 칸에 도착하면 다음 지역으로 이동합니다."
            )
        }
    }
}