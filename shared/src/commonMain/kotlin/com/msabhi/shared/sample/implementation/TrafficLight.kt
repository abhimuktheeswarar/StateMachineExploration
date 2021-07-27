package com.msabhi.shared.sample.implementation

import com.msabhi.shared.sample.statemachine.*


interface RoadState

interface RoadEvent

sealed class PedestrianState : RoadState {

    object Walk : PedestrianState()
    object Wait : PedestrianState()
    object Stop : PedestrianState()
    object Blinking : PedestrianState()

}

sealed class PedestrianEvent : RoadEvent {

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