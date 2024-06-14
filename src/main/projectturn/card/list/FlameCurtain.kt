package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.defense
import org.beobma.projectturn.text.TextManager.Companion.fireShieldText
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class FlameCurtain {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        game.gamePlayerStats[player]?.useCard(item, Card("화염 장막", listOf("${ChatColor.GRAY}5의 방어력을 얻고 ${fireShieldText()} 1을 얻습니다."), RarityType.Common, 1))
        Info.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F) //사운드 수정
        game.gamePlayerStats[player]?.addDefense(5)


    }
}