/**
 * Copyright (c) 2024 DAIA Tech Pvt Ltd. All rights reserved.
 */

package com.daiatech.chitralekhan.utils

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import com.daiatech.chitralekhan.models.Point

fun Path.drawQuadraticBezier(points: List<Point>) {
    if (points.size <= 1) return // need atLeast two points to draw path
    moveTo(points[0].x, points[0].y) // move the cursor from (0,0) to x0, y0
    var prevPoint = points[1]
    points.forEachIndexed { idx, point ->
        if (idx == 0) return@forEachIndexed
        // set middle as control point
        val controlPoint = Offset((prevPoint.x + point.x) / 2, (prevPoint.y + point.y) / 2)
        // draw a bezier curve from `prevPoint` to `point` through `controlPoint`
        quadraticTo(controlPoint.x, controlPoint.y, point.x, point.y)
        prevPoint = point
    }
}

