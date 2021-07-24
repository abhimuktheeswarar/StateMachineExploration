package com.msabhi.shared

import com.msabhi.shared.sample.implementation.MaterialEvent
import com.msabhi.shared.sample.implementation.MaterialSideEffect
import com.msabhi.shared.sample.implementation.MaterialState
import com.msabhi.shared.sample.statemachine.StateMachineZero
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MaterialStateMachineZeroTest {

    private val stateMachine = object :
        StateMachineZero<MaterialState, MaterialEvent, MaterialSideEffect>(MaterialState.Solid) {

        override fun transition(
            event: MaterialEvent,
            state: MaterialState,
        ): Pair<MaterialState, List<MaterialSideEffect>?> {
            return when (state) {
                is MaterialState.Solid -> {
                    when (event) {
                        is MaterialEvent.OnMelted -> Pair(MaterialState.Liquid,
                            listOf(MaterialSideEffect.LogMelted))
                        else -> throw IllegalStateException()
                    }
                }
                is MaterialState.Liquid -> {
                    when (event) {
                        is MaterialEvent.OnVaporized -> Pair(MaterialState.Gas,
                            listOf(MaterialSideEffect.LogVaporized))
                        is MaterialEvent.OnFrozen -> Pair(MaterialState.Solid,
                            listOf(MaterialSideEffect.LogFrozen))
                        else -> throw IllegalStateException()
                    }
                }
                is MaterialState.Gas -> {
                    when (event) {
                        is MaterialEvent.OnCondensed -> Pair(MaterialState.Liquid,
                            listOf(MaterialSideEffect.LogCondensed))
                        else -> throw IllegalStateException()
                    }
                }

                else -> throw IllegalStateException()
            }
        }

    }

    @Test
    fun test() {

        assertEquals(MaterialState.Solid, stateMachine.state)

        stateMachine.dispatch(MaterialEvent.OnMelted)
        assertEquals(MaterialState.Liquid, stateMachine.state)

        stateMachine.dispatch(MaterialEvent.OnFrozen)
        assertEquals(MaterialState.Solid, stateMachine.state)

        val exception: Exception? = try {
            stateMachine.dispatch(MaterialEvent.OnVaporized)
            null
        } catch (e: IllegalStateException) {
            //e.printStackTrace()
            e
        }
        assertNotNull(exception)
    }
}