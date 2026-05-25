package com.pulasthi.tapit.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.ui.theme.TapItBlueLight
import com.pulasthi.tapit.ui.theme.TapItCardYellow
import com.pulasthi.tapit.ui.theme.TapItGreen
import com.pulasthi.tapit.ui.theme.TapItWhite

@Composable
fun PaymentIllustration(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(width = 220.dp, height = 200.dp)) {
        Text(
            text = "Secure",
            color = TapItWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = (-8).dp, y = 20.dp),
        )
        Text(
            text = "Swift",
            color = TapItWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-12).dp, y = 24.dp),
        )
        Text(
            text = "Simple",
            color = TapItWhite,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-4).dp, y = (-28).dp),
        )

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 100.dp, height = 160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(TapItWhite.copy(alpha = 0.2f))
                    .border(2.dp, TapItWhite.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 72.dp, height = 48.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TapItBlueLight.copy(alpha = 0.85f))
                        .padding(8.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(TapItCardYellow)
                            .align(Alignment.TopStart),
                    )
                }
            }
            Box(
                modifier = Modifier
                    .offset(y = (-12).dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(TapItGreen),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = TapItWhite,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}
