package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.defense
import org.beobma.projectturn.info.Setup.Companion.movement
import org.beobma.projectturn.text.TextManager.Companion.machineNestingText
import org.beobma.projectturn.text.TextManager.Companion.manaText
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ElectricWire {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        game.gamePlayerStats[player]?.useCard(item, Card("전깃줄", listOf("${manaText()}를 1 회복하고 ${machineNestingText()} 3을 얻습니다."), RarityType.Common, 1))
        Info.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F) // 수정 필요
        game.gamePlayerStats[player]?.addMana(1)
        game.gamePlayerStats[player]?.addMachineNesting(3)
    }
}