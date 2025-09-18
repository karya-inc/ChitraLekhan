package com.daiatech.chitralekhan.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daiatech.chitralekhan.utils.colors
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPicker(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    pickedColor: Color,
    onColorPicked: (Color) -> Unit
) {
    FlowRow(modifier) {
        colors.forEach { color ->
            ColorPillItem(
                color = color,
                isSelected = color == pickedColor,
                onClick = { onColorPicked(color) }
            )
        }
    }
}


@Composable
fun ColorPillItem(
    modifier: Modifier = Modifier,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = remember(isSelected) { if (isSelected) Color.Black else color }
    IconButton(onClick = onClick) {
        Box(
            modifier = modifier
                .size(32.dp)
                .background(color, CircleShape)
                .border(2.dp, borderColor, CircleShape)

        )
    }
}

@Preview
@Composable
fun ColorPickerPrev() {
    var pickedColor by remember { mutableStateOf(colors[0]) }
    ColorPicker(
        colors = colors,
        pickedColor = pickedColor,
        onColorPicked = { pickedColor = it }
    )
}