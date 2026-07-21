package com.thindie.aspik.feature.home.input.domain

import com.thindie.aspik.domain.SpeekNote

interface InputRepository {
  suspend fun sendText(text: String)

  suspend fun sendText(note: SpeekNote)
}
