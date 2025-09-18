/**
 * Copyright (c) 2025 DAIA Tech Pvt Ltd. All rights reserved.
*/

package com.daiatech.chitralekhan

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.IntSize
import com.daiatech.chitralekhan.models.DrawMode
import com.daiatech.chitralekhan.models.DrawingStroke
import com.daiatech.chitralekhan.models.Point
import com.daiatech.chitralekhan.utils.calculateDistance
import com.daiatech.chitralekhan.utils.calculateMidPoint
import com.daiatech.chitralekhan.utils.getVertices

class ChitraLekhan(
    strokeColor: Color,
    strokeWidth: Float,
    drawMode: DrawMode,
    val image: ImageBitmap
) {
    private val _undoList = mutableStateListOf<DrawingStroke>()
    val strokes: SnapshotStateList<DrawingStroke> get() = _undoList

    private val _drawMode = mutableStateOf(drawMode)
    val drawMode: State<DrawMode> get() = _drawMode

    private val _strokeColor = mutableStateOf(strokeColor)
    val strokeColor: State<Color> = _strokeColor

    private val _strokeWidth = mutableFloatStateOf(strokeWidth)
    val strokeWidth: State<Float> = _strokeWidth

    private val _redoList = mutableListOf<DrawingStroke>()

    var imageDisplaySize: IntSize? = null
    val aspectRatio = image.width.toFloat() / image.height.toFloat()
    val isPortrait = image.width < image.height

    fun startDrawing(offset: Point) {
        val stroke = when (_drawMode.value) {
            DrawMode.Circle -> DrawingStroke.Circle(
                offset,
                offset,
                strokeColor.value.toArgb(),
                strokeWidth.value,
            )

            is DrawMode.Polygon -> {
                val edges = getVertices(0f, offset, (_drawMode.value as DrawMode.Polygon).sides)
                DrawingStroke.Polygon(
                    edges,
                    strokeColor.value.toArgb(),
                    strokeWidth.value,
                )
            }

            is DrawMode.FreeHand -> DrawingStroke.FreeHand(
                mutableStateListOf(offset),
                strokeColor.value.toArgb(),
                strokeWidth.value,
            )

            is DrawMode.None -> null
            is DrawMode.Rectangle -> DrawingStroke.Rectangle(
                offset,
                offset,
                strokeColor.value.toArgb(),
                strokeWidth.value,
            )
        }
        if (stroke != null) {
            _undoList.add(stroke)
        }
    }

    fun updateDrawing(offset: Point) {
        if (_undoList.isEmpty()) return
        when (val lastStroke = _undoList.last()) {
            is DrawingStroke.Circle -> {
                val newCircle = DrawingStroke.Circle(
                    lastStroke.poc1,
                    offset,
                    strokeColor.value.toArgb(),
                    strokeWidth.value,
                )
                _undoList.removeAt(_undoList.lastIndex)
                _undoList.add(newCircle)
            }

            is DrawingStroke.FreeHand -> {
                val newPoints = lastStroke.points + offset
                val newStroke = DrawingStroke.FreeHand(
                    points = newPoints,
                    width = lastStroke.width,
                    color = lastStroke.color
                )
                _undoList.removeAt(_undoList.lastIndex)
                _undoList.add(newStroke)
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
                    strokeColor.value.toArgb(),
                    strokeWidth.value,
                )
                _undoList.removeAt(_undoList.lastIndex)
                _undoList.add(newPolygon)
            }

            is DrawingStroke.Rectangle -> {
                val newRectangle =
                    DrawingStroke.Rectangle(
                        lastStroke.d1,
                        lastStroke.d2,
                        strokeColor.value.toArgb(),
                        strokeWidth.value,
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

    fun setDrawMode(drawMode: DrawMode) {
        _drawMode.value = drawMode
    }
}


