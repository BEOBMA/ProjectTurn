package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.text.TextManager.Companion.machineNestingText
import org.beobma.projectturn.text.TextManager.Companion.targetingFailSound
import org.beobma.projectturn.text.TextManager.Companion.targetingFailText
import org.beobma.projectturn.util.Utill
import org.beobma.projectturn.util.Utill.Companion.isTeam
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CogGear  {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        val target = Utill().shootLaser(player)

        if (target == null || target.isTeam()) {
            player.sendMessage(targetingFailText())
            player.targetingFailSound()
            return
        }
        else {
            game.gamePlayerStats[player]?.useCard(item, Card(
                "톱니바퀴",
                listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입히고 ${machineNestingText()} 1을 얻습니다."),
                RarityType.Common,
                1
            ))
            game.gamePlayerStats[player]?.addMachineNesting(1)
            Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) // 수정 필요
            game.gameEnemyStats[target]?.damage(5, player)
        }
    }
}