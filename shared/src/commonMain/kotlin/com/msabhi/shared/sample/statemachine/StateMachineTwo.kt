package com.msabhi.shared.sample.statemachine

class StateMachineTwo<S : Any, E : Any, A : Any>(
    initialState: S,
    private val stateTransitions: Map<Pair<S, E>, Pair<S, List<A>?>>,
) {

    var state: S = initialState
    var sideEffects: List<A>? = null

    fun dispatch(event: E) {

        for ((key, value) in stateTransitions) {

            val (s, e) = key

            if (s::class.isInstance(state) && e::class.isInstance(event)) {
                this.state = value.first
                this.sideEffects = value.second
                return
            }
        }

        throw IllegalStateException()
    }
}