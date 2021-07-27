package com.msabhi.shared.sample.statemachine

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


interface BaseStateEight<S : Any, E : Any>

open class IStateEight<S : Any, E : Any>(
    val stateMatch: Match<Any>,
    val eventMatch: Match<Any>,
) :
    BaseStateEight<S, E> {

    val transitions = linkedMapOf<Match<E>, (S, E) -> S>()
}

class StateHolderEight<S : Any, E : Any>(
    private val id: Any,
    stateMatch: Match<Any>,
    eventMatch: Match<Any>,
    initialState: S,
    private val states: Map<Match<Any>, BaseStateEight<S, E>>,
) : IStateEight<S, E>(stateMatch, eventMatch) {

    var currentState: S = initialState

    fun dispatch(event: E, completableDeferred: CompletableDeferred<S>?) {
        println("$id: ${event::class.simpleName}, ${currentState::class.simpleName}")

        transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
            currentState = it(currentState, event)
            completableDeferred?.complete(currentState)
            return
        }

        states.filterKeys { it.matches(currentState) }.values.firstOrNull()?.let {

            if (it is StateHolderEight && it.eventMatch.matches(event)) {
                it.dispatch(event, completableDeferred)
            } else if (it is IStateEight) {
                it.transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
                    currentState = it(currentState, event)
                    completableDeferred?.complete(currentState)
                    return
                }
            } else {
                throw IllegalStateException()
            }
        } ?: throw IllegalStateException()
    }
}


class StateMachineEight<S : Any, E : Any>(
    scope: CoroutineScope,
    private val states: Set<StateHolderEight<S, E>>,
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
        inputEvents.trySend(event)
    }
}