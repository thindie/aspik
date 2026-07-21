package com.thindie.aspik.feature.settings.ip

import com.thindie.engine.core.ScreenScope
import com.thindie.engine.core.stateSink

internal fun ScreenScope<SettingsIpState, SettingsIpCommand>.settingsIpExecute() {
  stateSink(this) { scope ->
    // No external subscriptions needed — all updates are command-driven.
  }
}
