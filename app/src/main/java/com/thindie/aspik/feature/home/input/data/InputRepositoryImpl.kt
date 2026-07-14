package com.thindie.aspik.feature.home.input.data

import android.content.Context
import com.thindie.aspik.domain.SpeekNote
import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.aspik.feature.spiks.data.ReactiveStorage
import com.thindie.aspik.util.NetworkUtils
import okhttp3.OkHttpClient

internal class InputRepositoryImpl(
  private val context: Context,
  private val client: OkHttpClient,
  private val storage: ReactiveStorage,
) : InputRepository {
  override suspend fun sendText(text: String) {
    NetworkUtils.sendText(client, context, text).getOrThrow()
  }

  override suspend fun sendText(note: SpeekNote) {
    storage.putAll(mapOf(note.id.value to note.note))
  }
}
