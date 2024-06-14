package org.beobma.projectturn.localization

import org.beobma.projectturn.text.TextManager.Companion.bleedingText
import org.beobma.projectturn.text.TextManager.Companion.burnText
import org.beobma.projectturn.text.TextManager.Companion.excessiveBleedingText
import org.beobma.projectturn.text.TextManager.Companion.extinctionText
import org.beobma.projectturn.text.TextManager.Companion.fireShieldText
import org.beobma.projectturn.text.TextManager.Companion.machineNestingText
import org.beobma.projectturn.text.TextManager.Companion.manaText
import org.beobma.projectturn.text.TextManager.Companion.powerUpText
import org.beobma.projectturn.text.TextManager.Companion.remnantText
import org.beobma.projectturn.text.TextManager.Companion.sameCardDisappearsText
import org.beobma.projectturn.text.TextManager.Companion.torsionText

class Dictionary {
    val dictionaryList: MutableMap<String, String> = mutableMapOf(
        Pair(
            "마나", "${manaText()}: 대부분의 카드를 사용할 때 소모되는 자원입니다."
        ), Pair(
            "잔존", "${remnantText()}: 이 효과가 붙은 카드는 사용해도 카드가 버려지지 않습니다."
        ), Pair(
            "소멸", "${extinctionText()}: 이 효과가 붙은 카드 사용시 이 카드가 제외됩니다."
        ), Pair(
            "동일 카드 소멸", "${sameCardDisappearsText()}: 이 효과가 붙은 카드 사용시 덱, 묘지에 있는 동일한 카드를 모두 제외합니다."
        ), Pair(
            "화상", "${burnText()}: 턴 시작시 수치에 비례한 피해를 입고 수치를 절반으로 감소시킵니다."
        ), Pair(
            "출혈", "${bleedingText()}: 수치가 30 이상이 되면 ${excessiveBleedingText()} 상태가 됩니다."
        ), Pair(
            "과다 출혈", "${excessiveBleedingText()}: 최대 체력의 10% 만큼의 피해를 입고 소멸합니다. 정예, 보스로 취급되는 적의 경우 5%의 피해를 입습니다."
        ), Pair(
            "기계 중첩", "${machineNestingText()}: 기계와 관련된 카드들의 중첩입니다. 특정 효과로 얻고 소모하거나 중첩에 따라 위력이 증가할 수 있습니다."
        ), Pair(
            "화염 방어막", "${fireShieldText()}: 피해를 받으면 공격자에게 수치 만큼의 ${burnText()}을 부여합니다. 다음 턴 시작시 제거됩니다."
        ), Pair(
            "기본 위력 증가", "${powerUpText()}: 적에게 가하는 피해가 수치만큼 증가합니다."
        ), Pair(
            "비틀림", "${torsionText()}: 어떤 경로로든 받는 피해가 수치만큼 증가합니다."
        )
    )
}