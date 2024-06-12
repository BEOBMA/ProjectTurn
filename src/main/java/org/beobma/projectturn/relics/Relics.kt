package org.beobma.projectturn.relics

import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

data class Relics(
    val name: String,
    val description: List<String>
){
    fun toItem(): ItemStack {
        val cardItem = ItemStack(Material.EYE_ARMOR_TRIM_SMITHING_TEMPLATE, 1)
        val meta = cardItem.itemMeta.apply {
            setDisplayName(name)
            lore = description
            addItemFlags(ItemFlag.HIDE_ITEM_SPECIFICS)
        }
        cardItem.itemMeta = meta

        return cardItem
    }
}
