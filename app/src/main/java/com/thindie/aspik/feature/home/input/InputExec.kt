package com.thindie.aspik.feature.home.input

import com.thindie.aspik.feature.home.HomeFlow

internal fun HomeFlow.inputExec(
  state: InputState,
  command: InputCommand,
): InputState? {
  return when (command) {
    InputCommand.Back -> {
      back()
      null
    }
  }
}
