package com.daiatech.chitralekhan

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import com.daiatech.chitralekhan.models.DrawMode

@Composable
fun rememberChitraLekhan(
    color: Color,
    width: Float,
    drawMode: DrawMode = DrawMode.FreeHand,
    image: ImageBitmap
): ChitraLekhan {
    return remember {
        ChitraLekhan(
            strokeColor = color,
            strokeWidth = width,
            drawMode = drawMode,
            image = image
        )
    }
}
