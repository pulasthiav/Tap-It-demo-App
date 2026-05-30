package com.pulasthi.tapit.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.R
import com.pulasthi.tapit.ui.theme.TapItWhite

@Composable
fun AuthTopSection(
    title: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .padding(horizontal = 24.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.image_1),
            contentDescription = "Tap-It",
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(48.dp)
                .padding(start = 8.dp),
            contentScale = ContentScale.Fit,
        )
        Text(
            text = title,
            color = TapItWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp),
        )
        PaymentIllustration(
            modifier = Modifier.align(Alignment.Center),
        )
    }
}
