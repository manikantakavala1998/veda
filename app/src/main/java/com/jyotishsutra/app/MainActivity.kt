package com.jyotishsutra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.jyotishsutra.app.ui.JyotishSutraApp
import com.jyotishsutra.app.ui.theme.JyotishSutraTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JyotishSutraTheme {
                JyotishSutraApp()
            }
        }
    }
}
