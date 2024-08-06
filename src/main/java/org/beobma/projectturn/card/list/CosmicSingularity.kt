package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.attack
import org.beobma.projectturn.text.TextManager.Companion.burnText
import org.beobma.projectturn.text.TextManager.Companion.extinctionText
import org.beobma.projectturn.text.TextManager.Companion.machineNestingText
import org.beobma.projectturn.text.TextManager.Companion.powerUpText
import org.beobma.projectturn.text.TextManager.Companion.sameCardDisappearsText
import org.beobma.projectturn.text.TextManager.Companion.targetingFailSound
import org.beobma.projectturn.text.TextManager.Companion.targetingFailText
import org.beobma.projectturn.util.Utill
import org.beobma.projectturn.util.Utill.Companion.isTeam
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CosmicSingularity {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        val target = Utill().shootLaser(player)


        game.gamePlayerStats[player]?.useCard(
            item, Card(
                "우주의 특이점",
                listOf(sameCardDisappearsText(), "${ChatColor.GRAY}영구적으로 ${powerUpText()} 1을 얻습니다."),
                RarityType.Legend,
                1
            )
        )
        game.gamePlayerStats[player]?.sameCardDisappears(
            Card(
                "우주의 특이점",
                listOf(sameCardDisappearsText(), "${ChatColor.GRAY}영구적으로 ${powerUpText()} 1을 얻습니다."),
                RarityType.Legend,
                1
            )
        )
        Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) // 수정 필요
        game.gamePlayerStats[player]!!.basicPower += 1
    }
}