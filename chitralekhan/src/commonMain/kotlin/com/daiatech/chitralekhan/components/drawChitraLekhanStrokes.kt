package com.daiatech.chitralekhan.components

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.daiatech.chitralekhan.models.DrawingStroke
import com.daiatech.chitralekhan.utils.drawQuadraticBezier

fun DrawScope.drawChitraLekhanStrokes(strokes: List<DrawingStroke>) {
    strokes.forEach { stroke ->
        when (stroke) {
            is DrawingStroke.FreeHand -> {
                drawPath(
                    Path().apply { drawQuadraticBezier(stroke.points) },
                    color = Color(stroke.color),
                    style = Stroke(
                        width = stroke.width,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            is DrawingStroke.Polygon -> {
                drawPath(
                    Path().apply { drawQuadraticBezier(stroke.points) },
                    color = Color(stroke.color),
                    style = Stroke(
                        width = stroke.width,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            is DrawingStroke.Circle -> {
                drawCircle(
                    color = Color(stroke.color),
                    radius = stroke.radius,
                    center = Offset(stroke.center.x, stroke.center.y),
                    style = Stroke(
                        width = stroke.width,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            is DrawingStroke.Rectangle -> {
                drawRect(
                    color = Color(stroke.color),
                    topLeft = Offset(stroke.topLeft.x, stroke.topLeft.y),
                    size = Size(stroke.edgeWidth, stroke.edgeLength),
                    style = Stroke(
                        width = stroke.width,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}