package com.thindie.aspik.feature.settings.main

import com.thindie.engine.core.Command

internal sealed interface SettingsCommand : Command {
  data object Back : SettingsCommand

  data object OpenIp : SettingsCommand
}
