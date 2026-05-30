package com.pulasthi.tapit.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.R
import com.pulasthi.tapit.ui.components.DashboardBackground
import com.pulasthi.tapit.ui.dashboard.DashboardPromoBannerImage
import com.pulasthi.tapit.ui.theme.TapItGridBackground
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.DashboardServiceItem

private object TapItDashboardDimens {
    val ScreenHorizontal = 20.dp
    val BottomNavClearance = 100.dp
    val BannerToGrid = 16.dp
    val GridRadius = 22.dp
    val GridInnerH = 16.dp
    val GridInnerV = 20.dp
    val GridRowGap = 24.dp
    val IconTileSize = 62.dp
    val IconTileRadius = 16.dp
    val ServiceIconSize = 28.dp
}

/**
 * Pixel-structured dashboard matching the reference composition.
 * Material placeholders only — swap drawables later without changing layout.
 */
@Composable
fun TapItDashboardScreen(
    onSettingsClick: () -> Unit,
    services: List<DashboardServiceItem>,
    onServiceClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    DashboardBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = TapItDashboardDimens.ScreenHorizontal)
                .padding(bottom = TapItDashboardDimens.BottomNavClearance),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(R.drawable.image_1),
                    contentDescription = "Tap-It",
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Fit,
                )
                IconButton(onClick = onSettingsClick) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = TapItWhite,
                        modifier = Modifier.size(32.dp),
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ) {
                DashboardPromoBannerImage(
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(TapItDashboardDimens.BannerToGrid))

                TapItDashboardServiceGrid(
                    services = services,
                    onServiceClick = onServiceClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                )
            }
        }
    }
}

@Composable
private fun TapItDashboardServiceGrid(
    services: List<DashboardServiceItem>,
    onServiceClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = services.chunked(3)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(TapItDashboardDimens.GridRadius),
        color = TapItGridBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(
                    horizontal = TapItDashboardDimens.GridInnerH,
                    vertical = TapItDashboardDimens.GridInnerV,
                ),
            verticalArrangement = Arrangement.spacedBy(TapItDashboardDimens.GridRowGap),
        ) {
            rows.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    rowItems.forEach { service ->
                        TapItDashboardGridItem(
                            service = service,
                            onClick = { onServiceClick(service.id) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    repeat(3 - rowItems.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun TapItDashboardGridItem(
    service: DashboardServiceItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(TapItDashboardDimens.IconTileSize)
                .background(TapItWhite, RoundedCornerShape(TapItDashboardDimens.IconTileRadius)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = service.icon,
                contentDescription = service.title,
                tint = service.iconTint,
                modifier = Modifier.size(TapItDashboardDimens.ServiceIconSize),
            )
        }
        Text(
            text = service.title,
            color = TapItWhite,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 13.sp,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
