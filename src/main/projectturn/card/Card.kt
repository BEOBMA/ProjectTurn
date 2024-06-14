@file:Suppress("DEPRECATION")

package org.beobma.projectturn.card

import org.beobma.projectturn.card.RarityType.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

@Suppress("DEPRECATION")
data class Card(
    val name: String,
    val description: List<String>,
    val rarity: RarityType,
    val cost: Int
) {
    fun toItem(): ItemStack {
        val displayName = when (rarity) {
            Common -> "${ChatColor.WHITE}${ChatColor.BOLD}$name | ($cost)"
            Uncommon -> "${ChatColor.BLUE}${ChatColor.BOLD}$name | ($cost)"
            Rare -> "${ChatColor.GREEN}${ChatColor.BOLD}$name | ($cost)"
            Legend -> "${ChatColor.YELLOW}${ChatColor.BOLD}$name | ($cost)"
        }

        val cardItem = ItemStack(Material.SENTRY_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
        val meta = cardItem.itemMeta.apply {
            setDisplayName(displayName)
            lore = description
            addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        }
        cardItem.itemMeta = meta

        return cardItem
    }
}

enum class RarityType {
    Common, Uncommon, Rare, Legend
}
