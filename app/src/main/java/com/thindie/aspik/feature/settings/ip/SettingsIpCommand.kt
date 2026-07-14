package com.thindie.aspik.feature.settings.ip

import com.thindie.engine.core.Command

internal sealed interface SettingsIpCommand : Command {
  data class SetIp(val text: String) : SettingsIpCommand

  data object Save : SettingsIpCommand

  data object Back : SettingsIpCommand
}
