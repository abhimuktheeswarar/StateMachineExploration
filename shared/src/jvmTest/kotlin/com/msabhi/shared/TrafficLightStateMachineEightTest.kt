package com.msabhi.shared

import com.msabhi.shared.sample.implementation.*
import com.msabhi.shared.sample.statemachine.BaseStateEight
import com.msabhi.shared.sample.statemachine.Match
import com.msabhi.shared.sample.statemachine.StateHolderEight
import com.msabhi.shared.sample.statemachine.StateMachineEight
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class TrafficLightStateMachineEightTest {

    @Test
    fun testTrafficLight() = runBlockingTest {
        println("----START----")
        val stateHolder =
            StateHolderEight<RoadState, RoadEvent>("TL",
                Match.instance<TrafficLightState>() as Match<Any>,
                Match.instance<TrafficLightEvent>() as Match<Any>,
                TrafficLightState.Green,
                trafficLight3 as Map<Match<Any>, BaseStateEight<RoadState, RoadEvent>>)
        val stateMachine =
            StateMachineEight(TestCoroutineScope(SupervisorJob()), setOf(stateHolder))

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