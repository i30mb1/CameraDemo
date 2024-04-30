package com.example.camerademo.usecase

import kotlin.time.Duration.Companion.seconds
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.MirrorMode
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.video.VideoRecordEvent.Finalize

class VideoCaptureUseCase {
    private val recorder: Recorder = Recorder.Builder().setQualitySelector(
        QualitySelector.from(
            Quality.HD,
            FallbackStrategy.lowerQualityOrHigherThan(Quality.HD),
        )
    )
        .build()


    val useCase: VideoCapture<Recorder> = VideoCapture.Builder(recorder)
        .setMirrorMode(MirrorMode.MIRROR_MODE_ON_FRONT_ONLY)
        .build()

    @SuppressLint("CheckResult")
    fun start(context: Context) {
        val content = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "Video")
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/cameraX")
        }
        val output = MediaStoreOutputOptions.Builder(
            context.contentResolver,
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        ).setContentValues(content).setDurationLimitMillis(3.seconds.inWholeMilliseconds)
            .build()

        useCase.output.prepareRecording(context, output)
            .start(context.mainExecutor) { event: VideoRecordEvent ->
                when (event) {
                    is Finalize -> {
                        if (event.error == Finalize.ERROR_DURATION_LIMIT_REACHED) {
                            Toast.makeText(context, "video success", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "video error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
    }
}
