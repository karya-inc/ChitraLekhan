package com.daiatech.chitralekhan.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.daiatech.chitralekhan.models.DrawMode
import com.daiatech.chitralekhan.utils.colors
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChitraLekhanToolbar(
    modifier: Modifier = Modifier,
    colors: List<Color>,
    pickedColor: Color,
    isColorPickerVisible: Boolean,
    onColorPickerClicked: () -> Unit,
    onColorPicked: (Color) -> Unit,
    drawMode: DrawMode,
    onDrawModeSelected: (DrawMode) -> Unit,
    onClear: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    brushSize: Float,
    onBrushSizeChange: (Float) -> Unit,
    minBrushSize: Float = 2f,
    maxBrushSize: Float = 32f
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClear) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onUndo) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Undo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onRedo) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Redo",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onColorPickerClicked) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(pickedColor, CircleShape)
                )
            }
            DrawModeSelector(
                modifier = Modifier.weight(1f),
                selected = drawMode,
                onSelect = onDrawModeSelected,
                polygonSides = 5
            )
        }

        AnimatedVisibility(isColorPickerVisible) {
            Column {
                Slider(
                    value = brushSize.coerceIn(minBrushSize, maxBrushSize),
                    onValueChange = onBrushSizeChange,
                    valueRange = minBrushSize..maxBrushSize,
                    thumb = {
                        Box(
                            Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(SliderDefaults.colors().thumbColor)
                        )
                    },
                )
                ColorPicker(
                    colors = colors,
                    pickedColor = pickedColor,
                    onColorPicked = onColorPicked,
                )
            }

        }
    }
}

@Preview
@Composable
private fun ChitraLekhanToolbarPreview() {
    MaterialTheme(colorScheme = darkColorScheme()) {
        ChitraLekhanToolbar(
            colors = colors,
            pickedColor = colors[0],
            isColorPickerVisible = true,
            onColorPickerClicked = { },
            onColorPicked = {},
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .padding(8.dp),
            drawMode = DrawMode.FreeHand,
            onDrawModeSelected = {},
            onClear = {},
            onUndo = {},
            onRedo = {},
            brushSize = 12f,
            onBrushSizeChange = {}
        )
    }

}