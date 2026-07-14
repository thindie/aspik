package com.thindie.aspik.feature.settings.ip

import androidx.compose.runtime.Immutable
import com.thindie.engine.core.ViewState

@Immutable
internal data class SettingsIpState(
  val ip: String = "",
) : ViewState
