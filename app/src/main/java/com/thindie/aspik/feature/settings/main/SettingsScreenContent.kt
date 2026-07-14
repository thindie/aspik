package com.thindie.aspik.feature.settings.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thindie.aspik.R
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.uikit.AppScreen
import com.thindie.engine.uikit.Button

@Composable
internal fun SettingsScreenContent(scope: ScreenScope<SettingsState, SettingsCommand>) {
  AppScreen(
    screenScope = scope,
    title = stringResource(R.string.settings_title),
  ) {
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(16.dp),
    ) {
      Button(
        text = stringResource(R.string.server_title),
        onClick = { scope.send(SettingsCommand.OpenIp) },
      )
    }
  }
}
