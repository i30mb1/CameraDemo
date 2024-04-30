package com.example.camerademo

import kotlin.time.Duration.Companion.seconds
import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController.IMAGE_ANALYSIS
import androidx.camera.view.CameraController.IMAGE_CAPTURE
import androidx.camera.view.CameraController.VIDEO_CAPTURE
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.video.AudioConfig
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.camerademo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var controller: LifecycleCameraController
    private val permission = AppPermissions(this) { setup() }
    private var isRecording = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        edgeToEdge()

        permission.run()
    }

    private fun edgeToEdge() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setup() {
        controller = LifecycleCameraController(this)
        controller.bindToLifecycle(this)
        controller.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        controller.setEnabledUseCases(VIDEO_CAPTURE or IMAGE_ANALYSIS or IMAGE_CAPTURE)
        binding.previewView.controller = controller
        binding.photo.setOnClickListener { takePhoto() }
        binding.video.setOnClickListener { if (!isRecording) takeVideo() }
        controller.setImageAnalysisAnalyzer(mainExecutor) { image ->
            binding.analyzer.setImageBitmap(image.toBitmap())
            image.close()
        }
    }

    private fun takeVideo() {
        val content = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "video")
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/cameraX")
        }
        val output = MediaStoreOutputOptions
            .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
            .setContentValues(content)
            .setDurationLimitMillis(3.seconds.inWholeMilliseconds)
            .build()
        controller.startRecording(
            output,
            AudioConfig.AUDIO_DISABLED,
            mainExecutor
        ) { event: VideoRecordEvent ->
            when (event) {
                is VideoRecordEvent.Start -> isRecording = true
                is VideoRecordEvent.Finalize -> {
                    isRecording = false
                    if (event.hasError() && event.error == VideoRecordEvent.Finalize.ERROR_DURATION_LIMIT_REACHED) {
                        showMessage("Video Success")
                    } else {
                        showMessage("Video Error")
                    }
                }
            }
        }
    }

    private fun takePhoto() {
        val content = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Photo")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/cameraX")
        }
        val output = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            content
        ).build()
        controller.takePicture(output, mainExecutor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                showMessage("Photo Success")
            }

            override fun onError(exception: ImageCaptureException) {
                showMessage("Photo Error")
            }
        }
        )
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
