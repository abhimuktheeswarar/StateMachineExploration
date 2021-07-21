package com.msabhi.shared.sample.implementation

sealed class MaterialState {
    object Solid : MaterialState()
    object Liquid : MaterialState()
    object Gas : MaterialState()
}

sealed class MaterialEvent {
    object OnMelted : MaterialEvent()
    object OnFrozen : MaterialEvent()
    object OnVaporized : MaterialEvent()
    object OnCondensed : MaterialEvent()
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

val materialtransitions =
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