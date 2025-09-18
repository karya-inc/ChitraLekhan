package com.daiatech.chitralekhan.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.daiatech.chitralekhan.ChitraLekhanCanvas
import com.daiatech.chitralekhan.components.ChitraLekhanToolbar
import com.daiatech.chitralekhan.models.DrawMode
import com.daiatech.chitralekhan.rememberChitraLekhan
import com.daiatech.chitralekhan.utils.colors
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.compose.util.toImageBitmap
import io.github.vinceglb.filekit.dialogs.openFilePicker
import kotlinx.coroutines.launch

@Composable
fun App() {
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            imageBitmap?.let { bmp ->
                val chitraLekhan = rememberChitraLekhan(
                    image = bmp,
                    drawMode = DrawMode.FreeHand,
                    color = colors.random(),
                    width = 1f
                )
                var isColorPickerVisible by remember { mutableStateOf(false) }
                ChitraLekhanCanvas(chitraLekhan = chitraLekhan, modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        // Save on disk
                    }
                ) {
                    Text("Save to gallery")
                }
                ChitraLekhanToolbar(
                    colors = colors,
                    pickedColor = chitraLekhan.strokeColor.value,
                    isColorPickerVisible = isColorPickerVisible,
                    onColorPickerClicked = { isColorPickerVisible = !isColorPickerVisible },
                    onColorPicked = {
                        chitraLekhan.setColor(it)
                        isColorPickerVisible = false
                    },
                    drawMode = chitraLekhan.drawMode.value,
                    onDrawModeSelected = chitraLekhan::setDrawMode,
                    onClear = chitraLekhan::clear,
                    onUndo = chitraLekhan::undo,
                    onRedo = chitraLekhan::redo,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp, 16.dp, 0.dp, 0.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer)
                        .padding(8.dp),
                    brushSize = chitraLekhan.strokeWidth.value,
                    onBrushSizeChange = chitraLekhan::setWidth
                )
            } ?: run {
                Row {
                    Button(
                        onClick = {
                            // Pick from disk
                            coroutineScope.launch {
                                val imageFile = FileKit.openFilePicker(type = FileKitType.Image)
                                imageBitmap  = imageFile?.toImageBitmap()
                            }
                        }
                    ) {
                        Text("Pick image")
                    }
                }
            }
        }
    }
}


