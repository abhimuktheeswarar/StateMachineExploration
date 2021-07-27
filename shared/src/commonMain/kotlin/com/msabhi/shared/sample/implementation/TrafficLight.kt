package com.msabhi.shared.sample.implementation

import com.msabhi.shared.sample.statemachine.*


interface RoadState

interface RoadEvent

sealed class PedestrianState : TrafficLightState() {

    object Walk : PedestrianState()
    object Wait : PedestrianState()
    object Stop : PedestrianState()
    object Blinking : PedestrianState()

}

sealed class PedestrianEvent : TrafficLightEvent() {

    object PedestrianCountdown : PedestrianEvent()
}

sealed class TrafficLightState : RoadState {

    object Green : TrafficLightState()
    object Yellow : TrafficLightState()
    object RedSM : TrafficLightState()

    data class Red(
        override val stateMachine: IStateMachine<PedestrianState, PedestrianEvent, Any> = pedestrianStateMachine,
    ) : TrafficLightState(), StateMachineState<PedestrianState, PedestrianEvent, Any>
}

sealed class TrafficLightEvent : RoadEvent {

    object Timer : TrafficLightEvent()
    object Unknown : TrafficLightEvent()
}

sealed class CameraState : RoadState {

    object Recording : CameraState()

    object Paused : CameraState()
}

sealed class CameraEvent : RoadEvent {

    object ResumeRecord : CameraEvent()

    object PauseRecord : CameraEvent()
}

@Suppress("UNCHECKED_CAST")
val pedestrianStates =
    LinkedHashMap<Match<PedestrianState>, BaseState<PedestrianState, PedestrianEvent, Any>>().apply {
        val walk = IStateSix<PedestrianState, PedestrianEvent, Any>().apply {
            transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                { _, _ -> Pair(PedestrianState.Wait, emptyList()) }
        }
        put(Match.instance<PedestrianState.Walk>() as Match<PedestrianState>, walk)
        val wait = IStateSix<PedestrianState, PedestrianEvent, Any>().apply {
            transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                { _, _ -> Pair(PedestrianState.Stop, emptyList()) }
        }
        put(Match.instance<PedestrianState.Wait>() as Match<PedestrianState>, wait)
        val stop = IStateSix<PedestrianState, PedestrianEvent, Any>().apply {
            transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                { _, _ -> Pair(PedestrianState.Blinking, emptyList()) }
        }
        put(Match.instance<PedestrianState.Stop>() as Match<PedestrianState>, stop)
        val blinking = IStateSix<PedestrianState, PedestrianEvent, Any>().apply {
            transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                { _, _ -> Pair(PedestrianState.Walk, emptyList()) }
        }
        put(Match.instance<PedestrianState.Blinking>() as Match<PedestrianState>, blinking)
    }

@Suppress("UNCHECKED_CAST")
val trafficLight =
    LinkedHashMap<Match<TrafficLightState>, BaseState<TrafficLightState, TrafficLightEvent, Any>>().apply {
        val green = IStateSix<TrafficLightState, TrafficLightEvent, Any>().apply {
            transitions[Match.instance<TrafficLightEvent.Timer>() as Match<TrafficLightEvent>] =
                { _, _ -> Pair(TrafficLightState.Yellow, emptyList()) }
        }
        put(Match.instance<TrafficLightState.Green>() as Match<TrafficLightState>, green)
        val yellow = IStateSix<TrafficLightState, TrafficLightEvent, Any>().apply {
            transitions[Match.instance<TrafficLightEvent.Timer>() as Match<TrafficLightEvent>] =
                { _, _ -> Pair(TrafficLightState.Red(), emptyList()) }
        }
        put(Match.instance<TrafficLightState.Yellow>() as Match<TrafficLightState>, yellow)
        val red = IStateSix<TrafficLightState, TrafficLightEvent, Any>().apply {
            transitions[Match.instance<TrafficLightEvent.Timer>() as Match<TrafficLightEvent>] =
                { _, _ -> Pair(TrafficLightState.Green, emptyList()) }
        }
        put(Match.instance<TrafficLightState.Red>() as Match<TrafficLightState>, red)
    }


val pedestrianStateMachine: IStateMachine<PedestrianState, PedestrianEvent, Any> =
    IStateMachine(PedestrianState.Walk, setOf(pedestrianStates))
val trafficLightStateMachine = IStateMachine(TrafficLightState.Green, setOf(trafficLight))

//--------------------------------------------------------------------------------------------------------

@Suppress("UNCHECKED_CAST")
val pedestrianStates2 =
    LinkedHashMap<Match<RoadState>, BaseStateSeven<RoadState, RoadEvent, Any>>().apply {
        val walk = IStateSeven<RoadState, RoadEvent, Any>().apply {
            transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<RoadEvent>] =
                { _, _ -> Pair(PedestrianState.Wait, emptyList()) }
        }
        put(Match.instance<PedestrianState.Walk>() as Match<RoadState>, walk)
        val wait = IStateSeven<RoadState, RoadEvent, Any>().apply {
            transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<RoadEvent>] =
                { _, _ -> Pair(PedestrianState.Stop, emptyList()) }
        }
        put(Match.instance<PedestrianState.Wait>() as Match<RoadState>, wait)
        val stop = IStateSeven<RoadState, RoadEvent, Any>().apply {
            transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<RoadEvent>] =
                { _, _ -> Pair(PedestrianState.Blinking, emptyList()) }
        }
        put(Match.instance<PedestrianState.Stop>() as Match<RoadState>, stop)
        val blinking = IStateSeven<RoadState, RoadEvent, Any>().apply {
            transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<RoadEvent>] =
                { _, _ -> Pair(PedestrianState.Walk, emptyList()) }
        }
        put(Match.instance<PedestrianState.Blinking>() as Match<RoadState>, blinking)
    }

@Suppress("UNCHECKED_CAST")
val trafficLight2 =
    LinkedHashMap<Match<RoadState>, BaseStateSeven<RoadState, RoadEvent, Any>>().apply {
        val green = IStateSeven<RoadState, RoadEvent, Any>().apply {
            transitions[Match.instance<TrafficLightEvent.Timer>() as Match<RoadEvent>] =
                { _, _ -> Pair(TrafficLightState.Yellow, emptyList()) }
        }
        put(Match.instance<TrafficLightState.Green>() as Match<RoadState>, green)
        val yellow = IStateSeven<RoadState, RoadEvent, Any>().apply {
            transitions[Match.instance<TrafficLightEvent.Timer>() as Match<RoadEvent>] =
                { _, _ -> Pair(TrafficLightState.RedSM, emptyList()) }
        }
        put(Match.instance<TrafficLightState.Yellow>() as Match<RoadState>, yellow)
        val red = StateHolderSeven<RoadState, RoadEvent, Any>("--------PD",
            Match.instance<PedestrianEvent>() as Match<Any>,
            PedestrianState.Walk,
            pedestrianStates2).apply {
            transitions[Match.instance<TrafficLightEvent.Timer>() as Match<RoadEvent>] =
                { _, _ -> Pair(TrafficLightState.Green, emptyList()) }
            /*transitions[Match.instance<PedestrianEvent>() as Match<RoadEvent>] =
                { s, _ -> Pair(s, emptyList()) }*/
        }
        put(Match.value(TrafficLightState.RedSM) as Match<RoadState>, red)
    }

//--------------------------------------------------------------------------------------------------------

@Suppress("UNCHECKED_CAST")
val pedestrianStates3 =
    LinkedHashMap<Match<RoadState>, BaseStateEight<PedestrianState, PedestrianEvent>>().apply {
        val walk =
            IStateEight<PedestrianState, PedestrianEvent>(Match.instance<PedestrianState>() as Match<Any>,
                Match.instance<PedestrianEvent>() as Match<Any>).apply {
                transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                    { _, _ -> PedestrianState.Wait }
            }
        put(Match.instance<PedestrianState.Walk>() as Match<RoadState>, walk)

        val wait =
            IStateEight<PedestrianState, PedestrianEvent>(Match.instance<PedestrianState>() as Match<Any>,
                Match.instance<PedestrianEvent>() as Match<Any>).apply {
                transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                    { _, _ -> PedestrianState.Stop }
            }
        put(Match.instance<PedestrianState.Wait>() as Match<RoadState>, wait)

        val stop =
            IStateEight<PedestrianState, PedestrianEvent>(Match.instance<PedestrianState>() as Match<Any>,
                Match.instance<PedestrianEvent>() as Match<Any>).apply {
                transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                    { _, _ -> PedestrianState.Blinking }
            }
        put(Match.instance<PedestrianState.Stop>() as Match<RoadState>, stop)

        val blinking =
            IStateEight<PedestrianState, PedestrianEvent>(Match.instance<PedestrianState>() as Match<Any>,
                Match.instance<PedestrianEvent>() as Match<Any>).apply {
                transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                    { _, _ -> PedestrianState.Walk }
            }
        put(Match.instance<PedestrianState.Blinking>() as Match<RoadState>, blinking)
    }

@Suppress("UNCHECKED_CAST")
val trafficLightStates3 =
    LinkedHashMap<Match<TrafficLightState>, BaseStateEight<TrafficLightState, TrafficLightEvent>>().apply {
        val green =
            IStateEight<TrafficLightState, TrafficLightEvent.Timer>(Match.instance<TrafficLightState>() as Match<Any>,
                Match.instance<TrafficLightEvent.Timer>() as Match<Any>).apply {
                transitions[Match.instance<TrafficLightEvent.Timer>()] =
                    { _, _ -> TrafficLightState.Yellow }
            }
        put(Match.instance<TrafficLightState.Green>() as Match<TrafficLightState>,
            green as BaseStateEight<TrafficLightState, TrafficLightEvent>)
        val yellow =
            IStateEight<TrafficLightState, TrafficLightEvent>(Match.instance<TrafficLightState>() as Match<Any>,
                Match.instance<TrafficLightEvent.Timer>() as Match<Any>).apply {
                transitions[Match.instance<TrafficLightEvent.Timer>() as Match<TrafficLightEvent>] =
                    { _, _ -> TrafficLightState.RedSM }
            }
        put(Match.instance<TrafficLightState.Yellow>() as Match<TrafficLightState>, yellow)
        val red = StateHolderEight<RoadState, RoadEvent>("PD",
            Match.instance<PedestrianState>() as Match<Any>,
            Match.instance<PedestrianEvent>() as Match<Any>,
            PedestrianState.Walk,
            pedestrianStates3 as Map<Match<Any>, BaseStateEight<RoadState, RoadEvent>>).apply {
            transitions[Match.instance<TrafficLightEvent.Timer>() as Match<RoadEvent>] =
                { _, _ -> TrafficLightState.Green }
        }
        put(Match.value(TrafficLightState.RedSM) as Match<TrafficLightState>,
            red as BaseStateEight<TrafficLightState, TrafficLightEvent>)
    }

@Suppress("UNCHECKED_CAST")
val cameraStates =
    LinkedHashMap<Match<RoadState>, BaseStateEight<CameraState, CameraEvent>>().apply {
        val pause =
            IStateEight<CameraState, CameraEvent>(Match.instance<CameraState>() as Match<Any>,
                Match.instance<CameraEvent>() as Match<Any>).apply {
                transitions[Match.instance<CameraEvent.ResumeRecord>() as Match<CameraEvent>] =
                    { _, _ -> CameraState.Recording }
            }
        put(Match.instance<CameraState.Paused>() as Match<RoadState>, pause)

        val recording =
            IStateEight<CameraState, CameraEvent>(Match.instance<CameraState>() as Match<Any>,
                Match.instance<CameraEvent>() as Match<Any>).apply {
                transitions[Match.instance<CameraEvent.PauseRecord>() as Match<CameraEvent>] =
                    { _, _ -> CameraState.Paused }
            }
        put(Match.instance<CameraState.Recording>() as Match<RoadState>, recording)
    }