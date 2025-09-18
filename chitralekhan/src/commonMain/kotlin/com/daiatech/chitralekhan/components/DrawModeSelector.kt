package com.daiatech.chitralekhan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daiatech.chitralekhan.Res
import com.daiatech.chitralekhan.ic_circle
import com.daiatech.chitralekhan.ic_pencil
import com.daiatech.chitralekhan.ic_polyline
import com.daiatech.chitralekhan.ic_rectangle
import com.daiatech.chitralekhan.models.DrawMode
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun DrawModeSelector(
    modifier: Modifier = Modifier,
    selected: DrawMode,
    polygonSides: Int,
    onSelect: (DrawMode) -> Unit
) {
    Row(
        modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary, CircleShape)
            .horizontalScroll(rememberScrollState())
    ) {
        DrawModeButton(
            drawMode = DrawMode.FreeHand,
            isSelected = selected == DrawMode.FreeHand,
            onSelect = onSelect
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_pencil),
                contentDescription = "free hand drawing",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        DrawModeButton(
            drawMode = DrawMode.Polygon(polygonSides),
            isSelected = selected is DrawMode.Polygon,
            onSelect = onSelect
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_polyline),
                contentDescription = "polygon drawing",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        DrawModeButton(
            drawMode = DrawMode.Circle,
            isSelected = selected == DrawMode.Circle,
            onSelect = onSelect
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_circle),
                contentDescription = "circle drawing",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
        DrawModeButton(
            drawMode = DrawMode.Rectangle,
            isSelected = selected == DrawMode.Rectangle,
            onSelect = onSelect
        ) {
            Icon(
                painter = painterResource(resource = Res.drawable.ic_rectangle),
                contentDescription = "rectangle drawing",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun DrawModeButton(
    drawMode: DrawMode,
    isSelected: Boolean,
    onSelect: (DrawMode) -> Unit,
    content: @Composable () -> Unit
) {
    IconButton(
        modifier = Modifier
            .padding(4.dp)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.inversePrimary else Color.Transparent,
                shape = CircleShape
            ),
        onClick = { onSelect(drawMode) }
    ) { content() }
}

@Preview
@Composable
private fun DrawModeSelectorPrev() {
    DrawModeSelector(
        selected = DrawMode.Circle,
        polygonSides = 5,
        onSelect = {},
    )
}