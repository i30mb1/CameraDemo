package com.example.camerademo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.lifecycle.lifecycleScope
import com.example.camerademo.databinding.ActivityMainBinding
import com.example.camerademo.usecase.ImageAnalysisUseCase
import com.example.camerademo.usecase.ImageCaptureUseCase
import com.example.camerademo.usecase.PreviewUseCase
import com.example.camerademo.usecase.VideoCaptureUseCase
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val permission = AppPermissions(this) { setup() }
    private var isRecording = false
    private val preview = PreviewUseCase()
    private val analysis = ImageAnalysisUseCase()
    private val videoCapture = VideoCaptureUseCase()
    private val imageCapture = ImageCaptureUseCase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permission.run()
    }

    private fun setup() = lifecycleScope.launch {
        preview.bind(binding.previewView.surfaceProvider)
        analysis.bind {
            binding.analyzer.post {
                binding.analyzer.setImageBitmap(it)
            }
        }

        binding.photo.setOnClickListener { takePhoto() }
        binding.video.setOnClickListener { if (!isRecording) takeVideo() }
        val instance = ProcessCameraProvider.awaitInstance(this@MainActivity)
        instance.bindToLifecycle(
            this@MainActivity,
            CameraSelector.DEFAULT_FRONT_CAMERA,
            preview.useCase,
            videoCapture.useCase,
            analysis.useCase,
            imageCapture.useCase,
        )
    }

    private fun takeVideo() {
        videoCapture.start(this)
    }

    private fun takePhoto() {
        imageCapture.start(this)
    }
}
