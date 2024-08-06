package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.info.Setup.Companion.attack
import org.beobma.projectturn.text.TextManager.Companion.burnText
import org.beobma.projectturn.text.TextManager.Companion.powerUpText
import org.beobma.projectturn.text.TextManager.Companion.targetingFailSound
import org.beobma.projectturn.text.TextManager.Companion.targetingFailText
import org.beobma.projectturn.util.Utill
import org.beobma.projectturn.util.Utill.Companion.isTeam
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BurningRevenge {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return

        game.gamePlayerStats[player]!!.basicPower += 1
        game.gamePlayerStats[player]?.battleEndFunctionList?.add { game.gamePlayerStats[player]!!.basicPower -= 1  }
        game.gamePlayerStats[player]?.useCard(item, Card("불타는 복수", listOf("${ChatColor.GRAY}이번 전투에서 ${powerUpText()} 1을 얻습니다."), RarityType.Rare, 0))
        Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) //사운드 수정
    }
}