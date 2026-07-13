package com.thindie.aspik.feature.home.input

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.uikit.AppScreen
import com.thindie.engine.uikit.Button
import com.thindie.engine.uikit.TextField
import com.thindie.engine.uikit.VSpacer

@Composable
internal fun InputScreenContent(scope: ScreenScope<InputState, InputCommand>) {
  AppScreen(
    screenScope = scope,
    title = "Input",
  ) {
    val state by scope.state.collectAsState()
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(16.dp),
      verticalArrangement = Arrangement.SpaceBetween,
    ) {
      TextField(
        value = state.input,
        onValueChange = { scope.send(InputCommand.SendText(it)) },
        placeholder = "Type or speak...",
        singleLine = false,
        maxLines = 6,
      )

      VSpacer(16.dp)

      Button(
        text = if (state.isListening) "Listening..." else "Speak",
        onClick = { scope.send(InputCommand.Listen) },
        loading = state.isListening,
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
      ) {
        Button(
          text = "Send",
          onClick = { scope.send(InputCommand.SendText(state.input)) },
          enabled = state.input.isNotBlank(),
        )
      }
    }
  }
}
