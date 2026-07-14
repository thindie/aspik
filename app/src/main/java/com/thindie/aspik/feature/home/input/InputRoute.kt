package com.thindie.aspik.feature.home.input

import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.engine.core.Route
import com.thindie.engine.core.RouteFactory
import com.thindie.engine.core.ScreenFlow
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.core.Section

fun <RESULT, ROUTE : Route> inputRoute(
  inputRepository: InputRepository,
  screenFlow: ScreenFlow<ROUTE, RESULT>,
  section: Section,
) = RouteFactory.create(
  id = "common_input_route",
  initialState = InputState(section = section),
  execute = { command: InputCommand, state: InputState ->
    screenFlow.inputExec(inputRepository, state, command)
  },
  section = section,
  routeContent = { screenScope: ScreenScope<InputState, InputCommand> ->
    InputScreenContent(screenScope)
  },
)
