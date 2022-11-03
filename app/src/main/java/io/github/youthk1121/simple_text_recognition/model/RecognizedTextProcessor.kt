package io.github.youthk1121.simple_text_recognition.model

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecognizedTextProcessor {

    private val _processedText = MutableStateFlow("")
    val processedText = _processedText.asStateFlow()

    private val _recognizedTextQueue = ArrayDeque<String>()

    fun put(text: String) {
        while (_recognizedTextQueue.size >= 3) {
            Log.d(this::class.simpleName, "dequeue")
            _recognizedTextQueue.removeFirst()
        }
        _recognizedTextQueue.add(text)
        if (isQueuedTextValid())  {
            Log.d(this::class.simpleName, "Valid text: ${_recognizedTextQueue.last().take(10)}")
            _processedText.value = _recognizedTextQueue.last()
        }
    }

    private fun isQueuedTextValid(): Boolean {
        return _recognizedTextQueue.size >= RECOGNITION_COUNT
                && _recognizedTextQueue.toSet().size == 1
    }

    companion object {
        const val RECOGNITION_COUNT = 3
    }
}