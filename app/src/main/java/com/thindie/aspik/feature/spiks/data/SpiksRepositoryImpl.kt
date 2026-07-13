package com.thindie.aspik.feature.spiks.data

import com.thindie.aspik.feature.spiks.domain.Id
import com.thindie.aspik.feature.spiks.domain.SpeekNote
import com.thindie.aspik.feature.spiks.domain.SpiksRepository
import com.thindie.aspik.feature.spiks.storage.ReactiveStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SpiksRepositoryImpl(
  private val storage: ReactiveStorage,
) : SpiksRepository {
  override val notes: Flow<List<SpeekNote>> =
    storage.cache.map { map ->
      map.entries
        .mapNotNull { (key, value) ->
          if (value.isEmpty()) null else SpeekNote(Id(key), value)
        }
        .sortedByDescending { it.id.value }
    }

  override suspend fun save(note: SpeekNote) {
    storage.putAll(mapOf(note.id.value to note.note))
  }

  override suspend fun update(note: SpeekNote) {
    storage.putAll(mapOf(note.id.value to note.note))
  }

  override suspend fun delete(id: Id) {
    storage.putAll(mapOf(id.value to null))
  }
}
