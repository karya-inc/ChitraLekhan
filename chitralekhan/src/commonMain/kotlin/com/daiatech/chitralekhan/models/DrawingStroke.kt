package com.daiatech.chitralekhan.models

import com.daiatech.chitralekhan.utils.calculateDistance
import com.daiatech.chitralekhan.utils.calculateMidPoint
import com.daiatech.chitralekhan.utils.getTopLeftAndBottomRight
import kotlinx.serialization.Serializable

/**
 * Sealed class representing different types of drawing strokes.
 *
 * @property color The color of the stroke.
 * @property width The width of the stroke.
 */
@Serializable
sealed interface DrawingStroke{
    val color: Int
    val width: Float

    /**
     * Represents a freehand stroke composed of a list of points.
     *
     * @property points The list of points defining the stroke path.
     * @param color The color of the stroke.
     * @param width The width of the stroke.
     */
    @Serializable
    data class FreeHand(
        val points: List<Point>,
        override val color: Int,
        override val width: Float
    ) : DrawingStroke

    /**
     * Represents a polygonal stroke, defined by a mutable list of points.
     * The first point is added again at the end to close the shape.
     *
     * @property points The list of points defining the polygon.
     * @param color The color of the stroke (default: Green).
     * @param width The width of the stroke.
     */
    @Serializable
    data class Polygon(
        val points: MutableList<Point>,
        override val color: Int,
        override val  width: Float
    ) : DrawingStroke {
        init {
            // Add the first point again at the end to close the shape
            points.add(points[0])
        }
    }

    /**
     * Represents a rectangular stroke, defined by two diagonal points.
     *
     * @property d1 One of the diagonal points.
     * @property d2 The second diagonal point.
     * @property topLeft The top-left corner of the rectangle.
     * @property bottomRight The bottom-right corner of the rectangle.
     * @property edgeWidth The width of the rectangle.
     * @property edgeLength The height of the rectangle.
     * @param color The color of the stroke (default: Green).
     * @param width The width of the stroke.
     */
    @Serializable
    data class Rectangle(
        val d1: Point,
        val d2: Point,
        override val color: Int,
        override val width: Float
    ) : DrawingStroke {
        private val topLeftAndBottomRight = getTopLeftAndBottomRight(d1, d2)
        val topLeft get() = topLeftAndBottomRight.first
        val bottomRight get() = topLeftAndBottomRight.second
        val edgeWidth get() = bottomRight.x - topLeft.x
        val edgeLength get() = bottomRight.y - topLeft.y
    }

    /**
     * Represents a circular stroke, defined by two points on the diameter.
     *
     * @property poc1 One of the points on the diameter.
     * @property poc2 The second point on the diameter.
     * @property radius The radius of the circle.
     * @property center The center of the circle.
     * @param color The color of the stroke.
     * @param width The width of the stroke.
     */
    @Serializable
    data class Circle(
        val poc1: Point,
        val poc2: Point,
        override val color: Int,
        override val width: Float
    ) : DrawingStroke {
        val radius get() = calculateDistance(poc1, poc2) / 2
        val center get() = calculateMidPoint(poc1, poc2)
    }
}
