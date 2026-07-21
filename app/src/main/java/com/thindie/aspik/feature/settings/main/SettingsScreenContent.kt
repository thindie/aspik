package com.thindie.aspik.feature.settings.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thindie.aspik.R
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.uikit.Action
import com.thindie.engine.uikit.AppScreen
import com.thindie.engine.uikit.Button
import com.thindie.engine.uikit.Toggle
import com.thindie.engine.uikit.VSpacer
import com.thindie.engine.uikit.WSpacer

@Composable
internal fun SettingsScreenContent(scope: ScreenScope<SettingsState, SettingsCommand>) {
  val state by scope.state.collectAsState()
  AppScreen(
    screenScope = scope,
    primary =
      Action(
        listener = { scope.send(SettingsCommand.Back) },
        resRef = R.drawable.ic_arrow_back_24,
      ),
    title = stringResource(R.string.settings_title),
  ) {
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(16.dp),
    ) {
      VSpacer(24.dp)
      Row(
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = stringResource(R.string.settings_delete_text_on_send),
          style = com.thindie.engine.uikit.AppTheme.typography.titleMedium,
          color = com.thindie.engine.uikit.AppTheme.colors.contentPrimary,
          modifier = Modifier.weight(1f),
        )
        Toggle(
          checked = state.deleteTextOnSend,
          onClick = { scope.send(SettingsCommand.ToggleDeleteTextOnSend) },
        )
      }
      WSpacer(0.5f)
      Button(
        text = stringResource(R.string.server_title),
        onClick = { scope.send(SettingsCommand.OpenIp) },
      )
    }
  }
}
