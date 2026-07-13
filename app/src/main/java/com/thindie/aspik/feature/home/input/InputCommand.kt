package com.thindie.aspik.feature.home.input

import com.thindie.engine.core.Command

internal sealed interface InputCommand : Command {
  data object Back : InputCommand
}
