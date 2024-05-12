@file:Suppress("DEPRECATION")

package org.beobma.projectturn.card

import io.papermc.paper.inventory.ItemRarity
import org.beobma.projectturn.card.RarityType.*
import org.beobma.projectturn.info.Setup
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.util.ChatPaginator.ChatPage

@Suppress("DEPRECATION")
data class Card(
    val name: String, val description: List<String>, val rarity: RarityType, val cost: Int
) {
    fun toItem(): ItemStack {
        val rarity = rarity
        when (rarity) {
            Common -> {
                val cardItem = ItemStack(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 1).apply {
                    itemMeta = itemMeta.apply {
                        setDisplayName("${ChatColor.WHITE}${ChatColor.BOLD}$name | ($cost)")
                    }
                    lore = description
                    addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)

                }
                return cardItem
            }

            Uncommon -> {
                val cardItem = ItemStack(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 1).apply {
                    itemMeta = itemMeta.apply {
                        setDisplayName("${ChatColor.BLUE}${ChatColor.BOLD}$name | ($cost)")
                    }
                    lore = description
                    addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)

                }
                return cardItem
            }
            Rare -> {
                val cardItem = ItemStack(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 1).apply {
                    itemMeta = itemMeta.apply {
                        setDisplayName("${ChatColor.GREEN}${ChatColor.BOLD}$name | ($cost)")
                    }
                    lore = description
                    addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)

                }
                return cardItem
            }
            Legend -> {
                val cardItem = ItemStack(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 1).apply {
                    itemMeta = itemMeta.apply {
                        setDisplayName("${ChatColor.YELLOW}${ChatColor.BOLD}$name | ($cost)")
                    }
                    lore = description
                    addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
                }
                return cardItem
            }
        }
    }
}

enum class RarityType {
    Common, Uncommon, Rare, Legend
}