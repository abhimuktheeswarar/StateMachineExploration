package com.msabhi.shared

import com.msabhi.shared.sample.implementation.*
import com.msabhi.shared.sample.statemachine.BaseStateEight
import com.msabhi.shared.sample.statemachine.Match
import com.msabhi.shared.sample.statemachine.StateHolderEight
import com.msabhi.shared.sample.statemachine.StateMachineEight
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class TrafficLightStateMachineEightTest {

    @Test
    fun testTrafficLight() = runBlockingTest {
        println("----START----")
        val cameraStateHolder = StateHolderEight("CAM",
            Match.instance<CameraState>() as Match<Any>,
            Match.instance<CameraEvent>() as Match<Any>,
            CameraState.Paused,
            cameraStates as Map<Match<Any>, BaseStateEight<RoadState, RoadEvent>>)

        val stateHolder =
            StateHolderEight<RoadState, RoadEvent>("TL",
                Match.instance<TrafficLightState>() as Match<Any>,
                Match.instance<TrafficLightEvent>() as Match<Any>,
                TrafficLightState.Green,
                trafficLightStates3 as Map<Match<Any>, BaseStateEight<RoadState, RoadEvent>>)

        val sets = setOf<StateHolderEight<RoadState, RoadEvent>>(stateHolder, cameraStateHolder)
        val stateMachine =
            StateMachineEight<RoadState, RoadEvent>(CoroutineScope(SupervisorJob()), sets)

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

            stateMachine.dispatch(CameraEvent.ResumeRecord)
            stateMachine.dispatch(CameraEvent.PauseRecord)
        }

        dispatchEvents()
        println("---------------------------")
        dispatchEvents()

        runCatching { stateMachine.dispatch(TrafficLightEvent.Unknown) }
            .fold({}, { it.printStackTrace() })

        job.cancelAndJoin()
        println("----END----")
    }
}