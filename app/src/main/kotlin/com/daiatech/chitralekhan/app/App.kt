package com.daiatech.chitralekhan.app

import android.graphics.Bitmap
import android.graphics.Bitmap.Config
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.daiatech.chitralekhan.ChitraLekhanCanvas
import com.daiatech.chitralekhan.components.ChitraLekhanToolbar
import com.daiatech.chitralekhan.models.DrawMode
import com.daiatech.chitralekhan.rememberChitraLekhan
import com.daiatech.chitralekhan.utils.colors
import com.daiatech.chitralekhan.utils.createBitmapFromStrokes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileDescriptor
import java.io.FileOutputStream

@Composable
fun App() {
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val context = LocalContext.current
    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            context.contentResolver.openInputStream(uri)?.use { `is` ->
                imageBitmap = BitmapFactory.decodeStream(`is`)
            }
        }
    }

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
                    image = bmp.asImageBitmap(),
                    drawMode = DrawMode.FreeHand,
                    color = colors.random(),
                    width = 1f
                )
                var isColorPickerVisible by remember { mutableStateOf(false) }
                ChitraLekhanCanvas(chitraLekhan = chitraLekhan, modifier = Modifier.weight(1f))
                Button(
                    onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            val bitmap = createBitmapFromStrokes(
                                chitraLekhan.strokes,
                                chitraLekhan.imageDisplaySize!!.width,
                                chitraLekhan.imageDisplaySize!!.height
                            )
                            val finalBmp = overlayBitmaps(bmp, bitmap)
                            val outputFile = File(context.filesDir, "image.png")
                            FileOutputStream(outputFile).use { os ->
                                finalBmp.compress(Bitmap.CompressFormat.PNG, 100, os)
                            }
                        }
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
                    Button(onClick = { mediaPicker.launch(PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly)) }) {
                        Text("Pick image")
                    }
                }
            }
        }
    }
}

fun overlayBitmaps(baseBitmap: Bitmap, overlayBitmap: Bitmap): Bitmap {
    val resultBitmap = Bitmap.createBitmap(baseBitmap.width, baseBitmap.height, Config.ARGB_8888)
    val canvas = Canvas(resultBitmap)
    canvas.drawBitmap(baseBitmap, 0f, 0f, null)
    val scaledOverlayBitmap =
        Bitmap.createScaledBitmap(overlayBitmap, baseBitmap.width, baseBitmap.height, true)
    canvas.drawBitmap(scaledOverlayBitmap, 0f, 0f, null)
    return resultBitmap
}
