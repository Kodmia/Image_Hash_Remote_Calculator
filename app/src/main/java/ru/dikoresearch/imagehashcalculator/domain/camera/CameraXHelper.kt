package ru.dikoresearch.imagehashcalculator.domain.camera

import android.app.Activity
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executors
import kotlin.math.abs

private const val RATIO_4_3_VALUE = 4.0 / 3.0
private const val RATIO_16_9_VALUE = 16.0 / 9.0

class CameraXHelper(
    private val caller: Any,
    private val previewView: PreviewView,
    private val filesDirectory: File,
    private val onPictureTaken: ((Uri?) -> Unit)? = null,
    private val builderPreview: Preview.Builder? = null,
    private val builderImageCapture: ImageCapture.Builder? = null,
    private val onError: ((Throwable) -> Unit)? = null,
) {
    private val context by lazy {
        when (caller) {
            is Activity -> caller
            is Fragment -> caller.activity ?: throw Exception("Fragment not attached to activity")
            else -> throw Exception("Can't get a context from caller")
        }
    }

    private var imagePreview: Preview? = null
    private var imageCapture: ImageCapture? = null

    private var lensFacing = CameraSelector.LENS_FACING_BACK
    private val executor = Executors.newSingleThreadExecutor()

    private var camera: Camera? = null

    fun start() {
        if (caller !is LifecycleOwner) throw Exception("Caller is not lifecycle owner")
        previewView.post {
            startCamera()
        }
    }

    fun stop(){
        ProcessCameraProvider.getInstance(context).get().unbindAll()
    }

    fun takePicture(){
        val dir = filesDirectory

        if (!dir.exists()){
            dir.mkdirs()
        }

        val fileName = "IMG_${System.currentTimeMillis()}.jpeg"

        val file = File(dir, fileName)

        val metadata = ImageCapture.Metadata().apply {
            isReversedHorizontal = lensFacing == CameraSelector.LENS_FACING_FRONT
        }

        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file)
            .setMetadata(metadata)
            .build()

        imageCapture?.takePicture(
            outputFileOptions,
            executor,
            object: ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    onPictureTaken?.invoke(
                        outputFileResults.savedUri
                    )
                }

                override fun onError(exception: ImageCaptureException) {
                    onError?.invoke(exception)
                }
            }
        )
    }

    private fun createImagePreview() =
        (builderPreview ?: Preview.Builder()
            .setTargetAspectRatio(aspectRatio()))
            .setTargetRotation(previewView.display.rotation)
            .build()
            .apply {
                setSurfaceProvider(previewView.surfaceProvider)
            }

    private fun createImageCapture() =
        (builderImageCapture ?: ImageCapture.Builder()
            .setTargetAspectRatio(aspectRatio()))
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()

    private fun startCamera(){
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        val cameraProvideFuture = ProcessCameraProvider.getInstance(context)
        cameraProvideFuture.addListener({
            try {
                val cameraProvider: ProcessCameraProvider = cameraProvideFuture.get()
                imagePreview = createImagePreview()


                imageCapture = createImageCapture()

                cameraProvider.unbindAll()

                if (camera != null){
                    camera!!.cameraInfo.cameraState.removeObservers(caller as LifecycleOwner)
                }

                camera = cameraProvider.bindToLifecycle(
                    caller as LifecycleOwner,
                    cameraSelector,
                    imagePreview,
                    imageCapture
                )

                imagePreview?.setSurfaceProvider(previewView.surfaceProvider)
                observeCameraState(camera?.cameraInfo!!)
            }
            catch (e: Exception){
                onError?.invoke(e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    private fun observeCameraState(cameraInfo: CameraInfo){
        cameraInfo.cameraState.observe(caller as LifecycleOwner) { cameraState ->
            run {
                when (cameraState.type) {
                    CameraState.Type.PENDING_OPEN -> {
                        // Ask the user to close other camera apps
                        Log.e("", "CameraState: Pending Open")
                    }
                    CameraState.Type.OPENING -> {
                        // Show the Camera UI
                        Log.e("", "CameraState: Opening")
                    }
                    CameraState.Type.OPEN -> {
                        // Setup Camera resources and begin processing
                        Log.e("", "CameraState: Open")
                    }
                    CameraState.Type.CLOSING -> {
                        // Close camera UI
                        Log.e("", "CameraState: Closing")
                    }
                    CameraState.Type.CLOSED -> {
                        // Free camera resources
                        Log.e("", "CameraState: Closed")
                    }
                }
            }

            cameraState.error?.let { error ->
                when (error.code) {
                    // Open errors
                    CameraState.ERROR_STREAM_CONFIG -> {
                        // Make sure to setup the use cases properly
                        Log.e("", "Stream config error")
                    }
                    // Opening errors
                    CameraState.ERROR_CAMERA_IN_USE -> {
                        // Close the camera or ask user to close another camera app that's using the
                        // camera
                        Log.e("", "Camera in use")
                    }
                    CameraState.ERROR_MAX_CAMERAS_IN_USE -> {
                        // Close another open camera in the app, or ask the user to close another
                        // camera app that's using the camera
                        Log.e("", "Max cameras in use")
                    }
                    CameraState.ERROR_OTHER_RECOVERABLE_ERROR -> {

                        Log.e("", "Other recoverable error")
                    }
                    // Closing errors
                    CameraState.ERROR_CAMERA_DISABLED -> {
                        // Ask the user to enable the device's cameras

                        Log.e("", "Camera disabled")
                    }
                    CameraState.ERROR_CAMERA_FATAL_ERROR -> {
                        // Ask the user to reboot the device to restore camera function
                        Log.e("", "Fatal error")
                    }
                    // Closed errors
                    CameraState.ERROR_DO_NOT_DISTURB_MODE_ENABLED -> {
                        // Ask the user to disable the "Do Not Disturb" mode, then reopen the camera
                        Log.e("", "Do not disturb mode enabled")
                    }
                }
            }
        }
    }

    private fun aspectRatio(): Int {
        @Suppress("DEPRECATION")
        val metrics = DisplayMetrics().also { previewView.display.getRealMetrics(it) }
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        val previewRatio = width.coerceAtLeast(height).toDouble() / width.coerceAtMost(height)

        if (abs(previewRatio - RATIO_4_3_VALUE) <= abs(previewRatio - RATIO_16_9_VALUE)){
            return AspectRatio.RATIO_4_3
        }
        return AspectRatio.RATIO_16_9
    }
}