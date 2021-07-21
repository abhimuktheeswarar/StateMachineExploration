package com.msabhi.shared

import com.msabhi.shared.sample.implementation.TurnstileEvent
import com.msabhi.shared.sample.implementation.TurnstileState
import com.msabhi.shared.sample.implementation.turnstileTransitions
import com.msabhi.shared.sample.statemachine.StateMachineThree
import org.junit.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class TurnstileStateMachineTwoTest {

    @Test
    fun test() {

        val stateMachine = StateMachineThree(TurnstileState.Locked(), turnstileTransitions)

        stateMachine.dispatch(TurnstileEvent.Unlock(1234))
        assertTrue { TurnstileState.UnLocked::class.isInstance(stateMachine.state) }

        val exception: Exception? = try {
            stateMachine.dispatch(TurnstileEvent.Unlock(1234))
            null
        } catch (e: IllegalStateException) {
            //e.printStackTrace()
            e
        }
        assertNotNull(exception)
    }
}