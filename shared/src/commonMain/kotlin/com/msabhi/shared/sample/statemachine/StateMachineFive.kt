package com.msabhi.shared.sample.statemachine

import kotlin.reflect.KClass


class StateMachineFive<S : Any, E : Any, A : Any>(
    initialState: S,
    private val transitions: Map<Match<S>, IState<S, E, A>>,
) {

    var state = initialState

    fun dispatch(event: E) {

        transitions.filterKeys { it.matches(state) }.values.firstOrNull()?.let {
            it.transitions.filterKeys { it.matches(event) }.values.firstOrNull()?.let {
                this.state = it.invoke(state, event).first
                return
            }
        }

        throw IllegalStateException()
    }
}

class Match<M : Any> private constructor(private val kClass: KClass<M>) {

    private val predicates = mutableListOf<(M) -> Boolean>({ kClass.isInstance(it) })

    fun add(value: (M) -> Boolean): Match<M> = apply {
        predicates.add(value)
    }

    fun matches(value: M): Boolean {
        return predicates.all { it(value) }
    }

    companion object {

        fun <M : Any> create(kClass: KClass<M>): Match<M> = Match(kClass)

        inline fun <reified M : Any> instance(): Match<M> = create(M::class)

        inline fun <reified M : Any> value(value: M): Match<M> = instance<M>().add { it == value }
    }
}

class IState<S : Any, E : Any, A : Any> {

    val onEnterActions = mutableListOf<A>()
    val onExitActions = mutableListOf<A>()
    val transitions = linkedMapOf<Match<E>, (S, E) -> Pair<S, List<A>>>()

}