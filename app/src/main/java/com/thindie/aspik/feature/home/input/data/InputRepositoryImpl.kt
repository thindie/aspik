package com.thindie.aspik.feature.home.input.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.thindie.aspik.feature.home.input.domain.InputRepository
import com.thindie.aspik.util.NetworkUtils
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
      onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )

  override fun listen() {
    if (!SpeechRecognizer.isRecognitionAvailable(context)) return

    val intent =
      Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
          RecognizerIntent.EXTRA_LANGUAGE_MODEL,
          RecognizerIntent.LANGUAGE_MODEL_FREE_FORM,
        )
        putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
      }

    val recognizer = SpeechRecognizer.createSpeechRecognizer(context)
    recognizer.setRecognitionListener(createListener(recognizer))
    recognizer.startListening(intent)
  }

  private fun createListener(recognizer: SpeechRecognizer): RecognitionListener {
    return object : RecognitionListener {
      override fun onResults(results: Bundle?) {
        results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let { text ->
          textChannel.tryEmit(text)
        }
        recognizer.stopListening()
      }

      override fun onError(error: Int) {
        recognizer.stopListening()
      }

      override fun onReadyForSpeech(params: Bundle?) {}

      override fun onBeginningOfSpeech() {}

      override fun onRmsChanged(rmsdB: Float) {}

      override fun onBufferReceived(buffer: ByteArray?) {}

      override fun onPartialResults(partialResults: Bundle?) {
        partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)?.firstOrNull()?.let { text ->
          textChannel.tryEmit(text)
        }
      }

      override fun onEvent(
        eventType: Int,
        params: Bundle?,
      ) {}

      override fun onEndOfSpeech() {
        recognizer.stopListening()
      }
    }
  }

  override val convertedText: Flow<String> = textChannel.asSharedFlow()

  override suspend fun sendText(text: String) {
    NetworkUtils.sendText(client, context, text).getOrThrow()
  }

  override fun setNote() {
    // TODO: implement note setting logic
  }
}
