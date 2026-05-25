package com.pulasthi.tapit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.R
import com.pulasthi.tapit.ui.theme.TapItWhite

@Composable
fun TapItLogo(
    modifier: Modifier = Modifier,
    iconSize: androidx.compose.ui.unit.Dp = 56.dp,
    showText: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_tap_logo),
            contentDescription = null,
            modifier = Modifier.size(iconSize),
        )
        if (showText) {
            Text(
                text = "Tap-It",
                color = TapItWhite,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .align(Alignment.CenterVertically),
            )
        }
    }
}
