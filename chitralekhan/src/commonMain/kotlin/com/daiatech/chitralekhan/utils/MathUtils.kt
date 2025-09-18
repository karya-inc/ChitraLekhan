package com.daiatech.chitralekhan.utils

import com.daiatech.chitralekhan.models.DrawingStroke
import com.daiatech.chitralekhan.models.Point
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Calculates the distance between two points using the Euclidean distance formula.
 *
 * @param offset1 The first point.
 * @param offset2 The second point.
 * @return The distance between the two points.
 */
fun calculateDistance(offset1: Point, offset2: Point): Float = sqrt(
    (offset1.y - offset2.y).toDouble().pow(2.0) +
            (offset2.x - offset1.x).toDouble().pow(2.0)
).toFloat()

/**
 * Calculates the midpoint between two points.
 *
 * @param offset1 The first point.
 * @param offset2 The second point.
 * @return The midpoint between the two given points.
 */
fun calculateMidPoint(offset1: Point, offset2: Point): Point = Point(
    x = (offset1.x + offset2.x) / 2,
    y = (offset1.y + offset2.y) / 2
)

/**
 * Determines the top-left corner of a rectangle given two diagonal endpoints.
 *
 * @param d1 One endpoint of the diagonal.
 * @param d2 The other endpoint of the diagonal.
 * @return The top-left corner of the rectangle.
 */
fun getTopLeft(d1: Point, d2: Point): Point {
    val x1 = d1.x
    val x2 = d2.x
    val y1 = d1.y
    val y2 = d2.y
    return if (y2 > y1) {
        if (x2 > x1) {
            Point(x1, y1)
        } else {
            Point(x2, y1)
        }
    } else {
        if (x2 > x1) {
            Point(x1, y2)
        } else {
            Point(x2, y2)
        }
    }
}

/**
 * Determines the top-left and bottom-right coordinates of a rectangle given two diagonal endpoints.
 *
 * @param d1 One endpoint of the diagonal.
 * @param d2 The other endpoint of the diagonal.
 * @return A pair where the first element is the top-left corner and the second is the bottom-right corner.
 */
fun getTopLeftAndBottomRight(d1: Point, d2: Point): Pair<Point, Point> {
    val x1 = d1.x
    val x2 = d2.x
    val y1 = d1.y
    val y2 = d2.y
    return if (y2 > y1) {
        if (x2 > x1) {
            Pair(Point(x1, y1), Point(x2, y2))
        } else {
            Pair(Point(x2, y1), Point(x1, y2))
        }
    } else {
        if (x2 > x1) {
            Pair(Point(x1, y2), Point(x2, y1))
        } else {
            Pair(Point(x2, y2), Point(x1, y1))
        }
    }
}

/**
 * Generates the vertices of a regular polygon given the radius, center, and number of sides.
 *
 * @param radius The radius of the polygon.
 * @param center The center point of the polygon.
 * @param sides The number of sides of the polygon.
 * @return A list of [Point] points representing the vertices of the polygon.
 */
fun getVertices(radius: Float, center: Point, sides: Int): MutableList<Point> {
    val vertices = mutableListOf<Point>()
    val x = center.x
    val y = center.y
    for (i in 0 until sides) {
        val x1 = (x + radius * cos(2 * PI * i / sides)).toFloat()
        val y1 = (y + radius * sin(2 * PI * i / sides)).toFloat()
        vertices.add(Point(x1, y1))
    }
    return vertices
}

/**
 * Scales a list of [DrawingStroke]s from a given display size to an image size.
 *
 * This function adjusts all points inside each stroke based on the ratio between
 * the original display dimensions and the actual image dimensions. It ensures that
 * the strokes retain their relative positions and proportions when mapped onto
 * the larger or smaller image.
 *
 * Supported stroke types:
 * - [DrawingStroke.Circle]
 * - [DrawingStroke.FreeHand]
 * - [DrawingStroke.Polygon]
 * - [DrawingStroke.Rectangle]
 *
 * @param strokes The list of [DrawingStroke]s drawn on the display size.
 * @param imageWidth The width of the target image to which strokes should be scaled.
 * @param imageHeight The height of the target image to which strokes should be scaled.
 * @param displayWidth The width of the display on which the strokes were originally drawn.
 * @param displayHeight The height of the display on which the strokes were originally drawn.
 * @param dispatcher The [CoroutineDispatcher] on which the scaling operation should be performed.
 *                   Defaults to [Dispatchers.Default].
 *
 * @return A new list of [DrawingStroke]s with all points scaled to match the target image size.
 *
 */
suspend fun scaleStrokesToImageSize(
    strokes: List<DrawingStroke>,
    imageWidth: Int,
    imageHeight: Int,
    displayWidth: Int,
    displayHeight: Int,
    dispatcher: CoroutineDispatcher = Dispatchers.Default
): List<DrawingStroke> = withContext(dispatcher) {
    val scaleX = (imageWidth.toFloat() / displayWidth)
    val scaleY = (imageHeight.toFloat() / displayHeight)
    return@withContext strokes.map { stroke ->
        fun Point.getScaledPoint(): Point {
            return Point(
                x = this.x.times(scaleX),
                y = this.y.times(scaleY)
            )
        }
        when (stroke) {
            is DrawingStroke.Circle -> {
                DrawingStroke.Circle(
                    poc1 = stroke.poc1.getScaledPoint(),
                    poc2 = stroke.poc2.getScaledPoint(),
                    color = stroke.color,
                    width = stroke.width
                )
            }

            is DrawingStroke.FreeHand -> {
                val points = stroke.points.map { it.getScaledPoint() }
                DrawingStroke.FreeHand(points, stroke.color, stroke.width)
            }

            is DrawingStroke.Polygon -> {
                val points = stroke.points.map { it.getScaledPoint() }
                    .toMutableList()
                DrawingStroke.Polygon(points, stroke.color, stroke.width)
            }

            is DrawingStroke.Rectangle -> {
                DrawingStroke.Rectangle(
                    d1 = stroke.d1.getScaledPoint(),
                    d2 = stroke.d2.getScaledPoint(),
                    color = stroke.color,
                    width = stroke.width
                )
            }
        }
    }
}