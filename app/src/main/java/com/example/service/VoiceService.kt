package com.example.service

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class VoiceService(context: Context) : TextToSpeech.OnInitListener {
    private val TAG = "VoiceService"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to instantiate TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale.US
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
            Log.d(TAG, "TTS initialized successfully")
        } else {
            Log.e(TAG, "TTS initialization failed with status: $status")
        }
    }

    fun speak(
        text: String,
        speechRate: Float = 1.0f,
        speechPitch: Float = 1.0f,
        languageCode: String = "English"
    ) {
        if (!isInitialized || tts == null) {
            Log.w(TAG, "TTS not ready yet")
            return
        }

        try {
            val locale = when (languageCode.lowercase()) {
                "hindi" -> Locale("hi", "IN")
                "marathi" -> Locale("mr", "IN")
                "japanese" -> Locale.JAPANESE
                "korean" -> Locale.KOREAN
                "spanish" -> Locale("es", "ES")
                "french" -> Locale.FRENCH
                "german" -> Locale.GERMAN
                else -> Locale.US
            }

            tts?.language = locale
            tts?.setSpeechRate(speechRate.coerceIn(0.5f, 2.0f))
            tts?.setPitch(speechPitch.coerceIn(0.5f, 2.0f))

            val params = Bundle().apply {
                putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "JARVIS_VOICE_${System.currentTimeMillis()}")
            }
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "JARVIS_VOICE")
        } catch (e: Exception) {
            Log.e(TAG, "Error in speak()", e)
            _isSpeaking.value = false
        }
    }

    fun stop() {
        try {
            tts?.stop()
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
            _isSpeaking.value = false
        } catch (e: Exception) {
            Log.e(TAG, "Error shutting down TTS", e)
        }
    }
}
