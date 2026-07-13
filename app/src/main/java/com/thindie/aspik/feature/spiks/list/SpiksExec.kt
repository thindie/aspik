package com.thindie.aspik.feature.spiks.list

import com.thindie.aspik.feature.spiks.SpiksFlow

internal fun SpiksFlow.spiksExec(
  state: SpiksState,
  command: SpiksCommand,
): SpiksState? {
  return when (command) {
    SpiksCommand.Back -> {
      back()
      null
    }
  }
}
