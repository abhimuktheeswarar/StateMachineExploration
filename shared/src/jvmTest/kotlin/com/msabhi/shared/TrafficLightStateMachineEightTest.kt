package com.msabhi.shared

import com.msabhi.shared.sample.implementation.*
import com.msabhi.shared.sample.statemachine.BaseStateEight
import com.msabhi.shared.sample.statemachine.Match
import com.msabhi.shared.sample.statemachine.StateHolderEight
import com.msabhi.shared.sample.statemachine.StateMachineEight
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class TrafficLightStateMachineEightTest {

    @Test
    @Suppress("UNCHECKED_CAST")
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
                trafficLightStates3 as Map<Match<Any>, BaseStateEight<RoadState, RoadEvent>>).apply {

                transitions[Match.instance<TrafficLightEvent.PowerOutage>() as Match<RoadEvent>] =
                    { _, _ -> TrafficLightState.RedSM(getRedSM(initialState = PedestrianState.Blinking)) }

                transitions[Match.instance<TrafficLightEvent.PowerRestored>() as Match<RoadEvent>] =
                    { _, _ -> TrafficLightState.RedSM() }
            }

        val scope = TestCoroutineScope() //CoroutineScope(SupervisorJob())
        val sets = setOf<StateHolderEight<Any, Any>>(stateHolder as StateHolderEight<Any, Any>,
            cameraStateHolder as StateHolderEight<Any, Any>)
        val stateMachine =
            StateMachineEight<Any, Any>(scope, sets)

        val job = launch {
            stateMachine.stateFlow.collect { state ->
                if (state is TrafficLightState.RedSM) {
                    println(" ${state::class.simpleName} | ${state.pedestrianState.currentState}")
                } else {
                    println(state::class.simpleName)
                }
                println("-----")
            }
        }

        fun dispatchEvents() {

            stateMachine.dispatch(TrafficLightEvent.Timer)
            stateMachine.dispatch(TrafficLightEvent.Timer)

            stateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
            stateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
            //stateMachine.dispatch(PedestrianEvent.PedestrianCountdown)
            //stateMachine.dispatch(PedestrianEvent.PedestrianCountdown)

            stateMachine.dispatch(TrafficLightEvent.Timer)
            stateMachine.dispatch(TrafficLightEvent.PowerOutage)
            stateMachine.dispatch(PedestrianEvent.PedestrianCountdown)

            //stateMachine.dispatch(CameraEvent.ResumeRecord)
            //stateMachine.dispatch(CameraEvent.PauseRecord)
            //stateMachine.dispatch(TrafficLightEvent.PowerRestored)

            println("---------------------------")
        }

        dispatchEvents()
        //dispatchEvents()

        /* runCatching { stateMachine.dispatch(TrafficLightEvent.Unknown) }
             .fold({}, {
                 //it.printStackTrace()
             })*/

        //scope.cancel()
        job.cancelAndJoin()
        println("----END----")
    }
}