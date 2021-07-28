package com.msabhi.shared.sample.implementation

import com.msabhi.shared.sample.statemachine.*


interface RoadState : BaseStateNine

interface RoadEvent : BaseEventNine

sealed class PedestrianState : RoadState, TrafficLightState() {

    object Walk : PedestrianState()
    object Wait : PedestrianState()
    object Stop : PedestrianState()
    object Blinking : PedestrianState()

}

sealed class PedestrianEvent : RoadEvent, TrafficLightEvent() {

    object PedestrianCountdown : PedestrianEvent()
}

sealed class TrafficLightState : RoadState {

    object Green : TrafficLightState()
    object Yellow : TrafficLightState()
    object RedSimple : TrafficLightState()
    data class RedSM(val pedestrianState: StateHolderEight<RoadState, RoadEvent> = getRedSM()) :
        TrafficLightState()

    data class Red(
        override val stateMachine: IStateMachine<PedestrianState, PedestrianEvent, Any> = pedestrianStateMachine,
    ) : TrafficLightState(), StateMachineState<PedestrianState, PedestrianEvent, Any>
}



sealed class TrafficLightEvent : RoadEvent {

    object Timer : TrafficLightEvent()
    object PowerOutage : TrafficLightEvent()
    object PowerRestored : TrafficLightEvent()
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
                { _, _ -> Pair(TrafficLightState.RedSimple, emptyList()) }
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
        put(Match.value(TrafficLightState.RedSimple) as Match<RoadState>, red)
    }

//--------------------------------------------------------------------------------------------------------

@Suppress("UNCHECKED_CAST")
val pedestrianStates3 =
    LinkedHashMap<Match<RoadState>, BaseStateEight<PedestrianState, PedestrianEvent>>().apply {
        val walk =
            IStateEight<PedestrianState, PedestrianEvent>(Match.instance<PedestrianState.Walk>() as Match<Any>,
                Match.instance<PedestrianEvent>() as Match<Any>).apply {
                transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                    { _, _ -> PedestrianState.Wait }
            }
        put(Match.instance<PedestrianState.Walk>() as Match<RoadState>, walk)

        val wait =
            IStateEight<PedestrianState, PedestrianEvent>(Match.instance<PedestrianState.Wait>() as Match<Any>,
                Match.instance<PedestrianEvent>() as Match<Any>).apply {
                transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                    { _, _ -> PedestrianState.Stop }
            }
        put(Match.instance<PedestrianState.Wait>() as Match<RoadState>, wait)

        val stop =
            IStateEight<PedestrianState, PedestrianEvent>(Match.instance<PedestrianState.Stop>() as Match<Any>,
                Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<Any>)/*.apply {
                transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                    { _, _ -> PedestrianState.Blinking }
            }*/
        put(Match.instance<PedestrianState.Stop>() as Match<RoadState>, stop)

        val blinking =
            IStateEight<PedestrianState, PedestrianEvent>(Match.instance<PedestrianState.Blinking>() as Match<Any>,
                Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<Any>)/*.apply {
                transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<PedestrianEvent>] =
                    { _, _ -> PedestrianState.Walk }
            }*/
        put(Match.instance<PedestrianState.Blinking>() as Match<RoadState>, blinking)
    }


fun getRedSM(initialState: PedestrianState = PedestrianState.Wait) =
    StateHolderEight<RoadState, RoadEvent>("PD",
        Match.instance<PedestrianState>() as Match<Any>,
        Match.instance<PedestrianEvent>() as Match<Any>,
        initialState,
        pedestrianStates3 as Map<Match<Any>, BaseStateEight<RoadState, RoadEvent>>).apply {
        transitions[Match.instance<TrafficLightEvent.Timer>() as Match<RoadEvent>] =
            { _, _ -> TrafficLightState.Green }
    }

@Suppress("UNCHECKED_CAST")
val trafficLightStates3 =
    LinkedHashMap<Match<TrafficLightState>, BaseStateEight<TrafficLightState, TrafficLightEvent>>().apply {
        val green =
            IStateEight<TrafficLightState, TrafficLightEvent.Timer>(Match.instance<TrafficLightState.Green>() as Match<Any>,
                Match.instance<TrafficLightEvent.Timer>() as Match<Any>).apply {
                transitions[Match.instance<TrafficLightEvent.Timer>()] =
                    { _, _ -> TrafficLightState.Yellow }
            }
        put(Match.instance<TrafficLightState.Green>() as Match<TrafficLightState>,
            green as BaseStateEight<TrafficLightState, TrafficLightEvent>)
        val yellow =
            IStateEight<TrafficLightState, TrafficLightEvent>(Match.instance<TrafficLightState.Yellow>() as Match<Any>,
                Match.instance<TrafficLightEvent.Timer>() as Match<Any>).apply {
                transitions[Match.instance<TrafficLightEvent.Timer>() as Match<TrafficLightEvent>] =
                    { _, _ -> TrafficLightState.RedSM() }
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
        put(Match.instance<TrafficLightState.RedSM>() as Match<TrafficLightState>,
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

//--------------------------------------------------------------------------------------------------------

@Suppress("UNCHECKED_CAST")
val pedestrianStates4 =
    LinkedHashMap<MatchNine<PedestrianState>, BaseStateNine>().apply {
        val walk =
            createIStateNine<PedestrianState, PedestrianEvent>().apply {
                transitions[MatchNine.instance<PedestrianEvent.PedestrianCountdown>() as MatchNine<BaseEventNine>] =
                    { _, _ -> PedestrianState.Wait }
            }
        put(MatchNine.instance<PedestrianState.Walk>() as MatchNine<PedestrianState>, walk)

        val wait =
            createIStateNine<PedestrianState, PedestrianEvent>().apply {
                transitions[MatchNine.instance<PedestrianEvent.PedestrianCountdown>() as MatchNine<BaseEventNine>] =
                    { _, _ -> PedestrianState.Stop }
            }
        put(MatchNine.instance<PedestrianState.Wait>() as MatchNine<PedestrianState>, wait)

        val stop =
            createIStateNine<PedestrianState, PedestrianEvent>().apply {
                transitions[MatchNine.instance<PedestrianEvent.PedestrianCountdown>() as MatchNine<BaseEventNine>] =
                    { _, _ -> PedestrianState.Blinking }
            }
        put(MatchNine.instance<PedestrianState.Stop>() as MatchNine<PedestrianState>, stop)

        val blinking =
            createIStateNine<PedestrianState, PedestrianEvent>().apply {
                transitions[MatchNine.instance<PedestrianEvent.PedestrianCountdown>() as MatchNine<BaseEventNine>] =
                    { _, _ -> PedestrianState.Walk }
            }
        put(MatchNine.instance<PedestrianState.Blinking>() as MatchNine<PedestrianState>, blinking)
    }

@Suppress("UNCHECKED_CAST")
val trafficLightStates4 =
    LinkedHashMap<MatchNine<TrafficLightState>, BaseStateNine>().apply {

        /*val green =
            createIStateNine<TrafficLightState.Green, TrafficLightEvent.Timer>().apply {
                transitions[MatchNine.instance<TrafficLightEvent.Timer>() as MatchNine<BaseEventNine>] =
                    { _, _ -> TrafficLightState.Yellow }
            }*/

        val green =
            IStateNine(MatchNine.instance<TrafficLightState.Green>() as MatchNine<BaseStateNine>,
                MatchNine.instance<TrafficLightEvent.Timer>() as MatchNine<BaseEventNine>).apply {
                transitions[MatchNine.instance<TrafficLightEvent.Timer>() as MatchNine<BaseEventNine>] =
                    { _, _ -> TrafficLightState.Yellow }
            }

        put(MatchNine.instance<TrafficLightState.Green>() as MatchNine<TrafficLightState>, green)

        val yellow =
            createIStateNine<TrafficLightState, TrafficLightEvent.Timer>().apply {
                transitions[MatchNine.instance<TrafficLightEvent.Timer>() as MatchNine<BaseEventNine>] =
                    { _, _ -> TrafficLightState.RedSM() }
            }
        put(MatchNine.instance<TrafficLightState.Yellow>() as MatchNine<TrafficLightState>, yellow)

        val red = StateHolderNine("PD",
            MatchNine.instance<PedestrianState>() as MatchNine<BaseStateNine>,
            MatchNine.instance<PedestrianEvent>() as MatchNine<BaseEventNine>,
            PedestrianState.Walk,
            pedestrianStates4 as Map<Match<BaseStateNine>, BaseStateNine>).apply {
            transitions[MatchNine.instance<TrafficLightEvent.Timer>() as MatchNine<BaseEventNine>] =
                { _, _ -> TrafficLightState.Green }
        }
        put(MatchNine.instance<TrafficLightState.RedSM>() as MatchNine<TrafficLightState>, red)
    }

@Suppress("UNCHECKED_CAST")
val cameraStates4 =
    LinkedHashMap<MatchNine<CameraState>, BaseStateNine>().apply {

        val pause =
            createIStateNine<CameraState.Paused, CameraEvent.ResumeRecord>().apply {
                transitions[MatchNine.instance<CameraEvent.ResumeRecord>() as MatchNine<BaseEventNine>] =
                    { _, _ -> CameraState.Recording }
            }
        put(MatchNine.instance<CameraState.Paused>() as MatchNine<CameraState>, pause)

        val recording =
            createIStateNine<CameraState.Recording, CameraEvent.PauseRecord>().apply {
                transitions[MatchNine.instance<CameraEvent.PauseRecord>() as MatchNine<BaseEventNine>] =
                    { _, _ -> CameraState.Paused }
            }
        put(MatchNine.instance<CameraState.Recording>() as MatchNine<CameraState>, recording)
    }


//--------------------------------------------------------------------------------------------------------

@Suppress("UNCHECKED_CAST")
val pedestrianStates10 =
    LinkedHashMap<Match<Any>, BaseStateTen>().apply {
        val walk = IStateTen(Match.instance<PedestrianState>() as Match<Any>,
            Match.instance<PedestrianEvent>() as Match<Any>).apply {
            transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<Any>] =
                { _, _ -> PedestrianState.Wait }
        }
        put(Match.instance<PedestrianState.Walk>() as Match<Any>, walk)
        val wait = IStateTen(Match.instance<PedestrianState>() as Match<Any>,
            Match.instance<PedestrianEvent>() as Match<Any>).apply {
            transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<Any>] =
                { _, _ -> PedestrianState.Stop }
        }
        put(Match.instance<PedestrianState.Wait>() as Match<Any>, wait)
        val stop = IStateTen(Match.instance<PedestrianState>() as Match<Any>,
            Match.instance<PedestrianEvent>() as Match<Any>).apply {
            /*transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<Any>] =
                { _, _ -> PedestrianState.Blinking }*/
        }
        put(Match.instance<PedestrianState.Stop>() as Match<Any>, stop)
        val blinking = IStateTen(Match.instance<PedestrianState>() as Match<Any>,
            Match.instance<PedestrianEvent>() as Match<Any>).apply {
            /*transitions[Match.instance<PedestrianEvent.PedestrianCountdown>() as Match<Any>] =
                { _, _ -> PedestrianState.Walk }*/
        }
        put(Match.instance<PedestrianState.Blinking>() as Match<Any>, blinking)
    }

@Suppress("UNCHECKED_CAST")
val trafficLight10 =
    LinkedHashMap<Match<Any>, BaseStateTen>().apply {
        val green = IStateTen(Match.instance<TrafficLightState>() as Match<Any>,
            Match.instance<TrafficLightEvent>() as Match<Any>).apply {
            transitions[Match.instance<TrafficLightEvent.Timer>() as Match<Any>] =
                { _, _ -> TrafficLightState.Yellow }
        }
        put(Match.instance<TrafficLightState.Green>() as Match<Any>, green)
        val yellow = IStateTen(Match.instance<TrafficLightState>() as Match<Any>,
            Match.instance<TrafficLightEvent>() as Match<Any>).apply {
            transitions[Match.instance<TrafficLightEvent.Timer>() as Match<Any>] =
                { _, _ -> TrafficLightState.RedSM() }
        }
        put(Match.instance<TrafficLightState.Yellow>() as Match<Any>, yellow)
        val red = StateHolderTen("PD",
            Match.instance<PedestrianState>() as Match<Any>,
            Match.instance<PedestrianEvent>() as Match<Any>,
            PedestrianState.Walk,
            pedestrianStates10).apply {
            transitions[Match.instance<TrafficLightEvent.Timer>() as Match<Any>] =
                { _, _ -> TrafficLightState.Green }
        }
        put(Match.instance<TrafficLightState.RedSM>() as Match<Any>, red)
    }

@Suppress("UNCHECKED_CAST")
val cameraStates10 =
    LinkedHashMap<Match<Any>, BaseStateTen>().apply {

        val pause =
            IStateTen(Match.instance<CameraState>() as Match<Any>,
                Match.instance<CameraEvent>() as Match<Any>).apply {
                transitions[Match.instance<CameraEvent.ResumeRecord>() as Match<Any>] =
                    { _, _ -> CameraState.Recording }
            }
        put(Match.instance<CameraState.Paused>() as Match<Any>, pause)

        val recording =
            IStateTen(Match.instance<CameraState>() as Match<Any>,
                Match.instance<CameraEvent>() as Match<Any>).apply {
                transitions[Match.instance<CameraEvent.PauseRecord>() as Match<Any>] =
                    { _, _ -> CameraState.Paused }
            }
        put(Match.instance<CameraState.Recording>() as Match<Any>, recording)
    }
