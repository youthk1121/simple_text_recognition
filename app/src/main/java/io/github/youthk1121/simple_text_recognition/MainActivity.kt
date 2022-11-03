package io.github.youthk1121.simple_text_recognition

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.youthk1121.simple_text_recognition.model.RecognizedTextProcessor
import io.github.youthk1121.simple_text_recognition.ui.component.RequestCameraPermission
import io.github.youthk1121.simple_text_recognition.ui.component.SimpleTextRecognizer
import io.github.youthk1121.simple_text_recognition.ui.theme.SimpleTextRecognitionTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SimpleTextRecognitionTheme {
                val permissionGranted = remember { mutableStateOf(false) }
                RequestCameraPermission { isGranted ->
                    if (isGranted) {
                        Toast.makeText(this, "Camera permission granted!", Toast.LENGTH_SHORT)
                            .show()
                        permissionGranted.value = true
                    } else {
                        permissionGranted.value = false
                    }
                }
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    if (permissionGranted.value) {
                        TextRecognizer()
                    } else {
                        PermissionError()
                    }
                }
            }
        }
    }
}

@Composable
fun TextRecognizer() {
    val textProcessor = remember { RecognizedTextProcessor() }

    Box {
        SimpleTextRecognizer(onRecognised = { text ->
            textProcessor.put(text)
        })
        Text(
            modifier = Modifier
                .align(alignment = Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .height(200.dp),
            text = textProcessor.processedText.collectAsState().value
        )
    }

}

@Composable
fun PermissionError() {
    Text(text = "Camera Permission is not granted")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SimpleTextRecognitionTheme {
        PermissionError()
    }
}