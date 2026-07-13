package com.thindie.aspik.feature.spiks.domain

import kotlinx.coroutines.flow.Flow

interface SpiksRepository {
  val notes: Flow<List<SpeekNote>>

  suspend fun save(note: SpeekNote)

  suspend fun update(note: SpeekNote)

  suspend fun delete(id: Id)
}
