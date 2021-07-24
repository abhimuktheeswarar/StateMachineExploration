package com.msabhi.shared.sample.implementation

import com.msabhi.shared.sample.statemachine.IState
import com.msabhi.shared.sample.statemachine.Match
import kotlin.reflect.KClass

sealed class MaterialState {
    object Solid : MaterialState()
    object Liquid : MaterialState()
    object Gas : MaterialState()
    data class Quantum(val any: MaterialState) : MaterialState()
}

sealed class MaterialEvent {
    object OnMelted : MaterialEvent()
    object OnFrozen : MaterialEvent()
    object OnVaporized : MaterialEvent()
    object OnCondensed : MaterialEvent()
    data class OnQuantum(val any: MaterialState.Quantum) : MaterialEvent()
}

sealed class MaterialSideEffect {
    object LogMelted : MaterialSideEffect()
    object LogFrozen : MaterialSideEffect()
    object LogVaporized : MaterialSideEffect()
    object LogCondensed : MaterialSideEffect()
}

const val ON_MELTED_MESSAGE = "I melted"
const val ON_FROZEN_MESSAGE = "I froze"
const val ON_VAPORIZED_MESSAGE = "I vaporized"
const val ON_CONDENSED_MESSAGE = "I condensed"

val materialTransitions =
    LinkedHashMap<Pair<MaterialState, MaterialEvent>, Pair<MaterialState, List<MaterialSideEffect>?>>().apply {
        put(Pair(MaterialState.Solid, MaterialEvent.OnMelted),
            Pair(MaterialState.Liquid, listOf(MaterialSideEffect.LogMelted)))
        put(Pair(MaterialState.Liquid, MaterialEvent.OnVaporized),
            Pair(MaterialState.Gas, listOf(MaterialSideEffect.LogVaporized)))
        put(Pair(MaterialState.Liquid, MaterialEvent.OnFrozen),
            Pair(MaterialState.Solid, listOf(MaterialSideEffect.LogFrozen)))
        put(Pair(MaterialState.Gas, MaterialEvent.OnCondensed),
            Pair(MaterialState.Liquid, listOf(MaterialSideEffect.LogCondensed)))
    }

val materialTransitions2 =
    LinkedHashMap<MaterialState, Map<MaterialEvent, MaterialState>>().apply {
        put(MaterialState.Solid, mapOf(MaterialEvent.OnMelted to MaterialState.Liquid))
        put(MaterialState.Liquid,
            mapOf(MaterialEvent.OnMelted to MaterialState.Gas,
                MaterialEvent.OnFrozen to MaterialState.Solid))
        put(MaterialState.Gas, mapOf(MaterialEvent.OnCondensed to MaterialState.Liquid))
    }

@Suppress("UNCHECKED_CAST")
val materialTransitions3 =
    LinkedHashMap<KClass<MaterialState>, Map<KClass<MaterialEvent>, MaterialState>>().apply {
        put(MaterialState.Solid::class as KClass<MaterialState>,
            mapOf(MaterialEvent.OnMelted::class as KClass<MaterialEvent> to MaterialState.Liquid))
        put(MaterialState.Liquid::class as KClass<MaterialState>,
            mapOf(MaterialEvent.OnMelted::class as KClass<MaterialEvent> to MaterialState.Gas,
                MaterialEvent.OnFrozen::class as KClass<MaterialEvent> to MaterialState.Solid))
        put(MaterialState.Gas::class as KClass<MaterialState>,
            mapOf(MaterialEvent.OnCondensed::class as KClass<MaterialEvent> to MaterialState.Liquid))
    }

@Suppress("UNCHECKED_CAST")
val materialTransitions4 =
    LinkedHashMap<Match<MaterialState>, IState<MaterialState, MaterialEvent, MaterialSideEffect>>().apply {
        val solid = IState<MaterialState, MaterialEvent, MaterialSideEffect>().apply {
            transitions[Match.instance<MaterialEvent.OnMelted>() as Match<MaterialEvent>] =
                { _, _ -> Pair(MaterialState.Liquid, emptyList()) }
            transitions[Match.value(MaterialEvent.OnQuantum(MaterialState.Quantum(MaterialState.Gas))) as Match<MaterialEvent>] =
                { _, e -> Pair((e as MaterialEvent.OnQuantum).any, emptyList()) }
        }
        put(Match.instance<MaterialState.Solid>() as Match<MaterialState>, solid)

        val liquid = IState<MaterialState, MaterialEvent, MaterialSideEffect>().apply {
            transitions[Match.instance<MaterialEvent.OnVaporized>() as Match<MaterialEvent>] =
                { _, _ -> Pair(MaterialState.Gas, emptyList()) }
            transitions[Match.instance<MaterialEvent.OnFrozen>() as Match<MaterialEvent>] =
                { _, _ -> Pair(MaterialState.Solid, emptyList()) }
        }
        put(Match.instance<MaterialState.Liquid>() as Match<MaterialState>, liquid)

        val gas = IState<MaterialState, MaterialEvent, MaterialSideEffect>().apply {
            transitions[Match.instance<MaterialEvent.OnCondensed>() as Match<MaterialEvent>] =
                { _, _ -> Pair(MaterialState.Liquid, emptyList()) }
        }
        put(Match.instance<MaterialState.Gas>() as Match<MaterialState>, gas)

        val quantum = IState<MaterialState, MaterialEvent, MaterialSideEffect>().apply {
            transitions[Match.instance<MaterialEvent.OnVaporized>() as Match<MaterialEvent>] =
                { s, _ -> Pair((s as MaterialState.Quantum).any, emptyList()) }
        }
        put(Match.value(MaterialState.Quantum(MaterialState.Gas)) as Match<MaterialState>, quantum)
    }