package com.pulasthi.tapit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.pulasthi.tapit.ui.theme.TapItDashboardBlueDeep
import com.pulasthi.tapit.ui.theme.TapItDashboardBlueMid
import com.pulasthi.tapit.ui.theme.TapItDashboardPurple
import com.pulasthi.tapit.ui.theme.TapItWhite

@Composable
fun DashboardBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        TapItDashboardBlueDeep,
                        TapItDashboardBlueMid,
                        TapItDashboardPurple,
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .size(280.dp)
                .offset(x = (-80).dp, y = 120.dp)
                .clip(CircleShape)
                .background(TapItWhite.copy(alpha = 0.06f)),
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = 220.dp, y = 60.dp)
                .clip(CircleShape)
                .background(TapItWhite.copy(alpha = 0.05f)),
        )
        content()
    }
}
