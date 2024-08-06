package org.beobma.projectturn.card.list

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.CardPack
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.info.Info
import org.beobma.projectturn.text.TextManager.Companion.burnText
import org.beobma.projectturn.text.TextManager.Companion.extinctionText
import org.beobma.projectturn.text.TextManager.Companion.manaText
import org.beobma.projectturn.text.TextManager.Companion.sameCardDisappearsText
import org.beobma.projectturn.util.Utill
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class Neptune {
    private val BirthOfTheUniverse = CardPack(
        "우주의 탄생", listOf("다양한 효과를 가진 카드들의 집합입니다. 다른 일부 카드들을 지원하는 카드들이 대거 등장합니다."), mutableListOf(
            Card(
                "태양",
                listOf("${ChatColor.GRAY}바라보는 적에게 3의 피해를 입히고 적 전체에게 ${burnText()} 5를 부여합니다."),
                RarityType.Common,
                2
            ),
            Card(
                "태양",
                listOf("${ChatColor.GRAY}바라보는 적에게 3의 피해를 입히고 적 전체에게 ${burnText()} 5를 부여합니다."),
                RarityType.Common,
                2
            ),
            Card(
                "태양",
                listOf("${ChatColor.GRAY}바라보는 적에게 3의 피해를 입히고 적 전체에게 ${burnText()} 5를 부여합니다."),
                RarityType.Common,
                2
            ),
            Card("수성", listOf("${ChatColor.GRAY}바라보는 적에게 9의 피해를 입힙니다."), RarityType.Common, 1),
            Card("수성", listOf("${ChatColor.GRAY}바라보는 적에게 9의 피해를 입힙니다."), RarityType.Common, 1),
            Card("수성", listOf("${ChatColor.GRAY}바라보는 적에게 9의 피해를 입힙니다."), RarityType.Common, 1),
            Card(
                "금성",
                listOf("${ChatColor.GRAY}바라보는 적에게 4의 피해를 입히고 ${manaText()}를 1 회복합니다."),
                RarityType.Common,
                0
            ),
            Card(
                "금성",
                listOf("${ChatColor.GRAY}바라보는 적에게 4의 피해를 입히고 ${manaText()}를 1 회복합니다."),
                RarityType.Common,
                0
            ),
            Card(
                "금성",
                listOf("${ChatColor.GRAY}바라보는 적에게 4의 피해를 입히고 ${manaText()}를 1 회복합니다."),
                RarityType.Common,
                0
            ),
            Card("지구", listOf("${ChatColor.GRAY}8의 방어력을 얻고 카드를 1장 뽑습니다."), RarityType.Common, 1),
            Card("지구", listOf("${ChatColor.GRAY}8의 방어력을 얻고 카드를 1장 뽑습니다."), RarityType.Common, 1),
            Card("지구", listOf("${ChatColor.GRAY}8의 방어력을 얻고 카드를 1장 뽑습니다."), RarityType.Common, 1),
            Card(
                "화성",
                listOf("${ChatColor.GRAY}바라보는 적에게 6의 피해를 입히고 ${manaText()}를 2 회복합니다."),
                RarityType.Uncommon,
                1
            ),
            Card(
                "화성",
                listOf("${ChatColor.GRAY}바라보는 적에게 6의 피해를 입히고 ${manaText()}를 2 회복합니다."),
                RarityType.Uncommon,
                1
            ),
            Card(
                "목성",
                listOf("${ChatColor.GRAY}바라보는 적에게 15의 피해를 입히고 대상의 남은 체력이 10 미만이면 제거합니다."),
                RarityType.Uncommon,
                2
            ),
            Card(
                "목성",
                listOf("${ChatColor.GRAY}바라보는 적에게 15의 피해를 입히고 대상의 남은 체력이 10 미만이면 제거합니다."),
                RarityType.Uncommon,
                2
            ),
            Card("토성", listOf("${ChatColor.GRAY}묘지의 카드중 무작위로 2장을 패에 넣습니다."), RarityType.Uncommon, 1),
            Card("토성", listOf("${ChatColor.GRAY}묘지의 카드중 무작위로 2장을 패에 넣습니다."), RarityType.Uncommon, 1),
            Card("천왕성", listOf("${ChatColor.GRAY}자신의 체력을 4 만큼 회복합니다."), RarityType.Uncommon, 2),
            Card("천왕성", listOf("${ChatColor.GRAY}자신의 체력을 4 만큼 회복합니다."), RarityType.Uncommon, 2),
            Card(
                "해왕성",
                listOf("${ChatColor.GRAY}덱에서 '우주의 탄생' 카드 팩에 존재하는 카드 1장을 고르고 패에 넣는다."),
                RarityType.Uncommon,
                0
            ),
            Card(
                "해왕성",
                listOf("${ChatColor.GRAY}덱에서 '우주의 탄생' 카드 팩에 존재하는 카드 1장을 고르고 패에 넣는다."),
                RarityType.Uncommon,
                0
            ),
            Card(
                "명왕성",
                listOf(extinctionText(), "${ChatColor.GRAY}바라보는 적에게 12의 피해를 입히고 덱에서 '명왕성' 카드 1장을 고르고 패에 넣는다."),
                RarityType.Common,
                1
            ),
            Card(
                "명왕성",
                listOf(extinctionText(), "${ChatColor.GRAY}바라보는 적에게 12의 피해를 입히고 덱에서 '명왕성' 카드 1장을 고르고 패에 넣는다."),
                RarityType.Common,
                1
            ),
            Card(
                "명왕성",
                listOf(extinctionText(), "${ChatColor.GRAY}바라보는 적에게 12의 피해를 입히고 덱에서 '명왕성' 카드 1장을 고르고 패에 넣는다."),
                RarityType.Common,
                1
            ),
            Card(
                "빅뱅",
                listOf(extinctionText(), "${ChatColor.GRAY}바라보는 적에게 (묘지에 존재하는 '우주의 탄생' 카드의 수 x 3) 만큼의 피해를 입힙니다."),
                RarityType.Legend,
                3
            ),
            Card(
                "우주의 특이점",
                listOf(sameCardDisappearsText(), "${ChatColor.GRAY}'우주의 탄생' 카드 팩에 존재하는 카드의 위력이 영구적으로 1 증가합니다."),
                RarityType.Legend,
                1
            ),
        )
    )

    fun using(player: Player, item: ItemStack) {
        val game = Info().getGame() ?: return

        game.gamePlayerStats[player]?.useCard(
            item, Card(
                "해왕성",
                listOf("${ChatColor.GRAY}덱에서 '우주의 탄생' 카드 팩에 존재하는 카드 1장을 고르고 패에 넣는다."),
                RarityType.Uncommon,
                0
            )
        )
        Info.world.playSound(player.location, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F) // 수정 필요

        var i = 0
        val cardList: MutableList<Card> = mutableListOf()
        game.gamePlayerStats[player]!!.deck.forEach {
            if (it in BirthOfTheUniverse.cardList) {
                cardList.add(it)
            }
        }
        if (cardList.isEmpty()) {
            return
        }

        game.gamePlayerStats[player]?.deckCheck(cardList)
    }
}