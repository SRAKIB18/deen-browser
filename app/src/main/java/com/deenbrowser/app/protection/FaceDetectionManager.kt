package com.deenbrowser.app.protection

import android.graphics.Bitmap
import android.graphics.Rect
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FaceDetectionManager {

    private val detectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.15f)
        .build()

    private val detector: FaceDetector by lazy {
        FaceDetection.getClient(detectorOptions)
    }

    suspend fun detectFaces(bitmap: Bitmap): List<Rect> = suspendCancellableCoroutine { continuation ->
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            detector.process(image)
                .addOnSuccessListener { faces: List<Face> ->
                    val boundingBoxes = faces.map { it.boundingBox }
                    if (continuation.isActive) {
                        continuation.resume(boundingBoxes)
                    }
                }
                .addOnFailureListener {
                    if (continuation.isActive) {
                        continuation.resume(emptyList())
                    }
                }
        } catch (e: Exception) {
            if (continuation.isActive) {
                continuation.resume(emptyList())
            }
        }
    }

    fun close() {
        try {
            detector.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}