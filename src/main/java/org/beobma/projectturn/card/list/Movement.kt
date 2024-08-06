package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.defense
import org.beobma.projectturn.info.Setup.Companion.movement
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Movement {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        game.gamePlayerStats[player]?.useCard(item, movement)
        Info.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F)
        game.gamePlayerStats[player]?.addMana(1)
    }
}