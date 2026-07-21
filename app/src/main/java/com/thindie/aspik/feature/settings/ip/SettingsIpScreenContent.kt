package com.thindie.aspik.feature.settings.ip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.thindie.aspik.R
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.uikit.Action
import com.thindie.engine.uikit.AppScreen
import com.thindie.engine.uikit.Button
import com.thindie.engine.uikit.TextField

@Composable
internal fun SettingsIpScreenContent(scope: ScreenScope<SettingsIpState, SettingsIpCommand>) {
  val state by scope.state.collectAsState()
  AppScreen(
    screenScope = scope,
    title = stringResource(R.string.settings_ip_title),
    primary =
      Action(
        listener = { scope.send(SettingsIpCommand.Back) },
        resRef = R.drawable.ic_main,
      ),
  ) {
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(16.dp),
    ) {
      TextField(
        value = state.ip,
        onValueChange = { scope.send(SettingsIpCommand.SetIp(it)) },
        placeholder = stringResource(R.string.settings_ip_hint),
        singleLine = true,
      )

      Button(
        text = stringResource(R.string.settings_ip_save),
        onClick = {
          val ip = state.ip.trim()
          if (ip.isNotBlank()) {
          } else {
            scope.send(SettingsIpCommand.Back)
          }
        },
        enabled = state.ip.isNotBlank(),
      )
    }
  }
}
