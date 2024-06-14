package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.game.StatusEffect
import org.beobma.projectturn.game.StatusEffectType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.defense
import org.beobma.projectturn.info.Setup.Companion.movement
import org.beobma.projectturn.text.TextManager.Companion.machineNestingText
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GearFactory {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        game.gamePlayerStats[player]?.useCard(item, Card(
            "톱니 공장",
            listOf("${ChatColor.GRAY}자신의 턴 시작마다 ${machineNestingText()} 2를 얻습니다."),
            RarityType.Uncommon,
            2
        ))
        Info.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F) //수정 필요
        game.gamePlayerStats[player]?.statusEffect?.add(StatusEffect(StatusEffectType.AutoMachineNesting, 2, 99999, player, game.gameEnemy.random(), false))
    }
}