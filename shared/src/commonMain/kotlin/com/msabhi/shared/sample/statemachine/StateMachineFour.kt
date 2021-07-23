package com.msabhi.shared.sample.statemachine

import kotlin.reflect.KClass

class StateMachineFour<S : Any, E : Any>(
    initialState: S,
    private val stateTransitions: Map<KClass<S>, Map<KClass<E>, S>>,
) {

    var state: S = initialState

    fun dispatch(event: E) {

        stateTransitions[state::class]?.get(event::class)?.let {
            this.state = it
            return
        }

        throw IllegalStateException()
    }
}

