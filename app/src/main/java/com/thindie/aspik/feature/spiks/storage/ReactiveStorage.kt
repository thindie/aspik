package com.thindie.aspik.feature.spiks.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

internal class ReactiveStorage(
  context: Context,
) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  private val _cache = MutableStateFlow<Map<String, String>>(emptyMap())

  val cache: StateFlow<Map<String, String>> = _cache.asStateFlow()

  init {
    val map = mutableMapOf<String, String>()
    for ((key, value) in prefs.all) {
      if (value is String) map[key] = value
    }
    _cache.value = map
  }

  fun putAll(entries: Map<String, String?>) {
    prefs.edit {
      entries.forEach { (key, value) ->
        if (value == null) {
          remove(key)
        } else {
          putString(key, value)
        }
      }
    }

    _cache.update { current ->
      val merged = current.toMutableMap()
      entries.forEach { (key, value) ->
        if (value == null) {
          merged.remove(key)
        } else {
          merged[key] = value
        }
      }
      merged
    }
  }

  fun get(key: String): String? = _cache.value[key]

  companion object {
    private const val PREFS_NAME = "spiks_prefs"
  }
}
