package com.example.camerademo.usecase

import java.util.concurrent.Executors
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis

class ImageAnalysisUseCase {
    val useCase: ImageAnalysis = ImageAnalysis.Builder()
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
        .build()


    fun bind(listener: (Bitmap) -> Unit) {
        useCase.setAnalyzer(Executors.newSingleThreadExecutor()) {
            listener(it.toBitmap())
        }
    }
}
