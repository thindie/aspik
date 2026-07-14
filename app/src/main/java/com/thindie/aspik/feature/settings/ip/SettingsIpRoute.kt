package com.thindie.aspik.feature.settings.ip

import com.thindie.engine.core.RouteFactory
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.core.Section

fun settingsIpRoute() =
  RouteFactory.create<SettingsIpCommand, SettingsIpState>(
    id = "settings_ip_route",
    initialState = SettingsIpState(),
    execute = { command, state ->
      when (command) {
        is SettingsIpCommand.SetIp -> state.copy(ip = command.text)
        else -> null
      }
    },
    section = Section.Leaf,
    routeContent = { scope: ScreenScope<SettingsIpState, SettingsIpCommand> ->
      SettingsIpScreenContent(scope)
    },
  )
