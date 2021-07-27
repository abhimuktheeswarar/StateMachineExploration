package com.msabhi.shared.sample.statemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
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

    fun dispatch(event: E, sendChannel: SendChannel<S>?) {
        println("$id: ${event::class.simpleName}, ${currentState::class.simpleName}")

        transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
            currentState = it(currentState, event)
            sendChannel?.trySend(currentState)
            return
        }

        states.filterKeys { it.matches(currentState) }.values.firstOrNull()?.let {

            if (it is StateHolderEight && it.eventMatch.matches(event)) {
                it.dispatch(event, sendChannel)
                return
            } else if (it is IStateEight) {
                it.transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
                    currentState = it(currentState, event)
                    sendChannel?.trySend(currentState)
                    return
                } ?: throwError(event)
            } else {
                throwError(event)
            }
        } ?: throwError(event)
    }

    private fun throwError(event: E) {
        println("!!! No match found for ${event::class.simpleName} | ${currentState::class.simpleName}")
        throw IllegalStateException()
    }
}


class StateMachineEight<S : Any, E : Any>(
    private val scope: CoroutineScope,
    private val states: Set<StateHolderEight<S, E>>,
) {

    private val mutableSharedFlow = MutableSharedFlow<S>()

    private val inputEvents =
        Channel<E>(capacity = Int.MAX_VALUE, onBufferOverflow = BufferOverflow.SUSPEND)

    private val stateUpdates =
        Channel<S>(capacity = Int.MAX_VALUE, onBufferOverflow = BufferOverflow.SUSPEND)

    val stateFlow: Flow<S> = mutableSharedFlow


    init {

        scope.launch {
            while (isActive) {
                for (state in stateUpdates) {
                    //println("++SM stateUpdates: ${state::class.simpleName}")
                    mutableSharedFlow.emit(state)
                }
            }
        }
        scope.launch {
            while (isActive) {
                for (event in inputEvents) {
                    states.forEach {
                        //println("++SM inputEvents: ${event::class.simpleName}")
                        if (it.eventMatch.matches(event)) {
                            it.dispatch(event, stateUpdates)
                        }
                    }
                }
            }
        }
    }

    fun dispatch(event: E) {
        //println("++SM dispatch: ${event::class.simpleName}")
        inputEvents.trySend(event)
    }
}