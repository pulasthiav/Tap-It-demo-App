package com.pulasthi.tapit.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pulasthi.tapit.R

private val DashboardBannerHeight = 172.dp
private val DashboardBannerCornerRadius = 22.dp
private val DashboardBannerHorizontalPadding = 4.dp

@Composable
fun DashboardPromoBannerImage(
    modifier: Modifier = Modifier,
    cornerShape: Shape = RoundedCornerShape(DashboardBannerCornerRadius),
) {
    Image(
        painter = painterResource(R.drawable.dashboard_promo_banner),
        contentDescription = "The Secret to Safe Online Money Transfers",
        modifier = modifier
            .padding(horizontal = DashboardBannerHorizontalPadding)
            .fillMaxWidth()
            .height(DashboardBannerHeight)
            .clip(cornerShape),
        contentScale = ContentScale.Crop,
    )
}
