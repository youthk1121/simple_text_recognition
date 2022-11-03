package io.github.youthk1121.simple_text_recognition.model

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class RecognizedTextProcessor {

    private val _processedText = MutableStateFlow("")
    val processedText = _processedText.asStateFlow()

    private val _recognizedTextLinesQueue = ArrayDeque<List<String>>()

    fun put(text: String) {
        while (_recognizedTextLinesQueue.size >= RECOGNITION_COUNT) {
            _recognizedTextLinesQueue.removeFirst()
        }
        val lines = text.split(lineSeparator)
        _recognizedTextLinesQueue.add(lines)
        val validLines = validLines(lines)
        if (validLines.isNotEmpty()) {
            _processedText.value = validLines.joinToString(separator = lineSeparator)
        }
    }

    private fun validLines(textLines: List<String>): List<String> {
        if (_recognizedTextLinesQueue.size < RECOGNITION_COUNT) return emptyList()
        Log.d(this::class.simpleName, "First text: ${textLines.first()}")
        return textLines.filter { line -> _recognizedTextLinesQueue.all { it.contains(line) } }
    }

    companion object {
        private const val RECOGNITION_COUNT = 3
        private val lineSeparator = System.lineSeparator()
    }
}