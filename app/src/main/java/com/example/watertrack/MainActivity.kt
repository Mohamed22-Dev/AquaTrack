package com.example.watertrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.example.watertrack.data.AppPreferences
import com.example.watertrack.navigation.AppNavGraph
import com.example.watertrack.ui.theme.WaterTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val prefs = remember { AppPreferences(applicationContext) }
            val dark by prefs.darkMode.collectAsState(initial = false)
            WaterTrackTheme(darkTheme = dark) { AppNavGraph() }
        }
    }
}