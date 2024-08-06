package org.beobma.projectturn.game

import org.beobma.projectturn.ProjectTurn
import org.beobma.projectturn.event.StatusEffectDamageEvent
import org.beobma.projectturn.info.Info
import org.bukkit.entity.Entity
import org.bukkit.entity.Player

data class StatusEffect(
    val type: StatusEffectType,
    val power: Int,
    var continuousTurn: Int,
    val victim: Entity,
    val caster: Entity,
    val beCanceled: Boolean = true
) {
    fun manifestation() {
        val game = Info().getGame() ?: return
        if (continuousTurn == 0) {
            if (victim is Player) {
                game.gamePlayerStats[victim]!!.statusEffect.remove(this)
            }
            else {
                game.gameEnemyStats[victim]!!.statusEffect.remove(this)
            }
            return
        }
        if (type == StatusEffectType.Torsion) {
            return
        }
        if (power != 0) {
            val damageEvent = StatusEffectDamageEvent(victim, caster, type, power)
            ProjectTurn.instance.server.pluginManager.callEvent(damageEvent)
            if (damageEvent.isCancelled) {
                return
            }

            if (damageEvent.damage != 0) {
                if (caster is Player) {
                    game.gameEnemyStats[victim]?.damage(damageEvent.damage, caster)
                }
                else {
                    game.gamePlayerStats[victim]?.damage(damageEvent.damage, caster)
                }
            }


            continuousTurn -= 1
        }
        else {
            //피해류 상태 이상이 아님.
            if (type == StatusEffectType.AutoMachineNesting) { game.gamePlayerStats[victim]?.addMachineNesting(power) }
            if (type == StatusEffectType.AutoCardDraw) { game.gamePlayerStats[victim]?.cardDraw(power) }
            if (type == StatusEffectType.AutoManaDraw) { game.gamePlayerStats[victim]?.addMana(power) }

            continuousTurn -= 1
        }
    }
    fun remove() {
        val game = Info().getGame() ?: return
        if (beCanceled) {
            if (victim is Player) {
                game.gamePlayerStats[victim]!!.statusEffect.remove(this)
            }
            else {
                game.gameEnemyStats[victim]!!.statusEffect.remove(this)
            }
        }
        else {
            return
        }
    }
}

enum class StatusEffectType {
    Burn, FireShield, AutoMachineNesting, AutoCardDraw, AutoManaDraw, Torsion
}