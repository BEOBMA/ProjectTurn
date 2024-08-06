package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.text.TextManager.Companion.burnText
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class FlameSwirl {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return

        game.gameEnemy.forEach { entity ->

            game.gameEnemyStats[entity]!!.damage(game.gameEnemyStats[entity]!!.getBurn() * 2, player)
        }

        game.gamePlayerStats[player]?.useCard(item, Card("화염 소용돌이", listOf("${ChatColor.GRAY}모든 적에게 적의 (${burnText()} x 2) 피해를 입힙니다."), RarityType.Legend, 2))
        Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) //사운드 수정
    }
}