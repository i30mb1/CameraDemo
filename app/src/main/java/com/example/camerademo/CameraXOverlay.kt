package com.example.camerademo

import java.util.concurrent.Executors
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.Rect
import android.media.ThumbnailUtils
import android.os.Handler
import android.os.Looper
import androidx.camera.core.CameraEffect
import androidx.camera.core.ImageProcessor
import androidx.camera.core.imagecapture.RgbaImageProxy
import androidx.camera.effects.Frame
import androidx.camera.effects.OverlayEffect
import androidx.core.graphics.withRotation

class CameraXOverlay(context: Context) {

    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.face)
    private var scaledBitmap: Bitmap? = null

    fun getOverlay(): CameraEffect {
        val overlay = OverlayEffect(
            CameraEffect.PREVIEW or CameraEffect.IMAGE_CAPTURE or CameraEffect.VIDEO_CAPTURE,
            0,
            Handler(Looper.getMainLooper()),
        ) {}
        overlay.setOnDrawListener { frame: Frame ->
            val bitmap = getScaledBitmap(frame)
            frame.overlayCanvas.withRotation(90f, bitmap.width / 2f, bitmap.height / 2f) {
                drawBitmap(bitmap, 0f, 0f, null)
            }
            true
        }
        return overlay
    }

    /**
     * оверлей будет работать только для IMAGE_CAPTURE
     */
    @SuppressLint("RestrictedApi")
    fun getOverlayForImageCapture(): CameraEffect {
        val imageProcessor = ImageProcessor { _ ->
            val output = RgbaImageProxy(bitmap, Rect(), 0, Matrix(), 0)
            ImageProcessor.Response { output }
        }

        class MyEffect : CameraEffect(
            IMAGE_CAPTURE,
            Executors.newSingleThreadExecutor(),
            imageProcessor,
            {}
        )
        return MyEffect()
    }

    private fun getScaledBitmap(frame: Frame): Bitmap {
        return scaledBitmap ?: kotlin.run {
            val thumbnail: Bitmap = ThumbnailUtils.extractThumbnail(
                bitmap,
                frame.overlayCanvas.width,
                frame.overlayCanvas.height,
            )
            scaledBitmap = thumbnail
            thumbnail
        }
    }
}
