package io.github.youthk1121.simple_text_recognition.ui.component

import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import java.util.concurrent.Executor

@Composable
fun SimpleTextRecognizer(modifier: Modifier = Modifier, onRecognised: (text: String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraController = remember { LifecycleCameraController(context) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { factoryContext ->
            PreviewView(factoryContext).apply {
                setupCamera(
                    previewView = this,
                    lifecycleOwner = lifecycleOwner,
                    cameraProviderFuture = cameraProviderFuture,
                    cameraController = cameraController,
                    executor = ContextCompat.getMainExecutor(context),
                    onRecognised = onRecognised
                )
            }
        }
    )
}

private fun setupCamera(
    previewView: PreviewView,
    lifecycleOwner: LifecycleOwner,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    cameraController: LifecycleCameraController,
    executor: Executor,
    onRecognised: (text: String) -> Unit
) {
    cameraProviderFuture.addListener({
        previewView.controller = cameraController
        cameraController.unbind()
        cameraController.bindToLifecycle(lifecycleOwner)
        cameraController.cameraSelector =
            CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        cameraController.clearImageAnalysisAnalyzer()

        val recognizer =
            TextRecognition.getClient(JapaneseTextRecognizerOptions.Builder().build())
        cameraController.setImageAnalysisAnalyzer(executor,
            MlKitAnalyzer(
                listOf(recognizer),
                COORDINATE_SYSTEM_VIEW_REFERENCED,
                executor
            ) { result ->
                val text = result.getValue(recognizer)
                if (text != null) {
                    onRecognised(text.text)
                }
            })
    }, executor)
}