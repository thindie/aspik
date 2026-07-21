package com.thindie.aspik.feature.spiks.list

import com.thindie.aspik.feature.spiks.SpiksFlow

internal suspend fun SpiksFlow.spiksExec(
  state: SpiksState,
  command: SpiksCommand,
): SpiksState? {
  return when (command) {
    is SpiksCommand.OpenNewNote -> {
      // todo pin note to repository
      goOperateNote()
      null
    }

    is SpiksCommand.UpdateNote -> {
      // todo pin note to repository
      goOperateNote()
      null
    }

    is SpiksCommand.DeleteNote -> {
      spiksRepository.delete(command.id)
      null
    }

    SpiksCommand.Back -> {
      back()
      null
    }
  }
}
