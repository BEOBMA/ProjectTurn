@file:Suppress("DEPRECATION")

package org.beobma.projectturn.info

import org.beobma.projectturn.card.Card
import org.beobma.projectturn.card.CardPack
import org.beobma.projectturn.card.RarityType
import org.beobma.projectturn.game.Field
import org.beobma.projectturn.gameevent.GameEvent
import org.beobma.projectturn.relics.Relics
import org.beobma.projectturn.text.TextManager.Companion.bleedingText
import org.beobma.projectturn.text.TextManager.Companion.burnText
import org.beobma.projectturn.text.TextManager.Companion.extinctionText
import org.beobma.projectturn.text.TextManager.Companion.fireShieldText
import org.beobma.projectturn.text.TextManager.Companion.machineNestingText
import org.beobma.projectturn.text.TextManager.Companion.manaText
import org.beobma.projectturn.text.TextManager.Companion.powerUpText
import org.beobma.projectturn.text.TextManager.Companion.remnantText
import org.beobma.projectturn.text.TextManager.Companion.sameCardDisappearsText
import org.beobma.projectturn.text.TextManager.Companion.torsionText
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class Setup {
    companion object {
        private val KnightOfFlame = CardPack(
            "화염의 기사", listOf("화상 키워드를 다루는 카드들이 대거 등장합니다."), mutableListOf(
                Card("용암 분출", listOf("${ChatColor.GRAY}모든 적에게 ${burnText()} 6를 부여합니다."), RarityType.Uncommon, 2),
                Card("용암 분출", listOf("${ChatColor.GRAY}모든 적에게 ${burnText()} 6를 부여합니다."), RarityType.Uncommon, 2),
                Card("불사조의 깃털", listOf("${ChatColor.GRAY}카드를 2장 뽑습니다."), RarityType.Uncommon, 1),
                Card("불사조의 깃털", listOf("${ChatColor.GRAY}카드를 2장 뽑습니다."), RarityType.Uncommon, 1),
                Card(
                    "화염 소용돌이",
                    listOf("${ChatColor.GRAY}모든 적에게 적의 (${burnText()} x 2) 피해를 입힙니다."),
                    RarityType.Legend,
                    2
                ),
                Card(
                    "휘황찬란한 화염",
                    listOf("${ChatColor.GRAY}바라보는 적에게 10의 피해를 입히고 ${burnText()} 10을 부여합니다."),
                    RarityType.Rare,
                    2
                ),
                Card(
                    "휘황찬란한 화염",
                    listOf("${ChatColor.GRAY}바라보는 적에게 10의 피해를 입히고 ${burnText()} 10을 부여합니다."),
                    RarityType.Rare,
                    2
                ),
                Card("불타는 복수", listOf("${ChatColor.GRAY}이번 전투에서 ${powerUpText()} 1을 얻습니다."), RarityType.Rare, 0),
                Card("불타는 복수", listOf("${ChatColor.GRAY}이번 전투에서 ${powerUpText()} 1을 얻습니다."), RarityType.Rare, 0),
                Card("연소", listOf("${ChatColor.GRAY}바라보는 적에게 적의 (${burnText()} x 3) 피해를 입힙니다."), RarityType.Rare, 1),
                Card("연소", listOf("${ChatColor.GRAY}바라보는 적에게 적의 (${burnText()} x 3) 피해를 입힙니다."), RarityType.Rare, 1),
                Card("불타는 보호막", listOf("${ChatColor.GRAY}7의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("불타는 보호막", listOf("${ChatColor.GRAY}7의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("불타는 보호막", listOf("${ChatColor.GRAY}7의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("화염의 숨", listOf("${manaText()}를 1 회복합니다."), RarityType.Common, 0),
                Card("화염의 숨", listOf("${manaText()}를 1 회복합니다."), RarityType.Common, 0),
                Card("화염의 숨", listOf("${manaText()}를 1 회복합니다."), RarityType.Common, 0),
                Card(
                    "화염의 손길",
                    listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입히고 ${burnText()} 3을 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "화염의 손길",
                    listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입히고 ${burnText()} 3을 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "화염의 손길",
                    listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입히고 ${burnText()} 3을 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card("폭염의 숨결", listOf("${manaText()}를 2 회복하고 ${burnText()} 1을 얻습니다."), RarityType.Common, 0),
                Card("폭염의 숨결", listOf("${manaText()}를 2 회복하고 ${burnText()} 1을 얻습니다."), RarityType.Common, 0),
                Card("폭염의 숨결", listOf("${manaText()}를 2 회복하고 ${burnText()} 1을 얻습니다."), RarityType.Common, 0),
                Card("화염 장막", listOf("${ChatColor.GRAY}5의 방어력을 얻고 ${fireShieldText()} 1을 얻습니다."), RarityType.Common, 1),
                Card("화염 장막", listOf("${ChatColor.GRAY}5의 방어력을 얻고 ${fireShieldText()} 1을 얻습니다."), RarityType.Common, 1),
                Card("화염 장막", listOf("${ChatColor.GRAY}5의 방어력을 얻고 ${fireShieldText()} 1을 얻습니다."), RarityType.Common, 1),
                Card(
                    "잿빛 폭풍",
                    listOf("${ChatColor.GRAY}바라보는 적이 보유한 ${burnText()} 만큼 모든 적에게 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "잿빛 폭풍",
                    listOf("${ChatColor.GRAY}바라보는 적이 보유한 ${burnText()} 만큼 모든 적에게 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "잿빛 폭풍",
                    listOf("${ChatColor.GRAY}바라보는 적이 보유한 ${burnText()} 만큼 모든 적에게 부여합니다."),
                    RarityType.Common,
                    1
                )
            )
        )
        private val Slaughterer = CardPack(
            "도살자", listOf("출혈 키워드를 다루는 카드들이 대거 등장합니다."), mutableListOf(
                Card(
                    "잔인한 찢김",
                    listOf("${ChatColor.GRAY}바라보는 적에게 3의 피해를 입히고 ${bleedingText()} 5를 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "잔인한 찢김",
                    listOf("${ChatColor.GRAY}바라보는 적에게 3의 피해를 입히고 ${bleedingText()} 5를 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "잔인한 찢김",
                    listOf("${ChatColor.GRAY}바라보는 적에게 3의 피해를 입히고 ${bleedingText()} 5를 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "죽음의 붉은 속삭임",
                    listOf("${ChatColor.GRAY}이번 전투동안 ${bleedingText()}을 부여할 때 3만큼 추가로 부여합니다."),
                    RarityType.Legend,
                    3
                ),
                Card("칼갈기", listOf("${manaText()}를 1 회복합니다."), RarityType.Common, 0),
                Card("칼갈기", listOf("${manaText()}를 1 회복합니다."), RarityType.Common, 0),
                Card("칼갈기", listOf("${manaText()}를 1 회복합니다."), RarityType.Common, 0),
                Card("도살 준비", listOf("${ChatColor.GRAY}카드를 2장 뽑습니다."), RarityType.Common, 0),
                Card("도살 준비", listOf("${ChatColor.GRAY}카드를 2장 뽑습니다."), RarityType.Common, 0),
                Card("도살 준비", listOf("${ChatColor.GRAY}카드를 2장 뽑습니다."), RarityType.Common, 0),
                Card(
                    "도살 시작",
                    listOf("${ChatColor.GRAY}이번 턴동안 ${bleedingText()}을 부여할 때 5만큼 추가로 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "도살 시작",
                    listOf("${ChatColor.GRAY}이번 턴동안 ${bleedingText()}을 부여할 때 5만큼 추가로 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "도살 시작",
                    listOf("${ChatColor.GRAY}이번 턴동안 ${bleedingText()}을 부여할 때 5만큼 추가로 부여합니다."),
                    RarityType.Common,
                    1
                ),
                Card("피의 보호막", listOf("${ChatColor.GRAY}7의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("피의 보호막", listOf("${ChatColor.GRAY}7의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("피의 보호막", listOf("${ChatColor.GRAY}7의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("피의 파동", listOf("${ChatColor.GRAY}모든 적에게 ${bleedingText()} 10을 부여합니다."), RarityType.Rare, 2),
                Card("피의 파동", listOf("${ChatColor.GRAY}모든 적에게 ${bleedingText()} 10을 부여합니다."), RarityType.Rare, 2),
                Card(
                    "도살",
                    listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입히고 ${bleedingText()} 7을 부여합니다."),
                    RarityType.Rare,
                    1
                ),
                Card(
                    "도살",
                    listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입히고 ${bleedingText()} 7을 부여합니다."),
                    RarityType.Rare,
                    1
                ),
            )
        )
        private val CogsAndMachines = CardPack(
            "톱니와 기계", listOf("스택을 쌓아 강해지는 카드들이 대거 등장합니다."), mutableListOf(
                Card(
                    "톱니바퀴",
                    listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입히고 ${machineNestingText()} 1을 얻습니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "톱니바퀴",
                    listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입히고 ${machineNestingText()} 1을 얻습니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "톱니바퀴",
                    listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입히고 ${machineNestingText()} 1을 얻습니다."),
                    RarityType.Common,
                    1
                ),
                Card("전깃줄", listOf("${manaText()}를 1 회복하고 ${machineNestingText()} 3을 얻습니다."), RarityType.Common, 1),
                Card("전깃줄", listOf("${manaText()}를 1 회복하고 ${machineNestingText()} 3을 얻습니다."), RarityType.Common, 1),
                Card("전깃줄", listOf("${manaText()}를 1 회복하고 ${machineNestingText()} 3을 얻습니다."), RarityType.Common, 1),
                Card(
                    "방어 기계",
                    listOf("${ChatColor.GRAY}7의 방어력을 얻고 ${machineNestingText()} 1을 얻습니다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "기어",
                    listOf("${ChatColor.GRAY}바라보는 적에게 (${machineNestingText()} 수치)의 피해를 입힙니다."),
                    RarityType.Common,
                    0
                ),
                Card(
                    "기어",
                    listOf("${ChatColor.GRAY}바라보는 적에게 (${machineNestingText()} 수치)의 피해를 입힙니다."),
                    RarityType.Common,
                    0
                ),
                Card(
                    "기어",
                    listOf("${ChatColor.GRAY}바라보는 적에게 (${machineNestingText()} 수치)의 피해를 입힙니다."),
                    RarityType.Common,
                    0
                ),
                Card(
                    "톱니 공장",
                    listOf("${ChatColor.GRAY}자신의 턴 시작마다 ${machineNestingText()} 2를 얻습니다."),
                    RarityType.Uncommon,
                    2
                ),
                Card(
                    "톱니 공장",
                    listOf("${ChatColor.GRAY}자신의 턴 시작마다 ${machineNestingText()} 2를 얻습니다."),
                    RarityType.Uncommon,
                    2
                ),
                Card(
                    "카드 공장",
                    listOf(sameCardDisappearsText(), "${ChatColor.GRAY}자신의 턴 시작마다 카드를 2장 추가로 뽑습니다."),
                    RarityType.Legend,
                    2
                ),
                Card(
                    "마나 공장",
                    listOf(sameCardDisappearsText(), "${ChatColor.GRAY}영구적으로 자신의 마나 최대치가 2 증가하고, 자신의 턴 시작마다 ${manaText()}를 2 추가로 회복합니다."),
                    RarityType.Legend,
                    3
                ),
            )
        )
        val BirthOfTheUniverse = CardPack(
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
                    listOf(extinctionText(), "${ChatColor.GRAY}바라보는 적에게 12의 피해를 입히고 덱에서 '명왕성' 카드 1장을 뽑는다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "명왕성",
                    listOf(extinctionText(), "${ChatColor.GRAY}바라보는 적에게 12의 피해를 입히고 덱에서 '명왕성' 카드 1장을 뽑는다."),
                    RarityType.Common,
                    1
                ),
                Card(
                    "명왕성",
                    listOf(extinctionText(), "${ChatColor.GRAY}바라보는 적에게 12의 피해를 입히고 덱에서 '명왕성' 카드 1장을 뽑는다."),
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
                    listOf(sameCardDisappearsText(), "${ChatColor.GRAY}영구적으로 ${powerUpText()} 1을 얻습니다."),
                    RarityType.Legend,
                    1
                ),
                Card(
                    "블랙홀",
                    listOf(sameCardDisappearsText(), "${ChatColor.GRAY}모든 적에게 ${torsionText()} 5를 부여합니다."),
                    RarityType.Legend,
                    5
                ),
            )
        )
        private val OurShield = CardPack(
            "우리의 방패", listOf("방어력을 얻는 효과를 가진 카드들이 대거 등장합니다.", "card2"), mutableListOf(
                Card("강력한 방어", listOf("${ChatColor.GRAY}15의 방어력을 얻습니다."), RarityType.Common, 2),
                Card("강력한 방어", listOf("${ChatColor.GRAY}15의 방어력을 얻습니다."), RarityType.Common, 2),
                Card("강력한 방어", listOf("${ChatColor.GRAY}15의 방어력을 얻습니다."), RarityType.Common, 2),
                Card("수비", listOf("${ChatColor.GRAY}10의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("수비", listOf("${ChatColor.GRAY}10의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("수비", listOf("${ChatColor.GRAY}10의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("방어 태세", listOf("${ChatColor.GRAY}30의 방어력을 얻습니다."), RarityType.Common, 3),
                Card("방어 태세", listOf("${ChatColor.GRAY}30의 방어력을 얻습니다."), RarityType.Common, 3),
                Card("방어 태세", listOf("${ChatColor.GRAY}30의 방어력을 얻습니다."), RarityType.Common, 3),
                Card("모두의 방패", listOf("${ChatColor.GRAY}아군 모두가 5의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("모두의 방패", listOf("${ChatColor.GRAY}아군 모두가 5의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("모두의 방패", listOf("${ChatColor.GRAY}아군 모두가 5의 방어력을 얻습니다."), RarityType.Common, 1),
                Card("모두의 방패", listOf("${ChatColor.GRAY}아군 모두가 5의 방어력을 얻습니다."), RarityType.Legend, 1),
                Card("모두의 방패", listOf("${ChatColor.GRAY}아군 모두가 5의 방어력을 얻습니다."), RarityType.Legend, 1),
            )
        )

        val cardAllList: MutableMap<Card, String> = mutableMapOf(
            Pair(Card("개발자의 권능", listOf(remnantText(), "${ChatColor.GRAY}바라보는 적을 제거합니다."), RarityType.Legend, 0), "DevelopersPowers"),
            Pair(Card("이동", listOf("${manaText()}를 1 회복합니다."), RarityType.Common, 0), "Movement"),
            Pair(Card("강공", listOf("${ChatColor.GRAY}바라보는 적에게 10의 피해를 입힙니다."), RarityType.Common, 2), "StrongAttack"),
            Pair(Card("방어", listOf("${ChatColor.GRAY}5의 방어력을 얻습니다."), RarityType.Common, 1), "Defense"),
            Pair(Card("공격", listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입힙니다."), RarityType.Common, 1), "Attack")
        )


        val attack = Card("공격", listOf("${ChatColor.GRAY}바라보는 적에게 5의 피해를 입힙니다."), RarityType.Common, 1)
        val defense = Card("방어", listOf("${ChatColor.GRAY}5의 방어력을 얻습니다."), RarityType.Common, 1)
        val strongAttack = Card("강공", listOf("${ChatColor.GRAY}바라보는 적에게 10의 피해를 입힙니다."), RarityType.Common, 2)
        val movement = Card("이동", listOf("${manaText()}를 1 회복합니다."), RarityType.Common, 0)
        val DevelopersPowers =
            Card("개발자의 권능", listOf(remnantText(), "${ChatColor.GRAY}바라보는 적을 제거합니다."), RarityType.Legend, 0)

        val forest = ItemStack(Material.OAK_LEAVES, 1).apply {
            itemMeta = itemMeta.apply {
                setDisplayName("${ChatColor.BOLD}${ChatColor.GREEN}숲")
            }
            lore = listOf("${ChatColor.BOLD}${ChatColor.GRAY}평화로운 숲 속입니다.")
        }
        val sea = ItemStack(Material.SAND, 1).apply {
            itemMeta = itemMeta.apply {
                setDisplayName("${ChatColor.BOLD}${ChatColor.BLUE}바다")
            }
            lore = listOf("${ChatColor.BOLD}${ChatColor.GRAY}마치 휴양지 같지만, 그 내면은 다릅니다.")
        }
        val cave = ItemStack(Material.STONE, 1).apply {
            itemMeta = itemMeta.apply {
                setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}동굴")
            }
            lore = listOf("${ChatColor.BOLD}${ChatColor.GRAY}동굴은 어둡고 강력한 적이 등장합니다.")
        }
        val end = ItemStack(Material.END_STONE, 1).apply {
            itemMeta = itemMeta.apply {
                setDisplayName("${ChatColor.BOLD}${ChatColor.GRAY}엔드")
            }
            lore = listOf("${ChatColor.BOLD}${ChatColor.YELLOW}최후의 결전을 위한 장소입니다.")
        }

        val relicsAllList: MutableMap<Relics, String> =
            mutableMapOf(Pair(Relics("신비한 카드덱", listOf("${ChatColor.GRAY}턴 시작시 카드를 1장 더 뽑습니다.")), "MysticDeck"))

        val sectorAllList: MutableMap<Field, ItemStack> =
            mutableMapOf(Pair(Field.Forest, forest), Pair(Field.Sea, sea), Pair(Field.Cave, cave), Pair(Field.End, end))
        val itemStackToFieldMap: MutableMap<ItemStack, Field> = sectorAllList.entries.associate { (key, value) ->
            value to key
        }.toMutableMap()

        val startCardList = mutableListOf(
            attack,
            attack,
            attack,
            strongAttack,
            strongAttack,
            defense,
            defense,
            defense,
            movement,
            movement
        )
        val cardPackList = listOf(KnightOfFlame, Slaughterer, CogsAndMachines, BirthOfTheUniverse, OurShield)

        val eventList: MutableList<GameEvent> = mutableListOf(
            GameEvent(
                "대혼돈", listOf(
                    "${ChatColor.GRAY}모든 플레이어는 이하의 선택지 중 하나를 선택해야 한다.",
                    "${ChatColor.BOLD}나는 혼돈을 좋아한다. ${ChatColor.RESET}${ChatColor.GRAY}- 보유한 덱에 존재하는 모든 카드를 완전 무작위 카드로 변경한다.",
                    "${ChatColor.BOLD}나는 혼돈을 싫어한다. ${ChatColor.RESET}${ChatColor.GRAY}- 아무 효과도 없다."
                ), "Chaos"
            ), GameEvent(
                "저금통", listOf(
                    "${ChatColor.GRAY}모든 플레이어는 이하의 선택지 중 하나를 선택해야 한다.",
                    "${ChatColor.BOLD}저금한다. ${ChatColor.RESET}${ChatColor.GRAY}- 다음 번 이 이벤트가 발생하면 유물 보상을 얻는다.",
                    "${ChatColor.BOLD}저금하지 않는다. ${ChatColor.RESET}${ChatColor.GRAY}- 카드 보상을 얻는다."
                ), "PiggyBank"
            )
        )
    }

    fun setUp() {
        // KnightOfFlame
        KnightOfFlame.cardList.forEach {
            cardAllList[it] = when (it.name) {
                "용암 분출" -> "LavaEruption"
                "불사조의 깃털" -> "PhoenixFeather"
                "화염 소용돌이" -> "FlameSwirl"
                "휘황찬란한 화염" -> "DazzlingFlames"
                "불타는 복수" -> "BurningRevenge"
                "연소" -> "Combustion"
                "불타는 보호막" -> "BurningShield"
                "화염의 숨" -> "BreathOfFlame"
                "화염의 손길" -> "TouchOfFlame"
                "폭염의 숨결" -> "BreathOfHeatwave"
                "화염 장막" -> "FlameCurtain"
                "잿빛 폭풍" -> "GrayStorm"
                else -> it.name
            }
        }

        // CogsAndMachines
        CogsAndMachines.cardList.forEach {
            cardAllList[it] = when (it.name) {
                "톱니바퀴" -> "CogGear"
                "전깃줄" -> "ElectricWire"
                "방어 기계" -> "DefenseMachine"
                "기어" -> "Gear"
                "톱니 공장" -> "GearFactory"
                "카드 공장" -> "CardFactory"
                "마나 공장" -> "ManaFactory"
                else -> it.name
            }
        }

        // BirthOfTheUniverse
        BirthOfTheUniverse.cardList.forEach {
            cardAllList[it] = when (it.name) {
                "태양" -> "Sun"
                "수성" -> "Mercury"
                "금성" -> "Venus"
                "지구" -> "Earth"
                "화성" -> "Mars"
                "목성" -> "Jupiter"
                "토성" -> "Saturn"
                "천왕성" -> "Uranus"
                "해왕성" -> "Neptune"
                "명왕성" -> "Pluto"
                "빅뱅" -> "BigBang"
                "우주의 특이점" -> "CosmicSingularity"
                "블랙홀" -> "BlackHole"
                else -> it.name
            }
        }

        // OurShield
        OurShield.cardList.forEach {
            cardAllList[it] = when (it.name) {
                "강력한 방어" -> "StrongDefense"
                "수비" -> "Defense"
                "방어 태세" -> "DefensiveStance"
                "모두의 방패" -> "ShieldForAll"
                else -> it.name
            }
        }

        // Slaughterer
        Slaughterer.cardList.forEach {
            cardAllList[it] = when (it.name) {
                "잔인한 찢김" -> "CruelRend"
                "죽음의 붉은 속삭임" -> "RedWhispersOfDeath"
                "칼갈기" -> "SharpenBlade"
                "도살 준비" -> "PreparationForSlaughter"
                "도살 시작" -> "BeginningOfSlaughter"
                "피의 보호막" -> "BloodShield"
                "피의 파동" -> "BloodWave"
                "도살" -> "Slaughter"
                else -> it.name
            }
        }
    }
}