package com.msabhi.shared.sample.statemachine

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


interface BaseStateEight

open class IStateEight(val stateMatch: Match<Any>, val eventMatch: Match<Any>) : BaseStateEight {

    val transitions = linkedMapOf<Match<Any>, (Any, Any) -> Any>()
}

class StateHolderEight(
    private val id: Any,
    stateMatch: Match<Any>,
    eventMatch: Match<Any>,
    initialState: Any,
    private val states: Map<Match<Any>, BaseStateEight>,
) : IStateEight(stateMatch, eventMatch) {

    var currentState: Any = initialState

    fun dispatch(event: Any, completableDeferred: CompletableDeferred<Any>?) {
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


class StateMachineEight(
    scope: CoroutineScope,
    private val states: Set<StateHolderEight>,
) {

    private val mutableSharedFlow = MutableSharedFlow<Any>()

    private val inputEvents =
        Channel<Any>(capacity = Int.MAX_VALUE, onBufferOverflow = BufferOverflow.SUSPEND)

    val stateFlow: Flow<Any> = mutableSharedFlow

    init {
        scope.launch {
            for (event in inputEvents) {
                states.forEach {
                    val completableDeferred = CompletableDeferred<Any>()
                    it.dispatch(event, completableDeferred)
                    val state = completableDeferred.await()
                    mutableSharedFlow.emit(state)
                }
            }
        }
    }

    fun dispatch(event: Any) {
        inputEvents.trySend(event)
    }

}