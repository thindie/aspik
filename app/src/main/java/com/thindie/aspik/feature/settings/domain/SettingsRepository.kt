package com.thindie.aspik.feature.settings.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
  val deleteTextOnSend: Flow<Boolean>

  suspend fun setDeleteTextOnSend(value: Boolean)
}
