package com.pulasthi.tapit.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.ui.settings.components.SettingsScaffold
import com.pulasthi.tapit.ui.theme.TapItTextSecondary

@Composable
fun SettingsSubScreen(
    title: String,
    message: String,
    onBack: () -> Unit,
) {
    SettingsScaffold(title = title, onBack = onBack) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = message,
                color = TapItTextSecondary,
                fontSize = 15.sp,
            )
        }
    }
}
