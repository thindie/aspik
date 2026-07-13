package com.thindie.aspik.feature.home.input

import com.thindie.aspik.HomeSection
import com.thindie.aspik.feature.home.HomeFlow
import com.thindie.engine.core.RouteFactory
import com.thindie.engine.core.ScreenScope

fun inputRoute(homeFlow: HomeFlow) =
  RouteFactory.create(
    id = "home_flow_input_route",
    initialState = InputState(),
    execute = { command: InputCommand, state: InputState -> homeFlow.inputExec(state, command) },
    section = HomeSection.Main,
    routeContent = { screenScope: ScreenScope<InputState, InputCommand> ->
      InputScreenContent(screenScope)
    },
    stateSink = { screenScope: ScreenScope<InputState, InputCommand> ->
      screenScope.inputSubscriptions(homeFlow.inputRepository)
    },
  )
