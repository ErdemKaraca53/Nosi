package com.erdem.nosi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.erdem.nosi.screen.MainScaffold
import com.erdem.nosi.ui.theme.NosiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NosiTheme {
                MainScaffold()
            }
        }
    }
}