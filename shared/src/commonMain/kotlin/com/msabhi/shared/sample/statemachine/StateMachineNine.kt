package com.msabhi.shared.sample.statemachine


import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.reflect.KClass


interface BaseNine
interface BaseEventNine : BaseNine
interface BaseStateNine : BaseNine

class MatchNine<M : Any> private constructor(private val kClass: KClass<M>) {

    private val predicates = mutableListOf<(M) -> Boolean>({ kClass.isInstance(it) })

    fun add(value: (M) -> Boolean): MatchNine<M> = apply {
        predicates.add(value)
    }

    fun matches(value: M): Boolean {
        return predicates.all { it(value) }
    }

    companion object {

        fun <M : Any> create(kClass: KClass<M>): MatchNine<M> = MatchNine(kClass)

        inline fun <reified M : Any> instance(): MatchNine<M> = create(M::class)

        inline fun <reified M : Any> value(value: M): MatchNine<M> =
            instance<M>().add { it == value }
    }
}


open class IStateNine(
    val stateMatch: MatchNine<BaseStateNine>,
    val eventMatch: MatchNine<BaseEventNine>,
) : BaseStateNine {

    val transitions =
        linkedMapOf<MatchNine<BaseEventNine>, (BaseStateNine, BaseEventNine) -> BaseStateNine>()
}

class StateHolderNine(
    private val id: Any,
    stateMatch: MatchNine<BaseStateNine>,
    eventMatch: MatchNine<BaseEventNine>,
    initialState: BaseStateNine,
    private val states: Map<Match<BaseStateNine>, BaseStateNine>,
) : IStateNine(stateMatch, eventMatch) {

    var currentState: BaseStateNine = initialState

    fun dispatch(event: BaseEventNine, sendChannel: SendChannel<BaseStateNine>?) {
        println("$id: ${event::class.simpleName}, ${currentState::class.simpleName}")

        transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
            currentState = it(currentState, event)
            sendChannel?.trySend(currentState)
            if (currentState is StateHolderNine) {
                (currentState as StateHolderNine).dispatch(event, sendChannel)
            }
            return
        } ?: run { println("No match found in transitions") }

        states.filterKeys { it.matches(currentState) }.values.firstOrNull()?.let {

            if (it is StateHolderNine && it.eventMatch.matches(event)) {
                it.dispatch(event, sendChannel)
                return
            } else if (it is IStateNine) {
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

    private fun throwError(event: BaseEventNine) {
        println("!!! No match found for ${event::class.simpleName} | ${currentState::class.simpleName}")
        throw IllegalStateException()
    }
}


class StateMachineNine(
    private val scope: CoroutineScope,
    private val states: Set<StateHolderNine>,
) {

    private val mutableSharedFlow = MutableSharedFlow<BaseStateNine>()

    private val inputEvents =
        Channel<BaseEventNine>(capacity = Int.MAX_VALUE, onBufferOverflow = BufferOverflow.SUSPEND)

    private val stateUpdates =
        Channel<BaseStateNine>(capacity = Int.MAX_VALUE, onBufferOverflow = BufferOverflow.SUSPEND)

    val stateFlow: Flow<BaseStateNine> = mutableSharedFlow

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

    fun dispatch(event: BaseEventNine) {
        println("++SM dispatch: ${event::class.simpleName}")
        inputEvents.trySend(event)
    }
}

/*inline fun <reified A: Any> createIStateNine(value: A): MatchNine<A> {

}*/

@Suppress("UNCHECKED_CAST")
inline fun <reified S : Any, reified E : Any> createIStateNine(): IStateNine {
    val stateMatch: MatchNine<S> = MatchNine.instance()
    val eventMatch: MatchNine<E> = MatchNine.instance()
    return IStateNine(stateMatch as MatchNine<BaseStateNine>,
        eventMatch as MatchNine<BaseEventNine>)
}

/*@Suppress("UNCHECKED_CAST")
inline fun <reified S : Any, reified E : Any> createTransition():  (S, E) -> S {
    val stateMatch: MatchNine<S> = MatchNine.instance()
    val eventMatch: MatchNine<E> = MatchNine.instance()
    return IStateNine(stateMatch as MatchNine<BaseStateNine>,
        eventMatch as MatchNine<BaseEventNine>)
}*/
