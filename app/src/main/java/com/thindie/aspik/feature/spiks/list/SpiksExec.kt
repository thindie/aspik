package com.thindie.aspik.feature.spiks.list

import com.thindie.aspik.feature.spiks.SpiksFlow
import com.thindie.aspik.feature.spiks.domain.Id
import com.thindie.aspik.feature.spiks.domain.SpeekNote
import java.util.UUID

internal suspend fun SpiksFlow.spiksExec(
  state: SpiksState,
  command: SpiksCommand,
): SpiksState? {
  return when (command) {
    is SpiksCommand.OpenNewNote -> {
      if (command.text.isEmpty()) {
        val id = UUID.randomUUID().toString()
        spiksRepository.save(SpeekNote(Id(id), ""))
        null
      } else {
        null
      }
    }

    is SpiksCommand.EditExistingNote -> {
      null
    }

    is SpiksCommand.UpdateNote -> {
      spiksRepository.update(command.note)
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
