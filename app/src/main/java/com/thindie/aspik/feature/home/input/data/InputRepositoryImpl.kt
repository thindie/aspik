package com.thindie.aspik.feature.home.input.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.aspik.util.NetworkUtils
import com.thindie.engine.core.Log
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import okhttp3.OkHttpClient

internal class InputRepositoryImpl(
  private val context: Context,
  private val client: OkHttpClient,
) : InputRepository {
  private val textChannel =
    MutableSharedFlow<String>(
      replay = 0,
      extraBufferCapacity = 16,
      onBufferOverflow = BufferOverflow.SUSPEND,
    )

  private var currentRecognizer: SpeechRecognizer? = null
  private val accumulatedText = StringBuilder()

  override fun listen() {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) return

    currentRecognizer?.stopListening()

    val intent =
      Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
          RecognizerIntent.EXTRA_LANGUAGE_MODEL,
          RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
        )
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true)
        putExtra(
          RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
          500,
        )
      }

    val recognizer = currentRecognizer ?: SpeechRecognizer.createSpeechRecognizer(context).also { currentRecognizer = it }
    Log.d(message = { "Starting speech recognition" })
    recognizer.setRecognitionListener(createListener(recognizer))
    recognizer.startListening(intent)
  }

  private fun createListener(recognizer: SpeechRecognizer): RecognitionListener {
    return object : RecognitionListener {
      override fun onResults(results: Bundle?) {
        val texts = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        Log.d(message = { "onResults: ${texts?.size} result(s)" })
        if (texts.isNullOrEmpty()) {
          Log.e(message = { "onResults returned empty list" })
        } else {
          val text = texts.firstOrNull() ?: return
          Log.d(message = { "onResults: '$text'" })
          accumulatedText.append(text)
          textChannel.tryEmit(accumulatedText.toString())
        }
        recognizer.stopListening()
      }

      override fun onError(error: Int) {
        val errorMsg =
          when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error"
            SpeechRecognizer.ERROR_CLIENT -> "Client side error"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Insufficient permissions"
            SpeechRecognizer.ERROR_NETWORK -> "Network error"
            SpeechRecognizer.ERROR_NO_MATCH -> "No match found"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Recognizer busy"
            SpeechRecognizer.ERROR_SERVER -> "Server error"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timeout"
            else -> "Unknown error ($error)"
          }
        Log.e(message = { "onError: $errorMsg" })
        recognizer.stopListening()
      }

      override fun onReadyForSpeech(params: Bundle?) {
        Log.d(message = { "Ready for speech input" })
      }

      override fun onBeginningOfSpeech() {
        Log.d(message = { "Speech detected (beginning)" })
      }

      override fun onRmsChanged(rmsdB: Float) {}

      override fun onBufferReceived(buffer: ByteArray?) {
        Log.d(message = { "onBufferReceived: ${buffer?.size} bytes" })
      }

      override fun onPartialResults(partialResults: Bundle?) {
        val texts = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        Log.d(message = { "onPartialResults: ${texts?.size} result(s)" })
        if (texts.isNullOrEmpty()) {
          Log.e(message = { "onPartialResults returned empty list" })
        } else {
          val text = texts.firstOrNull() ?: return
          Log.d(message = { "onPartialResults: '$text'" })
          accumulatedText.append(text)
          textChannel.tryEmit(accumulatedText.toString())
        }
      }

      override fun onEvent(
        eventType: Int,
        params: Bundle?,
      ) {
        Log.d(message = { "onEvent: type=$eventType" })
      }

      override fun onEndOfSpeech() {
        Log.d(message = { "Speech ended (end of speech)" })
        // Do NOT destroy here — onResults() will handle cleanup.
        // Destroying in onEndOfSpeech races with onResults and causes
        // the recognizer to terminate before emitting text.
      }
    }
  }

  override val convertedText: Flow<String> = textChannel.asSharedFlow()

  override suspend fun sendText(text: String) {
    NetworkUtils.sendText(client, context, text).getOrThrow()
  }

  override fun deleteAccumulated() {
    accumulatedText.clear()
    textChannel.tryEmit("")
  }

  override fun listenInvalidated() {
    currentRecognizer?.destroy()
    currentRecognizer = null
  }
}
