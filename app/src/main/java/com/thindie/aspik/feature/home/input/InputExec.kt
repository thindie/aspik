package com.thindie.aspik.feature.home.input

import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.engine.core.ScreenFlow
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.core.stateSink
import com.thindie.engine.core.sub
import com.thindie.engine.core.transition
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun ScreenFlow<*, *>.inputExec(
  repository: InputRepository,
  state: InputState,
  command: InputCommand,
): InputState? {
  return when (command) {
    InputCommand.Back -> {
      repository.listenInvalidated()
      back()
      null
    }
    InputCommand.Listen -> {
      withContext(Dispatchers.Main) {
        repository.listen()
        state.copy(isListening = true)
      }
    }
    is InputCommand.SendText -> {
      repository.sendText(command.text)
      state.copy(isListening = false)
    }
    InputCommand.DeleteAccumulated -> {
      repository.deleteAccumulated()
      null
    }
  }
}

internal fun ScreenScope<InputState, InputCommand>.inputSubscriptions(repository: InputRepository) {
  stateSink(this) { s ->
    s.sub(repository.convertedText).transition(
      block = { prevState: InputState, newText: String ->
        val merged = if (prevState.input.isEmpty()) newText else "${prevState.input} $newText"
        prevState.copy(
          input = merged,
          isListening = false,
        )
      },
    )
  }
}
