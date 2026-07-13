package com.thindie.aspik.feature.spiks.list

import androidx.compose.runtime.Immutable
import com.thindie.aspik.feature.spiks.domain.SpeekNote
import com.thindie.engine.core.ViewState

@Immutable
internal data class SpiksState(
  val notes: List<SpeekNote> = emptyList(),
) : ViewState
