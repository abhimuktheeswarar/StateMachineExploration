package com.msabhi.shared.sample.statemachine

abstract class StateMachineZero<S, E, A>(initialState: S) {

    var state: S = initialState
    var sideEffects: List<A>? = null

    fun dispatch(event: E) {
        val (state, sideEffects) = transition(event, state)
        this.state = state
        this.sideEffects = sideEffects
    }

    abstract fun transition(event: E, state: S): Pair<S, List<A>?>

}