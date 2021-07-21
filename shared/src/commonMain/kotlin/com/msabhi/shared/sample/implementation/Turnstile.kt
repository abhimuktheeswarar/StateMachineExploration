package com.msabhi.shared.sample.implementation

import kotlin.reflect.KClass

sealed class TurnstileState {

    data class Locked(val password: Int = 1234, val inValidAttempts: Int = 0) : TurnstileState()

    data class UnLocked(val validAttempts: Int = 0) : TurnstileState()
}

sealed class TurnstileEvent {

    object Lock : TurnstileEvent()

    data class Unlock(val password: Int) : TurnstileEvent()
}

sealed class TurnstileEffect {
    object Locked : TurnstileEffect()
    object Unlocked : TurnstileEffect()
    object IncorrectAttempts : TurnstileEffect()
}

val turnstileTransitions =
    LinkedHashMap<Pair<KClass<TurnstileState>, KClass<TurnstileEvent>>, (TurnstileState, TurnstileEvent) -> Pair<TurnstileState, List<TurnstileEffect>?>>().apply {

        val k1 = Pair(TurnstileState.Locked::class,
            TurnstileEvent.Unlock::class) as Pair<KClass<TurnstileState>, KClass<TurnstileEvent>>
        val v1: (TurnstileState.Locked, TurnstileEvent.Unlock) -> Pair<TurnstileState, List<TurnstileEffect>?> =
            { s, e ->
                if (s.password == e.password) {
                    Pair(TurnstileState.UnLocked(), null)
                } else Pair(s, null)
            }
        put(k1,
            v1 as (TurnstileState, TurnstileEvent) -> Pair<TurnstileState, List<TurnstileEffect>?>)

        val k2 = Pair(TurnstileState.UnLocked::class,
            TurnstileEvent.Lock::class) as Pair<KClass<TurnstileState>, KClass<TurnstileEvent>>
        val v2: (TurnstileState, TurnstileEvent) -> Pair<TurnstileState, List<TurnstileEffect>?> =
            { s, e -> Pair(TurnstileState.Locked(), null) }
        put(k2, v2)

    }