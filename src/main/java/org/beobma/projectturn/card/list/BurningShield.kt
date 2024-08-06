package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.defense
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BurningShield {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        game.gamePlayerStats[player]?.useCard(item, Card("불타는 보호막", listOf("${ChatColor.GRAY}7의 방어력을 얻습니다."), RarityType.Common, 1))
        Info.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F) //사운드 수정
        game.gamePlayerStats[player]?.addDefense(7)
    }
}