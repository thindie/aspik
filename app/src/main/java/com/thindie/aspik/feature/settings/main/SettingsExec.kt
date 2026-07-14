package com.thindie.aspik.feature.settings.main

import com.thindie.aspik.feature.settings.SettingsFlow

internal fun SettingsFlow.settingsExec(
  state: SettingsState,
  command: SettingsCommand,
): SettingsState? {
  return when (command) {
    SettingsCommand.Back -> {
      back()
      null
    }

    SettingsCommand.OpenIp -> TODO()
  }
}
