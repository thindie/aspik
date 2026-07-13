package com.thindie.aspik.util

import android.content.Context
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object NetworkUtils {
  private const val PREFS_NAME = "aspik_prefs"
  private const val KEY_SERVER_IP = "server_ip"
  private const val KEY_SERVER_PORT = "server_port"
  private const val DEFAULT_IP = "192.168.0.1"
  private const val DEFAULT_PORT = "8080"

  fun saveServerAddress(
    context: Context,
    ip: String,
    port: String,
  ) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().apply {
      putString(KEY_SERVER_IP, ip.ifBlank { DEFAULT_IP })
      putString(KEY_SERVER_PORT, port.ifBlank { DEFAULT_PORT })
      apply()
    }
  }

  fun getServerAddress(context: Context): Pair<String, String> {
    val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    return Pair(
      prefs.getString(KEY_SERVER_IP, DEFAULT_IP) ?: DEFAULT_IP,
      prefs.getString(KEY_SERVER_PORT, DEFAULT_PORT) ?: DEFAULT_PORT,
    )
  }

  suspend fun sendText(
    client: OkHttpClient,
    context: Context,
    text: String,
  ): Result<Unit> {
    return try {
      val (ip, port) = getServerAddress(context)
      val url = "http://$ip:$port/api/speech"

      val request =
        Request.Builder()
          .url(url)
          .post(text.toRequestBody("text/plain".toMediaType()))
          .build()

      withContext(Dispatchers.IO) {
        client.newCall(request).execute().use { response ->
          if (response.isSuccessful) {
            Result.success(Unit)
          } else {
            Result.failure(
              IOException("Server returned ${response.code}"),
            )
          }
        }
      }
    } catch (ce: CancellationException) {
      throw ce
    } catch (e: Exception) {
      Result.failure(e)
    }
  }
}
