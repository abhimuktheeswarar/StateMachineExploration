package com.msabhi.shared

import com.msabhi.shared.sample.implementation.*
import com.msabhi.shared.sample.statemachine.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

@ExperimentalCoroutinesApi
class TrafficLightStateMachineNineTest {

    @Test
    @Suppress("UNCHECKED_CAST")
    fun testTrafficLight() = runBlockingTest {
        println("----START----")
        val cameraStateHolder = StateHolderNine("CAM",
            MatchNine.instance<CameraState>() as MatchNine<BaseStateNine>,
            MatchNine.instance<CameraEvent>() as MatchNine<BaseEventNine>,
            CameraState.Paused,
            cameraStates4 as Map<Match<BaseStateNine>, BaseStateNine>)

        val stateHolder =
            StateHolderNine("TL",
                MatchNine.instance<TrafficLightState>() as MatchNine<BaseStateNine>,
                MatchNine.instance<TrafficLightEvent>() as MatchNine<BaseEventNine>,
                TrafficLightState.Green,
                trafficLightStates4 as Map<Match<BaseStateNine>, BaseStateNine>).apply {

                transitions[MatchNine.instance<TrafficLightEvent.PowerOutage>() as MatchNine<BaseEventNine>] =
                    { _, _ -> TrafficLightState.RedSM(getRedSM(initialState = PedestrianState.Blinking)) }

                transitions[MatchNine.instance<TrafficLightEvent.PowerRestored>() as MatchNine<BaseEventNine>] =
                    { _, _ -> TrafficLightState.RedSM() }
            }

        val scope = TestCoroutineScope() //CoroutineScope(SupervisorJob())
        val sets = setOf<StateHolderNine>(stateHolder, cameraStateHolder)
        val stateMachine =
            StateMachineNine(scope, sets)

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
            stateMachine.dispatch(TrafficLightEvent.Timer)
            return

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