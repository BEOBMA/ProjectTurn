package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.attack
import org.beobma.projectturn.text.TextManager.Companion.burnText
import org.beobma.projectturn.text.TextManager.Companion.targetingFailSound
import org.beobma.projectturn.text.TextManager.Companion.targetingFailText
import org.beobma.projectturn.util.Utill
import org.beobma.projectturn.util.Utill.Companion.isTeam
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class LavaEruption {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return

        game.gameEnemy.forEach {
            game.gameEnemyStats[it]?.addBurn(6, player)
        }

        game.gamePlayerStats[player]?.useCard(item, Card("용암 분출", listOf("${ChatColor.GRAY}모든 적에게 ${burnText()} 6를 부여합니다."), RarityType.Uncommon, 2))
        Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) //사운드 수정
    }
}