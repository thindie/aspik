package com.thindie.aspik.feature.home.input

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.thindie.aspik.R
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.core.Section
import com.thindie.engine.core.ServiceCommand
import com.thindie.engine.uikit.Action
import com.thindie.engine.uikit.AppScreen
import com.thindie.engine.uikit.AppTheme
import com.thindie.engine.uikit.TextField
import com.thindie.engine.uikit.VSpacer
import com.thindie.engine.uikit.WSpacer

@Composable
internal fun InputScreenContent(scope: ScreenScope<InputState, InputCommand>) {
  val state by scope.state.collectAsState()
  AppScreen(
    modifier = Modifier.imePadding(),
    screenScope = scope,
    primary =
      if (state.section is Section.Leaf) {
        Action(
          listener = { scope.send(InputCommand.Back) },
          resRef = R.drawable.ic_main,
        )
      } else {
        null
      },
  ) {
    Text(
      modifier = Modifier.padding(horizontal = 16.dp),
      text = stringResource(R.string.input_title),
      style = AppTheme.typography.headlineLarge,
      color = AppTheme.colors.contentPrimary,
    )
    Column(
      modifier =
        Modifier
          .fillMaxSize()
          .padding(horizontal = 16.dp),
    ) {
      VSpacer(24.dp)
      AnimatedVisibility(
        state.input.isNotBlank(),
      ) {
        TextField(
          value = state.input,
          onValueChange = { scope.send(InputCommand.ManualCorrection(it)) },
          placeholder = stringResource(R.string.input_listening),
          singleLine = false,
        )
      }
      WSpacer(1f)
      VSpacer(16.dp)

      val speechLauncher =
        rememberLauncherForActivityResult(
          contract = ActivityResultContracts.StartActivityForResult(),
        ) { result ->
          if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            scope.send(InputCommand.Input(data?.firstOrNull() ?: ""))
          }
        }

      val speak = stringResource(R.string.input_speak)
      val errorText = stringResource(R.string.input_error)

      Row(
        modifier = Modifier.height(128.dp),
      ) {
        val backgroundColor by animateColorAsState(
          if (state.input.isNotBlank()) AppTheme.colors.cardPrimary else AppTheme.colors.backgroundSecondary,
        )
        val textColor by animateColorAsState(
          if (state.input.isNotBlank()) AppTheme.colors.contentSecondary else AppTheme.colors.contentTertiary,
        )
        AnimatedVisibility(visible = state.input.isNotBlank()) {
          Text(
            modifier =
              Modifier
                .background(backgroundColor, CircleShape)
                .padding(20.dp)
                .clickable(
                  indication = null,
                  interactionSource = null,
                  enabled = state.input.isNotBlank(),
                  onClick = { scope.send(InputCommand.ManualCorrection("")) },
                )
                .align(Alignment.Bottom),
            text = stringResource(R.string.input_delete),
            style = AppTheme.typography.headlineSmall,
            color = textColor,
          )
        }
        Text(
          modifier =
            Modifier
              .background(AppTheme.colors.accentPrimary, CircleShape)
              .weight(1f)
              .padding(40.dp)
              .clickable(
                indication = null,
                interactionSource = null,
                onClick = {
                  val intent =
                    Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                      .apply {
                        putExtra(
                          RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                          RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
                        )
                        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
                        putExtra(RecognizerIntent.EXTRA_PROMPT, speak)
                      }
                  try {
                    speechLauncher.launch(intent)
                  } catch (_: Exception) {
                    scope.sendEvent(ServiceCommand.UiEvent.SnackText(errorText))
                  }
                },
              )
              .align(Alignment.Top),
          text = stringResource(R.string.input_speak),
          style = AppTheme.typography.headlineLarge,
          color = AppTheme.colors.onAccentPrimary,
          textAlign = TextAlign.Center,
        )
        AnimatedVisibility(
          modifier = Modifier.align(Alignment.Bottom),
          visible = state.input.isNotBlank(),
        ) {
          Text(
            modifier =
              Modifier
                .background(backgroundColor, CircleShape)
                .padding(20.dp)
                .clickable(
                  indication = null,
                  interactionSource = null,
                  enabled = state.input.isNotBlank(),
                  onClick = { scope.send(InputCommand.SendText(state.input)) },
                )
                .align(Alignment.Bottom),
            text = stringResource(R.string.input_send),
            color = AppTheme.colors.accentPrimary,
            style = AppTheme.typography.headlineSmall,
          )
        }
      }
      WSpacer(0.25f)
    }
  }
}
