package com.msabhi.shared.sample.statemachine

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


interface BaseStateSeven<S : Any, E : Any, A : Any>

open class IStateSeven<S : Any, E : Any, A : Any> : BaseStateSeven<S, E, A> {

    val onEnterActions = mutableListOf<A>()
    val onExitActions = mutableListOf<A>()
    val transitions = linkedMapOf<Match<E>, (S, E) -> Pair<S, List<A>>>()

}

class StateHolderSeven<S : Any, E : Any, A : Any>(
    private val id: Any,
    private val eventMatch: Match<Any>,
    initialState: S,
    private val states: Map<Match<S>, BaseStateSeven<S, E, A>>,
) : IStateSeven<S, E, A>() {

    var currentState: S = initialState

    fun dispatch(event: E, completableDeferred: CompletableDeferred<S>?) {
        println("$id dispatch: ${event::class.simpleName} | ${currentState::class.simpleName}")

        transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
            this.currentState = it.invoke(currentState, event).first
            completableDeferred?.complete(this.currentState)
            //println("$id return for ${event::class.simpleName}  ${currentState::class.simpleName}")
            //println("$id Updated state 0 = ${event::class.simpleName} -> ${this.currentState::class.simpleName}")
            return
        }

        states.filterKeys { it.matches(currentState) }.values.firstOrNull()?.let {


            if (it is StateHolderSeven && it.eventMatch.matches(event)) {
                it.dispatch(event, completableDeferred)
                return
            } else if (it is IStateSeven) {
                it.transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
                    this.currentState = it.invoke(currentState, event).first
                    completableDeferred?.complete(this.currentState)
                    //println("$id Updated state 1 = ${event::class.simpleName} -> ${this.currentState::class.simpleName}")
                    return
                }
                    ?: run { throw IllegalStateException("0 $id ${event::class} | ${currentState::class.simpleName}") }
            } else {
                throw IllegalStateException("1 $id ${event::class} | ${currentState::class.simpleName}")
            }
        }
            ?: run {
                throw IllegalStateException("2 $id ${event::class} | ${currentState::class.simpleName}")
            }
    }

    fun _dispatch(event: E, completableDeferred: CompletableDeferred<S>?) {
        transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
            this.currentState = it.invoke(currentState, event).first
            completableDeferred?.complete(this.currentState)
            //println("$id Updated state 0 = ${this.currentState::class.simpleName}")
        }
            ?: run { throw IllegalStateException("2 $id ${event::class} | ${currentState::class.simpleName}") }
    }
}

class StateMachineSeven<S : Any, E : Any, A : Any>(
    scope: CoroutineScope,
    private val states: Set<StateHolderSeven<S, E, A>>,
) {

    private val mutableSharedFlow = MutableSharedFlow<S>()

    private val inputEvents =
        Channel<E>(capacity = Int.MAX_VALUE, onBufferOverflow = BufferOverflow.SUSPEND)

    val stateFlow: Flow<S> = mutableSharedFlow

    init {
        scope.launch {
            for (event in inputEvents) {
                states.forEach {
                    val completableDeferred = CompletableDeferred<S>()
                    it.dispatch(event, completableDeferred)
                    val state = completableDeferred.await()
                    mutableSharedFlow.emit(state)
                }
            }
        }
    }

    fun dispatch(event: E) {
        states.forEach { it.dispatch(event, null) }
    }
}