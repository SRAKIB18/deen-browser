package com.deenbrowser.app.protection

import android.graphics.RectF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.deenbrowser.app.data.models.DetectionMode
import com.deenbrowser.app.ui.theme.DeenForestPrimary

@Composable
fun FaceBlurOverlayCanvas(
    mappedFaces: List<RectF>,
    detectionMode: DetectionMode,
    hideWholeImage: Boolean,
    blurRadius: Float,
    modifier: Modifier = Modifier
) {
    if (detectionMode == DetectionMode.OFF || mappedFaces.isEmpty()) return

    Canvas(modifier = modifier.fillMaxSize()) {
        mappedFaces.forEach { face ->
            val initialWidth = face.width()
            val initialHeight = face.height()

            if (initialWidth > 0 && initialHeight > 0) {
                // If hideWholeImage is enabled, expand padding to cover the containing photo
                val expandX = if (hideWholeImage) initialWidth * 1.2f else 0f
                val expandY = if (hideWholeImage) initialHeight * 1.2f else 0f

                val left = (face.left - expandX).coerceAtLeast(0f)
                val top = (face.top - expandY).coerceAtLeast(0f)
                val width = initialWidth + (expandX * 2)
                val height = initialHeight + (expandY * 2)

                when (detectionMode) {
                    DetectionMode.FACE_BLUR -> {
                        drawRoundRect(
                            color = DeenForestPrimary.copy(alpha = 0.88f),
                            topLeft = Offset(left, top),
                            size = Size(width, height),
                            cornerRadius = CornerRadius(16f, 16f)
                        )
                        drawRoundRect(
                            color = Color.White.copy(alpha = 0.25f),
                            topLeft = Offset(left + 2, top + 2),
                            size = Size(width - 4, height - 4),
                            cornerRadius = CornerRadius(14f, 14f)
                        )
                    }
                    DetectionMode.STRICT -> {
                        drawRoundRect(
                            color = Color.Black,
                            topLeft = Offset(left, top),
                            size = Size(width, height),
                            cornerRadius = CornerRadius(12f, 12f)
                        )
                    }
                    DetectionMode.CUSTOM -> {
                        drawRoundRect(
                            color = DeenForestPrimary.copy(alpha = 0.9f),
                            topLeft = Offset(left, top),
                            size = Size(width, height),
                            cornerRadius = CornerRadius(16f, 16f)
                        )
                    }
                    DetectionMode.OFF -> {}
                }
            }
        }
    }
}