package com.msabhi.shared.sample.statemachine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch


interface BaseStateTen

open class IStateTen(val stateMatch: Match<Any>, val eventMatch: Match<Any>) : BaseStateTen {

    val transitions = linkedMapOf<Match<Any>, (Any, Any) -> Any>()
}

class StateHolderTen(
    private val id: Any,
    stateMatch: Match<Any>,
    eventMatch: Match<Any>,
    initialState: Any,
    private val states: Map<Match<Any>, BaseStateTen>,
) : IStateTen(stateMatch, eventMatch) {

    var currentState: Any = initialState

    fun dispatch(event: Any, sendChannel: SendChannel<Any>?) {
        println("$id: ${event::class.simpleName}, ${currentState::class.simpleName}")

        transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
            currentState = it(currentState, event)
            sendChannel?.trySend(currentState)
            if (currentState is StateHolderTen) {
                println("dispatch to child")
                (currentState as StateHolderTen).dispatch(event, sendChannel)
            }
            println("return stage 1")
            return
        }

        states.filterKeys { it.matches(currentState) }.values.firstOrNull()?.let {

            if (it is StateHolderTen && it.eventMatch.matches(event)) {
                it.dispatch(event, sendChannel)
            } else if (it is IStateTen) {
                it.transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
                    currentState = it(currentState, event)
                    sendChannel?.trySend(currentState)
                    return
                }
            } else {
                throw IllegalStateException()
            }
        } ?: throw IllegalStateException()
    }
}


class StateMachineTen(
    scope: CoroutineScope,
    private val states: Set<StateHolderTen>,
) {

    private val mutableSharedFlow = MutableSharedFlow<Any>()

    private val inputEvents =
        Channel<Any>(capacity = Int.MAX_VALUE, onBufferOverflow = BufferOverflow.SUSPEND)

    private val stateUpdates =
        Channel<Any>(capacity = Int.MAX_VALUE, onBufferOverflow = BufferOverflow.SUSPEND)

    val stateFlow: Flow<Any> = mutableSharedFlow

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

    fun dispatch(event: Any) {
        inputEvents.trySend(event)
    }

}