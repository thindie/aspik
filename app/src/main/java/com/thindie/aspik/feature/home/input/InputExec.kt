package com.thindie.aspik.feature.home.input

import com.thindie.aspik.domain.Id
import com.thindie.aspik.domain.SpeekNote
import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.engine.core.ScreenFlow
import com.thindie.engine.core.Section
import java.util.UUID

internal suspend fun ScreenFlow<*, *>.inputExec(
  repository: InputRepository,
  state: InputState,
  command: InputCommand,
): InputState? {
  return when (command) {
    InputCommand.Back -> {
      back()
      null
    }

    is InputCommand.SendText -> {
      when (state.section) {
        is Section.Leaf -> {
          repository.sendText(
            note =
              SpeekNote(
                id = Id(UUID.randomUUID().toString()),
                note = state.input
              ),
          )
        }
        else -> repository.sendText(command.text)
      }
      null
    }

    is InputCommand.Input -> {
      state.copy(input = command.text)
    }
  }
}
