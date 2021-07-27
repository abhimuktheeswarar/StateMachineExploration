package com.msabhi.shared.sample.statemachine


interface BaseStateSeven<S : Any, E : Any, A : Any>

open class IStateSeven<S : Any, E : Any, A : Any> : BaseStateSeven<S, E, A> {

    val onEnterActions = mutableListOf<A>()
    val onExitActions = mutableListOf<A>()
    val transitions = linkedMapOf<Match<E>, (S, E) -> Pair<S, List<A>>>()

}

class StateHolderSeven<S : Any, E : Any, A : Any>(
    private val id: Any,
    initialState: S,
    private val states: Map<Match<S>, BaseStateSeven<S, E, A>>,
) : IStateSeven<S, E, A>() {

    var currentState: S = initialState

    fun dispatch(event: E) {
        println("$id dispatch: ${event::class.simpleName} | ${currentState::class.simpleName}")

        transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
            this.currentState = it.invoke(currentState, event).first
            //println("$id Updated state 0 = ${this.currentState::class.simpleName}")
            return
        }

        states.filterKeys { it.matches(currentState) }.values.firstOrNull()?.let {

            if (it is StateHolderSeven) {
                it.dispatch(event)
                return
            } else if (it is IStateSeven) {
                it.transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
                    this.currentState = it.invoke(currentState, event).first
                    //println("$id Updated state 1 = ${this.currentState::class.simpleName}")
                    return
                }
                    ?: run { throw IllegalStateException("$id ${event::class} | ${currentState::class.simpleName}") }
            } else {
                throw IllegalStateException("$id ${event::class} | ${currentState::class.simpleName}")
            }
        }
            ?: run { throw IllegalStateException("$id ${event::class} | ${currentState::class.simpleName}") }
    }

}

class StateMachineSeven<S : Any, E : Any, A : Any>(private val states: Set<StateHolderSeven<S, E, A>>) {

    fun dispatch(event: E) {
        states.forEach { it.dispatch(event) }
    }
}