package com.pulasthi.tapit.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pulasthi.tapit.R
import com.pulasthi.tapit.ui.components.DashboardWavyLines
import com.pulasthi.tapit.ui.theme.TapItWhite

@Composable
fun DashboardHeader(
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp),
    ) {
        DashboardWavyLines(
            modifier = Modifier
                .fillMaxWidth()
                .height(88.dp)
                .align(Alignment.TopCenter),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.image_1),
                contentDescription = "Tap-It",
                modifier = Modifier.size(60.dp),
                contentScale = ContentScale.Fit,
            )
            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.size(48.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TapItWhite,
                    modifier = Modifier.size(32.dp),
                )
            }
        }
    }
}
