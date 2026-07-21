package com.thindie.aspik.feature.settings.main

import androidx.compose.runtime.Immutable
import com.thindie.engine.core.ViewState

@Immutable
internal class SettingsState(
  val deleteTextOnSend: Boolean = false,
) : ViewState
