package com.pulasthi.tapit.ui.qr

import android.os.SystemClock
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun QrCameraPreview(
    enabled: Boolean,
    flashEnabled: Boolean,
    onQrCodeScanned: (String) -> Unit,
    onCameraError: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()
    val analysisExecutor = remember { Executors.newSingleThreadExecutor() }
    val barcodeScanner = remember { BarcodeScanning.getClient() }
    val scanThrottle = remember { QrScanThrottle(minIntervalMs = 400L) }
    val onQrCodeScannedState = rememberUpdatedState(onQrCodeScanned)
    val onCameraErrorState = rememberUpdatedState(onCameraError)
    var cameraControl by remember { mutableStateOf<androidx.camera.core.CameraControl?>(null) }

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            scope.launch(Dispatchers.IO) {
                runCatching {
                    ProcessCameraProvider.getInstance(context).get().unbindAll()
                }
                barcodeScanner.close()
                analysisExecutor.shutdown()
            }
        }
    }

    LaunchedEffect(enabled, lifecycleOwner) {
        if (!enabled) {
            withContext(Dispatchers.IO) {
                runCatching {
                    ProcessCameraProvider.getInstance(context).get().unbindAll()
                }
            }
            cameraControl = null
            return@LaunchedEffect
        }

        try {
            val cameraProvider = withContext(Dispatchers.IO) {
                ProcessCameraProvider.getInstance(context).get()
            }

            withContext(Dispatchers.Main) {
                cameraProvider.unbindAll()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
                    processImageProxy(
                        imageProxy = imageProxy,
                        barcodeScanner = barcodeScanner,
                        scanThrottle = scanThrottle,
                        onQrCodeScanned = { payload ->
                            // ML Kit listener thread: hop to main once per throttle window.
                            scope.launch(Dispatchers.Main.immediate) {
                                onQrCodeScannedState.value(payload)
                            }
                        },
                    )
                }

                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis,
                )
                cameraControl = camera.cameraControl
                camera.cameraControl.enableTorch(flashEnabled)
            }
        } catch (exception: Exception) {
            onCameraErrorState.value(exception.message ?: "Unable to start camera")
        }
    }

    LaunchedEffect(flashEnabled, cameraControl) {
        cameraControl?.enableTorch(flashEnabled)
    }

    AndroidView(
        factory = { previewView },
        modifier = modifier,
    )
}

private class QrScanThrottle(private val minIntervalMs: Long) {
    private var lastEmitAt = 0L
    private val hasEmitted = AtomicBoolean(false)

    fun shouldAnalyzeFrame(): Boolean {
        val now = SystemClock.elapsedRealtime()
        if (now - lastEmitAt < minIntervalMs) return false
        lastEmitAt = now
        return true
    }

    fun tryEmit(payload: String, onEmit: (String) -> Unit): Boolean {
        if (payload.isBlank()) return false
        if (!hasEmitted.compareAndSet(false, true)) return false
        onEmit(payload)
        return true
    }
}

@androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
private fun processImageProxy(
    imageProxy: ImageProxy,
    barcodeScanner: com.google.mlkit.vision.barcode.BarcodeScanner,
    scanThrottle: QrScanThrottle,
    onQrCodeScanned: (String) -> Unit,
) {
    if (!scanThrottle.shouldAnalyzeFrame()) {
        imageProxy.close()
        return
    }

    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        imageProxy.close()
        return
    }

    val inputImage = InputImage.fromMediaImage(
        mediaImage,
        imageProxy.imageInfo.rotationDegrees,
    )

    barcodeScanner.process(inputImage)
        .addOnSuccessListener { barcodes ->
            val payload = barcodes
                .firstOrNull { it.format == Barcode.FORMAT_QR_CODE || it.rawValue != null }
                ?.rawValue
            scanThrottle.tryEmit(payload.orEmpty(), onQrCodeScanned)
        }
        .addOnFailureListener {
            // Ignore transient scan failures; preview continues.
        }
        .addOnCompleteListener {
            imageProxy.close()
        }
}
