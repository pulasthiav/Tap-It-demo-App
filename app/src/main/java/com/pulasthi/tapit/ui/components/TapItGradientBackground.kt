package com.pulasthi.tapit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.pulasthi.tapit.ui.theme.TapItBlueDark
import com.pulasthi.tapit.ui.theme.TapItBlueLight

@Composable
fun TapItGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(TapItBlueDark, TapItBlueLight),
                ),
            ),
    ) {
        content()
    }
}
