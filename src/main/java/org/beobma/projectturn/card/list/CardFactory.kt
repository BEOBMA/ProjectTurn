package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.game.StatusEffect
import org.beobma.projectturn.game.StatusEffectType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.text.TextManager.Companion.sameCardDisappearsText
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class CardFactory {
    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return
        val originCard = Card(
            "카드 공장",
            listOf(sameCardDisappearsText(), "${ChatColor.GRAY}자신의 턴 시작마다 카드를 2장 추가로 뽑습니다."),
            RarityType.Legend,
            2
        )
        game.gamePlayerStats[player]?.useCard(item, originCard)
        game.gamePlayerStats[player]?.sameCardDisappears(originCard)

        Info.world.playSound(player.location, Sound.ITEM_ARMOR_EQUIP_IRON, 1.0F, 1.0F) //수정 필요
        game.gamePlayerStats[player]?.statusEffect?.add(StatusEffect(StatusEffectType.AutoCardDraw, 2, 99999, player, game.gameEnemy.random(), false))
    }
}