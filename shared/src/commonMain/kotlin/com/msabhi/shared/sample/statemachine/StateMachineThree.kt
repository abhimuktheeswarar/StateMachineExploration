package com.msabhi.shared.sample.statemachine

import kotlin.reflect.KClass

class StateMachineThree<S : Any, E : Any, A : Any>(
    initialState: S,
    private val stateTransitions: Map<Pair<KClass<S>, KClass<E>>, (S, E) -> Pair<S, List<A>?>>,
) {

    var state: S = initialState
    var sideEffects: List<A>? = null

    fun dispatch(event: E) {

        for ((key, value) in stateTransitions) {

            val (s, e) = key

            if (s.isInstance(state) && e.isInstance(event)) {
                val output = value.invoke(state, event)
                this.state = output.first
                this.sideEffects = output.second
                return
            }
        }

        throw IllegalStateException()
    }
}