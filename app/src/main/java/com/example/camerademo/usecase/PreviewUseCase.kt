package com.example.camerademo.usecase

import androidx.camera.core.Preview

class PreviewUseCase {
    val useCase: Preview = Preview.Builder().build()

    fun bind(surface: Preview.SurfaceProvider) {
        useCase.setSurfaceProvider(surface)
    }
}
