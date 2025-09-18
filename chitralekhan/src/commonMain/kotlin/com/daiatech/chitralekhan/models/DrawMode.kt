/**
 * Copyright (c) 2025 DAIA Tech Pvt Ltd. All rights reserved.
 */

package com.daiatech.chitralekhan.models

/**
 * Represents different drawing modes available for annotation.
 */
sealed interface DrawMode {

    /**
     * Represents freehand drawing mode where strokes are drawn freely.
     */
    data object FreeHand : DrawMode

    /**
     * Represents a mode for drawing circles.
     */
    data object Circle : DrawMode

    /**
     * Represents a mode for drawing polygons with a specific number of sides.
     *
     * @property sides The number of sides of the polygon.
     */
    class Polygon(val sides: Int) : DrawMode

    /**
     * Represents a state where no drawing mode is selected.
     */
    data object None : DrawMode

    /**
     * Represents a mode for drawing rectangles.
     */
    data object Rectangle : DrawMode
}
