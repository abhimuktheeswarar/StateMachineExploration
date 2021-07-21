package com.msabhi.shared.sample.statemachine

class StateMachineOne<S, E, A>(
    initialState: S,
    private val stateTransitions: Map<Pair<S, E>, Pair<S, List<A>?>>,
) {

    var state: S = initialState
    var sideEffects: List<A>? = null

    fun dispatch(event: E) {
        val key = Pair(state, event)
        stateTransitions[key]?.let { (state, sideEffects) ->
            this.state = state
            this.sideEffects = sideEffects
        } ?: run {
            throw IllegalStateException()
        }
    }
}