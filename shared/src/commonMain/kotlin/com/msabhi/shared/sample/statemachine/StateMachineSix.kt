package com.msabhi.shared.sample.statemachine

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch


class StateMachineSix<S : Any, E : Any, A : Any>(
    private val scope: CoroutineScope,

    ) : BaseState<S, E, A> {

    private val states = mutableSetOf<IStateMachine<S, E, A>>()
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

    fun add(stateMachine: IStateMachine<S, E, A>) {
        states.add(stateMachine)
    }

    fun dispatch(event: E) {
        inputEvents.trySend(event)
    }

    fun terminate() {
        scope.cancel()
    }
}

interface BaseState<S : Any, E : Any, A : Any>

class IStateSix<S : Any, E : Any, A : Any> : BaseState<S, E, A> {

    val onEnterActions = mutableListOf<A>()
    val onExitActions = mutableListOf<A>()
    val transitions = linkedMapOf<Match<E>, (S, E) -> Pair<S, List<A>>>()

}

interface StateMachineState<S : Any, E : Any, A : Any> : BaseState<S, E, A> {

    val stateMachine: IStateMachine<S, E, A>
}


class IStateMachine<S : Any, E : Any, A : Any>(
    initialState: S,
    private val states: Set<Map<Match<S>, BaseState<S, E, A>>>,
) : BaseState<S, E, A> {

    var state = initialState

    fun dispatch(event: E, completableDeferred: CompletableDeferred<S>) {
        println("dispatch = ${event::class.simpleName} | ${state::class.simpleName} | ${state is StateMachineState<*, *, *>}")

        states.forEach {

            it.filterKeys { it.matches(state) }.values.firstOrNull()?.let {

                when (it) {
                    is IStateSix -> {
                        println("IState 1 = ${it::class.simpleName}")
                        if (it is StateMachineState) {
                            println("StateMachineState ${it::class.simpleName}")
                            it.stateMachine.dispatch(event, completableDeferred)
                        } else {
                            println("IState 2 ${it::class.simpleName}")
                            it.transitions.filterKeys { it.matches(event) }.values.firstOrNull()
                                ?.let {
                                    this.state = it.invoke(state, event).first
                                    completableDeferred.complete(this.state)
                                    return
                                }
                        }
                    }
                    is IStateMachine -> {
                        it.dispatch(event, completableDeferred)
                    }
                    else -> {
                        throw Exception("Unknown 0")
                    }
                }
            } ?: throw Exception("Unknown 1")
        }

        throw IllegalStateException()
    }
}

@Suppress("UNCHECKED_CAST")
inline fun <reified S : Any> Flow<S>.validTransitions() =
    filterIsInstance<S>().distinctUntilChanged()