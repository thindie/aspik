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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thindie.aspik.R
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.uikit.AppScreen
import com.thindie.engine.uikit.Button
import com.thindie.engine.uikit.TextField
import com.thindie.engine.uikit.VSpacer

@Composable
internal fun InputScreenContent(scope: ScreenScope<InputState, InputCommand>) {
  AppScreen(
    screenScope = scope,
    title = stringResource(R.string.input_title),
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
        placeholder = stringResource(R.string.input_placeholder),
        singleLine = false,
        maxLines = 6,
      )

      VSpacer(16.dp)

      Button(
        text =
          stringResource(
            id = if (state.isListening) R.string.input_listening else R.string.input_speak,
          ),
        onClick = { scope.send(InputCommand.Listen) },
        loading = state.isListening,
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
      ) {
        Button(
          text = stringResource(R.string.input_send),
          onClick = { scope.send(InputCommand.SendText(state.input)) },
          enabled = state.input.isNotBlank(),
        )
      }
    }
  }
}
