package com.daiatech.chitralekhan

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import com.daiatech.chitralekhan.components.drawChitraLekhanStrokes
import com.daiatech.chitralekhan.models.DrawMode
import com.daiatech.chitralekhan.models.Point

@Composable
fun ChitraLekhanCanvas(
    chitraLekhan: ChitraLekhan,
    modifier: Modifier = Modifier
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var zoomOffset by remember { mutableStateOf(Offset.Zero) }
    BoxWithConstraints(
        modifier = modifier
            .apply { if (chitraLekhan.isPortrait) fillMaxHeight() else fillMaxWidth() }
            .aspectRatio(chitraLekhan.aspectRatio)
            .graphicsLayer { clip = true }
    ) {
        val state = rememberTransformableState { zoomChange, panChange, _ ->
            // Allow panning and zooming only in non-draw mode
            if (chitraLekhan.drawMode.value == DrawMode.None) {
                scale = (scale * zoomChange).coerceIn(1f, 5f)

                val extraWidth = (scale - 1) * constraints.maxWidth
                val extraHeight = (scale - 1) * constraints.maxHeight

                val maxX = extraWidth / 2
                val maxY = extraHeight / 2

                zoomOffset = Offset(
                    x = (zoomOffset.x + scale * panChange.x).coerceIn(-maxX, maxX),
                    y = (zoomOffset.y + scale * panChange.y).coerceIn(-maxY, maxY)
                )
            }
        }
        Image(
            bitmap = chitraLekhan.image,
            modifier = Modifier
                .onGloballyPositioned { chitraLekhan.imageDisplaySize = it.size }
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    translationX = zoomOffset.x
                    translationY = zoomOffset.y
                }
                .matchParentSize()
                .transformable(state),
            contentDescription = null
        )
        if (chitraLekhan.drawMode.value != DrawMode.None) {
            Canvas(
                modifier = Modifier
                    .aspectRatio(chitraLekhan.aspectRatio)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = zoomOffset.x
                        translationY = zoomOffset.y
                    }
                    .matchParentSize()
                    .transformable(state)
                    .pointerInput(null) {
                        detectDragGestures(
                            onDragStart = {
                                chitraLekhan.startDrawing(Point(it.x, it.y))
                            },
                            onDrag = { change, _ ->
                                chitraLekhan.updateDrawing(Point(change.position.x, change.position.y))
                            }
                        )
                    }
            ) {
                drawChitraLekhanStrokes(chitraLekhan.strokes)
            }
        } else {
            Canvas(
                modifier = Modifier
                    .aspectRatio(chitraLekhan.aspectRatio)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        translationX = zoomOffset.x
                        translationY = zoomOffset.y
                    }
                    .matchParentSize()
                    .transformable(state)
            ) {
                drawChitraLekhanStrokes(chitraLekhan.strokes)
            }
        }
    }
}


