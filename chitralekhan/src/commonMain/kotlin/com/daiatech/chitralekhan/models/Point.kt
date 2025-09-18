package com.daiatech.chitralekhan.models

import kotlinx.serialization.Serializable

/**
 * A data class representing a point on the screen's XY-coordinates
 */
@Serializable
data class Point(
    val x: Float,
    val y: Float
)