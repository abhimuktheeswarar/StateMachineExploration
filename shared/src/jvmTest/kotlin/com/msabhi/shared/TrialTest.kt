package com.msabhi.shared

import com.msabhi.shared.sample.implementation.TurnstileEvent
import com.msabhi.shared.sample.implementation.TurnstileState
import org.junit.Test
import kotlin.test.assertTrue

class TrialTest {

    @Test
    fun instanceTest() {

        val eventA = TurnstileEvent.Unlock(1)
        val eventB = TurnstileEvent.Unlock(2)

        assertTrue { eventA::class.isInstance(eventB) }

        val stateA = TurnstileState.Locked(1)
        val stateB = TurnstileState.Locked(3)

        assertTrue { stateA::class.isInstance(stateB) }

    }
}