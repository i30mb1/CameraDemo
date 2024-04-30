package com.example.camerademo.usecase

import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException

class ImageCaptureUseCase {
    val useCase = ImageCapture.Builder().build()

    fun start(context: Context) {
        val content = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Photo")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/cameraX")
        }
        val output: ImageCapture.OutputFileOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            content
        )
            .build()

        useCase.takePicture(
            output,
            context.mainExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(context, "photo success", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(context, "photo error", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
