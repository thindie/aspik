package com.thindie.aspik.domain

import androidx.compose.runtime.Immutable

@Immutable
data class SpeekNote(
  val id: Id,
  val note: String,
)
