/**
 * Copyright (c) 2025 DAIA Tech Pvt Ltd. All rights reserved.
 */

package com.daiatech.chitralekhan.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.daiatech.chitralekhan.app.theme.ChitraLekhanTheme
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.init

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        FileKit.init(this)
        setContent {
            ChitraLekhanTheme {
                App()
            }
        }
    }
}
