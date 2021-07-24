package com.msabhi.shared

import com.msabhi.shared.sample.implementation.MaterialEvent
import com.msabhi.shared.sample.implementation.MaterialState
import com.msabhi.shared.sample.implementation.materialTransitions4
import com.msabhi.shared.sample.statemachine.StateMachineFive
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MaterialStateMachineFiveTest {

    @Test
    fun test() {

        val stateMachine = StateMachineFive(MaterialState.Solid, materialTransitions4)

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


        stateMachine.dispatch(MaterialEvent.OnQuantum(MaterialState.Quantum(MaterialState.Gas)))
        assertEquals(MaterialState.Quantum(MaterialState.Gas), stateMachine.state)

        stateMachine.dispatch(MaterialEvent.OnVaporized)
        assertEquals(MaterialState.Gas, stateMachine.state)
    }
}