package com.msabhi.shared

import com.msabhi.shared.sample.implementation.*
import com.msabhi.shared.sample.statemachine.StateHolderSeven
import com.msabhi.shared.sample.statemachine.StateMachineSeven
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class TrafficLightStateMachineSevenTest {

    @Test
    fun testTrafficLight() = runBlockingTest {

        val stateHolderRoot =
            StateHolderSeven<RoadState, RoadEvent, Any>("TL",
                TrafficLightState.Green,
                trafficLight2)
        val rootStateMachine =
            StateMachineSeven<RoadState, RoadEvent, Any>(setOf(stateHolderRoot))

        val job = launch {


        }

        rootStateMachine.dispatch(TrafficLightEvent.Timer)
        rootStateMachine.dispatch(TrafficLightEvent.Timer)

        rootStateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
        rootStateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
        rootStateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
        rootStateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
        rootStateMachine.dispatch(PedestrianEvent.PedestrianCountdown)

        rootStateMachine.dispatch(TrafficLightEvent.Timer)

        job.cancel()
    }
}