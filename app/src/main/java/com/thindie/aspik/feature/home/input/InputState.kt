package com.thindie.aspik.feature.home.input

import androidx.compose.runtime.Immutable
import com.thindie.engine.core.ViewState

@Immutable
internal data class InputState(
  val input: String = "",
  val isListening: Boolean = false,
) : ViewState
