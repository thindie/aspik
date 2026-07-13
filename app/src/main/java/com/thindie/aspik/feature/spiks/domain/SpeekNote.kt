package com.thindie.aspik.feature.spiks.domain

import androidx.compose.runtime.Immutable

@Immutable
data class SpeekNote(
  val id: Id,
  val note: String,
)
