package com.msabhi.shared

import com.msabhi.shared.sample.implementation.MaterialEvent
import com.msabhi.shared.sample.implementation.MaterialState
import com.msabhi.shared.sample.implementation.materialTransitions
import com.msabhi.shared.sample.statemachine.StateMachineOne
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MaterialStateMachineOneTest {

    @Test
    fun test() {

        val stateMachine = StateMachineOne(MaterialState.Solid, materialTransitions)

        assertEquals(MaterialState.Solid, stateMachine.state)

        stateMachine.dispatch(MaterialEvent.OnMelted)
        assertEquals(MaterialState.Liquid, stateMachine.state)

        stateMachine.dispatch(MaterialEvent.OnFrozen)
        assertEquals(MaterialState.Solid, stateMachine.state)

        val exception: Exception? = try {
            stateMachine.dispatch(MaterialEvent.OnVaporized)
            null
        } catch (e: IllegalStateException) {
            e.printStackTrace()
            e
        }
        assertNotNull(exception)
    }
}