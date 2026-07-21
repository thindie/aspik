package com.thindie.aspik.feature.home.input

import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.aspik.feature.settings.domain.SettingsRepository
import com.thindie.engine.core.Route
import com.thindie.engine.core.RouteFactory
import com.thindie.engine.core.ScreenFlow
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.core.Section
import com.thindie.engine.core.sub
import com.thindie.engine.core.transition

fun <RESULT, ROUTE : Route> inputRoute(
  inputRepository: InputRepository,
  settingsRepository: SettingsRepository,
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
  stateSink = { scope ->
    scope.sub(settingsRepository.deleteTextOnSend)
      .transition { state, deleteTextOnSend ->
        state.copy(clearInputOnSend = deleteTextOnSend)
      }
  },
)
