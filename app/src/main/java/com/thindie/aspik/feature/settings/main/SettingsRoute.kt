package com.thindie.aspik.feature.settings.main

import com.thindie.aspik.HomeSection
import com.thindie.aspik.feature.settings.SettingsFlow
import com.thindie.engine.core.RouteFactory
import com.thindie.engine.core.ScreenScope

fun settingsRoute(settingsFlow: SettingsFlow) =
  RouteFactory.create(
    id = "home_flow_settings_route",
    initialState = SettingsState(),
    execute = { command: SettingsCommand, state: SettingsState -> settingsFlow.settingsExec(state, command) },
    section = HomeSection.Settings,
    routeContent = { screenScope: ScreenScope<SettingsState, SettingsCommand> ->
      SettingsScreenContent(screenScope)
    },
  )
