package com.thindie.aspik.feature.home.input

import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.engine.core.ScreenFlow
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.core.stateSink
import com.thindie.engine.core.sub
import com.thindie.engine.core.transition

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
    InputCommand.Listen -> {
      repository.listen()
      state.copy(isListening = true)
    }
    is InputCommand.SendText -> {
      repository.sendText(command.text)
      state.copy(isListening = false)
    }
  }
}

internal fun ScreenScope<InputState, InputCommand>.inputSubscriptions(repository: InputRepository) {
  stateSink(this) { s ->
    s.sub(repository.convertedText).transition(
      block = { prevState: InputState, newText: String ->
        prevState.copy(
          input = newText,
          isListening = false,
        )
      },
    )
  }
}
