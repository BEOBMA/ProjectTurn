package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.game.StatusEffect
import org.beobma.projectturn.game.StatusEffectType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.text.TextManager.Companion.manaText
import org.beobma.projectturn.text.TextManager.Companion.sameCardDisappearsText
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class ManaFactory {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        val originCard = Card(
            "마나 공장",
            listOf(sameCardDisappearsText(), "${ChatColor.GRAY}영구적으로 자신의 마나 최대치가 2 증가하고, 자신의 턴 시작마다 ${manaText()}를 2 추가로 회복합니다."),
            RarityType.Legend,
            3
        )
        game.gamePlayerStats[player]?.useCard(item, originCard)
        game.gamePlayerStats[player]?.sameCardDisappears(originCard)

        Info.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F) //수정 필요
        game.gamePlayerStats[player]!!.maxMana += 2
        game.gamePlayerStats[player]?.statusEffect?.add(StatusEffect(StatusEffectType.AutoManaDraw, 2, 99999, player, game.gameEnemy.random(), false))
    }
}