package com.msabhi.shared

import com.msabhi.shared.sample.implementation.TrafficLightEvent
import com.msabhi.shared.sample.implementation.TrafficLightState
import com.msabhi.shared.sample.implementation.trafficLightStateMachine
import com.msabhi.shared.sample.statemachine.StateMachineSix
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class TrafficLightStateMachineSixTest {

    @Test
    fun testTrafficLight() = runBlockingTest {
        val rootStateMachine =
            StateMachineSix<TrafficLightState, TrafficLightEvent, Any>(TestCoroutineScope(
                SupervisorJob()))
        rootStateMachine.add(trafficLightStateMachine)

        val job = launch {

            rootStateMachine.stateFlow.collect { state ->
                println(state::class.simpleName)
                println("-----")
            }
        }

        rootStateMachine.dispatch(TrafficLightEvent.Timer)
        rootStateMachine.dispatch(TrafficLightEvent.Timer)
        /*rootStateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
        rootStateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
        rootStateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
        rootStateMachine.dispatch(PedestrianEvent.PedestrianCountdown)*/

        rootStateMachine.dispatch(TrafficLightEvent.Timer)

        job.cancel()
        rootStateMachine.terminate()
    }
}