package com.msabhi.shared

import com.msabhi.shared.sample.implementation.*
import com.msabhi.shared.sample.statemachine.Match
import com.msabhi.shared.sample.statemachine.StateHolderTen
import com.msabhi.shared.sample.statemachine.StateMachineTen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class TrafficLightStateMachineTenTest {

    @Test
    fun testTrafficLight() = runBlockingTest {
        println("----START----")

        val cameraStateHolder = StateHolderTen("CAM",
            Match.instance<CameraState>() as Match<Any>,
            Match.instance<CameraEvent>() as Match<Any>,
            CameraState.Paused,
            cameraStates10)

        val stateHolder =
            StateHolderTen("TL",
                Match.instance<TrafficLightState>() as Match<Any>,
                Match.instance<TrafficLightEvent>() as Match<Any>,
                TrafficLightState.Green,
                trafficLight10).apply {

                transitions[Match.instance<TrafficLightEvent.PowerOutage>() as Match<Any>] =
                    { _, _ -> TrafficLightState.RedSM(getRedSM(initialState = PedestrianState.Blinking)) }

                transitions[Match.instance<TrafficLightEvent.PowerRestored>() as Match<Any>] =
                    { _, _ -> TrafficLightState.RedSM() }
            }
        val stateMachine =
            StateMachineTen(TestCoroutineScope(SupervisorJob()),
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

            stateMachine.dispatch(TrafficLightEvent.Timer)
            stateMachine.dispatch(TrafficLightEvent.Timer)
            stateMachine.dispatch(TrafficLightEvent.PowerOutage)
            stateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
        }

        dispatchEvents()
        println("---------------------------")
        //dispatchEvents()

        job.cancel()
        println("----END----")
    }
}