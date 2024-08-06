package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.attack
import org.beobma.projectturn.text.TextManager.Companion.burnText
import org.beobma.projectturn.text.TextManager.Companion.machineNestingText
import org.beobma.projectturn.text.TextManager.Companion.manaText
import org.beobma.projectturn.text.TextManager.Companion.sameCardDisappearsText
import org.beobma.projectturn.text.TextManager.Companion.targetingFailSound
import org.beobma.projectturn.text.TextManager.Companion.targetingFailText
import org.beobma.projectturn.text.TextManager.Companion.torsionText
import org.beobma.projectturn.util.Utill
import org.beobma.projectturn.util.Utill.Companion.isTeam
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BlackHole {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return


        game.gamePlayerStats[player]?.useCard(
            item, Card(
                "블랙홀",
                listOf(sameCardDisappearsText(), "${ChatColor.GRAY}모든 적에게 ${torsionText()} 5를 부여합니다."),
                RarityType.Legend,
                5
            )
        )
        game.gameEnemy.forEach {
            game.gameEnemyStats[it]!!.addTorsion(5, player)
        }
        Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) // 수정 필요
    }
}