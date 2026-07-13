package com.thindie.aspik.feature.spiks.list

import com.thindie.engine.core.Command

internal sealed interface SpiksCommand : Command {
  data object Back : SpiksCommand
}
