package com.msabhi.shared.sample.statemachine

import kotlin.reflect.KClass

class StateMachineThree<S : Any, E : Any, A : Any>(
    initialState: S,
    private val stateTransitions: Map<Pair<KClass<S>, KClass<E>>, (S, E) -> Pair<S, List<A>?>>,
) {

    var state: S = initialState
    var sideEffects: List<A>? = null

    fun dispatch(event: E) {

        stateTransitions[Pair(state::class, event::class)]?.let {
            val output = it.invoke(state, event)
            this.state = output.first
            this.sideEffects = output.second
        } ?: run {
            throw IllegalStateException()
        }
    }
}


