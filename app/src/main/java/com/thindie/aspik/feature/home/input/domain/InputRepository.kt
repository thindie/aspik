package com.thindie.aspik.feature.home.input.domain

import kotlinx.coroutines.flow.Flow

interface InputRepository {
  fun listen()

  val convertedText: Flow<String>

  suspend fun sendText(text: String)

  fun setNote()
}
