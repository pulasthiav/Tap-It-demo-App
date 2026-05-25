package com.pulasthi.tapit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.pulasthi.tapit.navigation.TapItNavGraph
import com.pulasthi.tapit.ui.theme.TapItTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TapItTheme {
                TapItNavGraph(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
