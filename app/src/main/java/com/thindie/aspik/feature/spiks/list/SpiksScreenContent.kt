package com.thindie.aspik.feature.spiks.list

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.thindie.aspik.R
import com.thindie.engine.core.ScreenScope
import com.thindie.engine.uikit.AppScreen
import com.thindie.engine.uikit.AppTheme

@Composable
internal fun SpiksScreenContent(scope: ScreenScope<SpiksState, SpiksCommand>) {
  AppScreen(
    screenScope = scope,
    title = "Spiks",
  ) {
    val state by scope.state.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
      if (state.notes.isEmpty()) {
        Box(
          modifier = Modifier.fillMaxSize(),
          contentAlignment = Alignment.Center,
        ) {
          Text(
            text = "No notes yet",
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.contentSecondary,
          )
        }
      } else {
        Column(modifier = Modifier.fillMaxSize()) {
          state.notes.forEach { note ->
            NoteItem(
              text = note.note,
              onClick = { scope.send(SpiksCommand.OpenNewNote(note.note)) },
              onLongClick = { scope.send(SpiksCommand.DeleteNote(note.id)) },
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(80.dp))

      IconButton(
        onClick = { scope.send(SpiksCommand.OpenNewNote()) },
        modifier =
          Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(AppTheme.colors.accentPrimary),
      ) {
        Icon(
          painter = painterResource(R.drawable.ic_spiks),
          contentDescription = "Add note",
          tint = AppTheme.colors.buttonContentPrimary,
          modifier = Modifier.size(24.dp),
        )
      }
    }
  }
}

@Composable
private fun NoteItem(
  text: String,
  onClick: () -> Unit,
  onLongClick: () -> Unit,
) {
  Row(
    modifier =
      Modifier
        .fillMaxWidth()
        .combinedClickable(
          onLongClick = onLongClick,
          onClick = onClick,
        )
        .padding(horizontal = 16.dp, vertical = 8.dp),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Text(
      text = text.ifBlank { "Empty note" },
      style = AppTheme.typography.bodyMedium,
      color = AppTheme.colors.contentPrimary,
      modifier = Modifier.weight(1f),
    )
  }
}
