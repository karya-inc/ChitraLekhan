package com.daiatech.chitralekhan.utils

import android.graphics.Bitmap
import android.graphics.Bitmap.createBitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import com.daiatech.chitralekhan.models.DrawingStroke
import com.daiatech.chitralekhan.models.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


fun android.graphics.Path.drawQuadraticBezier(points: List<Point>) {
    if (points.size <= 1) return
    moveTo(points[0].x, points[0].y)
    var prevPoint = points[1]
    points.forEachIndexed { idx, point ->
        if (idx == 0) return@forEachIndexed
        val controlPoint = Offset((prevPoint.x + point.x) / 2, (prevPoint.y + point.y) / 2)
        quadTo(controlPoint.x, controlPoint.y, point.x, point.y)
        prevPoint = point
    }
}

/**
 * Creates a [Bitmap] by drawing a list of [DrawingStroke]s onto a blank canvas.
 *
 * This function initializes an empty [Bitmap] of the specified width and height, then
 * iterates over the given strokes, drawing each one using appropriate Paint settings.
 * Supported strokes include:
 * - [DrawingStroke.Circle]: Draws a circle at the specified center with given radius.
 * - [DrawingStroke.FreeHand]: Draws a smooth freehand path using quadratic Bezier curves.
 * - [DrawingStroke.Polygon]: Draws a polygon shape with smooth curves between points.
 * - [DrawingStroke.Rectangle]: Draws a rectangle from the top-left to the bottom-right points.
 *
 * @param strokes The list of [DrawingStroke]s to draw.
 * @param canvasWidth The width of the output [Bitmap].
 * @param canvasHeight The height of the output [Bitmap].
 *
 * @return A [Bitmap] containing the rendered strokes.
 */
suspend fun createBitmapFromStrokes(
    strokes: List<DrawingStroke>,
    canvasWidth: Int,
    canvasHeight: Int
): Bitmap =
    withContext(Dispatchers.Default) {
        val bitmap = createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888)
        val blankCanvas = Canvas(bitmap)
        strokes.forEach { stroke ->
            val paint = Paint().apply {
                setColor(stroke.color)
                strokeWidth = stroke.width
                style = Paint.Style.STROKE
                strokeJoin = Paint.Join.ROUND
                strokeCap = Paint.Cap.ROUND
            }
            when (stroke) {
                is DrawingStroke.Circle -> {
                    val path = android.graphics.Path().apply {
                        addCircle(
                            stroke.center.x,
                            stroke.center.y,
                            stroke.radius,
                            android.graphics.Path.Direction.CW
                        )
                    }
                    blankCanvas.drawPath(path, paint)
                }

                is DrawingStroke.FreeHand -> {
                    val path = android.graphics.Path().apply { drawQuadraticBezier(stroke.points) }
                    blankCanvas.drawPath(path, paint)
                }

                is DrawingStroke.Polygon -> {
                    val path = android.graphics.Path().apply { drawQuadraticBezier(stroke.points) }
                    blankCanvas.drawPath(path, paint)
                }

                is DrawingStroke.Rectangle -> {
                    val path = android.graphics.Path().apply {
                        addRect(
                            stroke.topLeft.x,
                            stroke.topLeft.y,
                            stroke.bottomRight.x,
                            stroke.bottomRight.y,
                            android.graphics.Path.Direction.CW
                        )
                    }
                    blankCanvas.drawPath(path, paint)
                }
            }
        }
        bitmap
    }
