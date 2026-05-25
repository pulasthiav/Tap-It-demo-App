package com.pulasthi.tapit.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.ui.theme.TapItBannerGold
import com.pulasthi.tapit.ui.theme.TapItBannerPurple
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItWhite

@Composable
fun PromoBanner(
    titleLine1: String,
    titleLine2: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = TapItBannerPurple,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = titleLine1,
                    color = TapItBannerGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                )
                Text(
                    text = titleLine2,
                    color = TapItBannerGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 22.sp,
                )
            }
            Box(
                modifier = Modifier.size(88.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    TapItBluePrimary.copy(alpha = 0.5f),
                                    TapItBluePrimary.copy(alpha = 0.1f),
                                ),
                            ),
                        ),
                )
                Icon(
                    imageVector = Icons.Default.Smartphone,
                    contentDescription = null,
                    tint = TapItWhite,
                    modifier = Modifier.size(36.dp),
                )
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = null,
                    tint = TapItWhite.copy(alpha = 0.9f),
                    modifier = Modifier
                        .size(20.dp)
                        .align(Alignment.TopEnd),
                )
            }
        }
    }
}
