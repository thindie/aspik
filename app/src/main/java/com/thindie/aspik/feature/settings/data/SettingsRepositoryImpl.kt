package com.thindie.aspik.feature.settings.data

import com.thindie.aspik.feature.settings.domain.SettingsRepository
import com.thindie.aspik.feature.spiks.data.ReactiveStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SettingsRepositoryImpl(
  private val storage: ReactiveStorage,
) : SettingsRepository {
  override val deleteTextOnSend: Flow<Boolean> =
    storage.cache.map { it[KEY_DELETE_TEXT_ON_SEND] == "true" }

  override suspend fun setDeleteTextOnSend(value: Boolean) {
    storage.putAll(mapOf(KEY_DELETE_TEXT_ON_SEND to value.toString()))
  }
}

private const val KEY_DELETE_TEXT_ON_SEND = "delete_text_on_send"
