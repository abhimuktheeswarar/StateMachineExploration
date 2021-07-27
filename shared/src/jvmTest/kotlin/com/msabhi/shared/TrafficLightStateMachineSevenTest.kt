package com.msabhi.shared

import com.msabhi.shared.sample.implementation.*
import com.msabhi.shared.sample.statemachine.Match
import com.msabhi.shared.sample.statemachine.StateHolderSeven
import com.msabhi.shared.sample.statemachine.StateMachineSeven
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class TrafficLightStateMachineSevenTest {

    @Test
    fun testTrafficLight() = runBlockingTest {
        println("----START----")
        val stateHolder =
            StateHolderSeven<RoadState, RoadEvent, Any>("TL",
                Match.instance<TrafficLightEvent>() as Match<Any>,
                TrafficLightState.Green,
                trafficLight2)
        val stateMachine =
            StateMachineSeven<RoadState, RoadEvent, Any>(TestCoroutineScope(SupervisorJob()),
                setOf(stateHolder))

        val job = launch {
            stateMachine.stateFlow.collect { state ->
                println(state::class.simpleName)
                println("-----")
            }
        }

        fun dispatchEvents() {

            stateMachine.dispatch(TrafficLightEvent.Timer)
            stateMachine.dispatch(TrafficLightEvent.Timer)

            stateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
            stateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
            stateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
            stateMachine.dispatch(PedestrianEvent.PedestrianCountdown)

            stateMachine.dispatch(TrafficLightEvent.Timer)
        }

        dispatchEvents()
        println("---------------------------")
        dispatchEvents()

        job.cancel()
        println("----END----")
    }
}