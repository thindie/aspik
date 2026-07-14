package com.thindie.aspik.util

import android.content.Context
import com.thindie.engine.core.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object NetworkUtils {
  private const val PREFS_NAME = "aspik_prefs"
  private const val KEY_SERVER_IP = "server_ip"
  private const val KEY_SERVER_PORT = "server_port"
  private const val DEFAULT_IP = "192.168.1.90"
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

  private var webSocket: WebSocket? = null

  @Volatile private var isConnected = false

  fun connectWebSocket(
    client: OkHttpClient,
    context: Context,
    onConnectResult: (Boolean) -> Unit = {},
  ): WebSocket {
    val (ip, port) = getServerAddress(context)
    val url = "ws://$ip:$port/speech"

    val listener =
      object : WebSocketListener() {
        override fun onOpen(
          webSocket: WebSocket,
          response: Response,
        ) {
          this@NetworkUtils.webSocket = webSocket
          isConnected = true
          onConnectResult(true)
        }

        override fun onMessage(
          webSocket: WebSocket,
          text: String,
        ) {
          // Сервер может что-то отвечать, если нужно
        }

        override fun onFailure(
          webSocket: WebSocket,
          t: Throwable,
          response: Response?,
        ) {
          Log.e(message = { "${t.cause} ${t.message}" })
          this@NetworkUtils.webSocket = null
          isConnected = false
          onConnectResult(false)
        }

        override fun onClosing(
          webSocket: WebSocket,
          code: Int,
          reason: String,
        ) {
          Log.d(message = { "websocket: onClosing - reason $reason" })
          this@NetworkUtils.webSocket = null
          isConnected = false
        }
      }
    return client.newWebSocket(Request.Builder().url(url).build(), listener)
  }

  suspend fun sendText(
    client: OkHttpClient,
    context: Context,
    text: String,
  ): Result<Unit> =
    withContext(Dispatchers.IO) {
      try {
        var ws = webSocket
        if (ws == null || !isConnected) {
          val latch = CountDownLatch(1)
          ws =
            connectWebSocket(client, context) { success ->
              latch.countDown()
            }
          latch.await(3, TimeUnit.SECONDS)
        }

        if (isConnected) {
          if (ws.send(text)) {
            Result.success(Unit)
          } else {
            Result.failure(IOException("WebSocket buffer overflow or closed"))
          }
        } else {
          Result.failure(IOException("WebSocket is not connected yet"))
        }
      } catch (ce: CancellationException) {
        throw ce
      } catch (e: Exception) {
        Result.failure(e)
      }
    }
}
