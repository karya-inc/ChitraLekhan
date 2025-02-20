/**
 * Copyright (c) 2025 DAIA Tech Pvt Ltd. All rights reserved.
*/

package com.daiatech.chitralekhan

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntSize
import com.daiatech.chitralekhan.models.DrawMode
import com.daiatech.chitralekhan.models.DrawingStroke
import com.daiatech.chitralekhan.utils.calculateDistance
import com.daiatech.chitralekhan.utils.calculateMidPoint
import com.daiatech.chitralekhan.utils.drawQuadraticBezier
import com.daiatech.chitralekhan.utils.getVertices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChitraLekhan(
    strokeColor: Color,
    strokeWidth: Float,
    strokeAlpha: Float,
    drawMode: DrawMode,
    val image: Bitmap
) {
    private val _undoList = mutableStateListOf<DrawingStroke>()
    val strokes: SnapshotStateList<DrawingStroke> get() = _undoList

    private val _drawMode = mutableStateOf(drawMode)
    val drawMode: State<DrawMode> get() = _drawMode

    private val _strokeColor = mutableStateOf(strokeColor)
    val strokeColor: State<Color> = _strokeColor

    private val _strokeWidth = mutableFloatStateOf(strokeWidth)
    val strokeWidth: State<Float> = _strokeWidth

    private val _strokeAlpha = mutableFloatStateOf(strokeAlpha)
    val strokeAlpha: State<Float> = _strokeAlpha

    private val _redoList = mutableListOf<DrawingStroke>()

    var imageDisplaySize: IntSize? = null
    val aspectRatio = image.width.toFloat() / image.height.toFloat()
    val isPortrait = image.width < image.height

    fun startDrawing(offset: Offset) {
        val stroke = when (_drawMode.value) {
            DrawMode.Circle -> DrawingStroke.Circle(
                offset,
                offset,
                strokeColor.value,
                strokeWidth.value,
                strokeAlpha.value
            )

            is DrawMode.Polygon -> {
                val edges = getVertices(0f, offset, (_drawMode.value as DrawMode.Polygon).sides)
                DrawingStroke.Polygon(
                    edges,
                    strokeColor.value,
                    strokeWidth.value,
                    strokeAlpha.value
                )
            }

            is DrawMode.FreeHand -> DrawingStroke.FreeHand(
                mutableStateListOf(offset),
                strokeColor.value,
                strokeWidth.value,
                strokeAlpha.value
            )

            is DrawMode.None -> null
            is DrawMode.Rectangle -> DrawingStroke.Rectangle(
                offset,
                offset,
                strokeColor.value,
                strokeWidth.value,
                strokeAlpha.value
            )
        }
        if (stroke != null) {
            _undoList.add(stroke)
        }
    }

    fun updateDrawing(offset: Offset) {
        if (_undoList.isEmpty()) return
        when (val lastStroke = _undoList.last()) {
            is DrawingStroke.Circle -> {
                val newCircle = DrawingStroke.Circle(
                    lastStroke.poc1,
                    offset,
                    strokeColor.value,
                    strokeWidth.value,
                    strokeAlpha.value
                )
                _undoList.removeAt(_undoList.lastIndex)
                _undoList.add(newCircle)
            }

            is DrawingStroke.FreeHand -> {
                lastStroke.points.add(offset)
            }

            is DrawingStroke.Polygon -> {
                val p1 = lastStroke.points[0]
                val p2 = offset
                val radius = calculateDistance(p1, p2) / 2
                val center = calculateMidPoint(p1, p2)
                val sides = 5
                val edges = getVertices(radius, center, sides)
                val newPolygon = DrawingStroke.Polygon(
                    edges,
                    strokeColor.value,
                    strokeWidth.value,
                    strokeAlpha.value
                )
                _undoList.removeAt(_undoList.lastIndex)
                _undoList.add(newPolygon)
            }

            is DrawingStroke.Rectangle -> {
                val newRectangle =
                    DrawingStroke.Rectangle(
                        lastStroke.d1,
                        lastStroke.d2,
                        strokeColor.value,
                        strokeWidth.value,
                        strokeAlpha.value
                    )
                _undoList.removeAt(_undoList.lastIndex)
                _undoList.add(newRectangle)
            }
        }
    }

    fun undo() {
        if (_undoList.isEmpty()) return
        val removed = _undoList.removeAt(_undoList.lastIndex)
        _redoList.add(removed)
    }

    fun redo() {
        if (_redoList.isEmpty()) return
        val removed = _redoList.removeAt(_redoList.lastIndex)
        _undoList.add(removed)
    }

    fun clear() {
        _undoList.clear()
        _redoList.clear()
    }

    fun setColor(color: Color) {
        _strokeColor.value = color
    }

    fun setWidth(width: Float) {
        _strokeWidth.floatValue = width
    }

    fun setAlpha(alpha: Float) {
        _strokeAlpha.floatValue = alpha
    }

    fun setDrawMode(drawMode: DrawMode) {
        _drawMode.value = drawMode
    }

    suspend fun getDrawingAsBitmap(): Bitmap = withContext(Dispatchers.Default) {
        imageDisplaySize?.let { displaySize ->
            // draw the strokes on a canvas of size [displaySize]
            val originalSizeBmp = createBitmapFromStrokes(displaySize)
            // then scale it down/up to match the bitmap size
            Bitmap.createScaledBitmap(originalSizeBmp, image.width, image.height, true)
        } ?: throw IllegalStateException("Please initialize the imageDisplaySize")
    }

    private suspend fun createBitmapFromStrokes(displaySize: IntSize): Bitmap =
        withContext(Dispatchers.Default) {
            val bitmap = Bitmap.createBitmap(
                displaySize.width, displaySize.height, Bitmap.Config.ARGB_8888
            )
            val blankCanvas = Canvas(bitmap)
            strokes.forEach { stroke ->
                val paint = Paint().apply {
                    setARGB(
                        255,
                        (stroke.color.red * 255).toInt(),
                        (stroke.color.green * 255).toInt(),
                        (stroke.color.blue * 255).toInt()
                    )
                    strokeWidth = stroke.width
                    style = Paint.Style.STROKE
                    strokeJoin = Paint.Join.ROUND
                    strokeCap = Paint.Cap.ROUND
                }
                when (stroke) {
                    is DrawingStroke.Circle -> {
                        val path = Path().apply {
                            // Add circle to path based on the properties of the Circle stroke
                            addCircle(
                                stroke.center.x,
                                stroke.center.y,
                                stroke.radius,
                                Path.Direction.CW
                            )
                        }
                        blankCanvas.drawPath(path, paint)
                    }

                    is DrawingStroke.FreeHand -> {
                        val path = Path().apply { drawQuadraticBezier(stroke.points) }
                        blankCanvas.drawPath(path, paint)
                    }

                    is DrawingStroke.Polygon -> {
                        val path = Path().apply { drawQuadraticBezier(stroke.points) }
                        blankCanvas.drawPath(path, paint)
                    }

                    is DrawingStroke.Rectangle -> {
                        val path = Path().apply {
                            addRect(
                                stroke.topLeft.x,
                                stroke.topLeft.y,
                                stroke.bottomRight.x,
                                stroke.bottomRight.y,
                                Path.Direction.CW
                            )
                        }
                        blankCanvas.drawPath(path, paint)
                    }
                }
            }
            bitmap
        }
}


