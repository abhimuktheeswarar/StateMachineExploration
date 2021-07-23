package com.msabhi.shared

import com.msabhi.shared.sample.implementation.MaterialEvent
import com.msabhi.shared.sample.implementation.MaterialState
import com.msabhi.shared.sample.implementation.materialTransitions3
import com.msabhi.shared.sample.statemachine.StateMachineFour
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MaterialStateMachineFourTest {

    @Test
    fun test() {

        val stateMachine = StateMachineFour(MaterialState.Solid, materialTransitions3)

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