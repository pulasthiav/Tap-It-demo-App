package com.pulasthi.tapit.ui.govpay.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.GovPaymentCard

@Composable
fun PaymentCardCarousel(
    cards: List<GovPaymentCard>,
    selectedIndex: Int,
    onCardSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    fromLabel: String = "From",
) {
    val pagerState = rememberPagerState(
        initialPage = selectedIndex.coerceIn(0, (cards.size - 1).coerceAtLeast(0)),
        pageCount = { cards.size.coerceAtLeast(1) },
    )

    LaunchedEffect(pagerState.currentPage) {
        onCardSelected(pagerState.currentPage)
    }

    LaunchedEffect(selectedIndex) {
        if (pagerState.currentPage != selectedIndex && selectedIndex in cards.indices) {
            pagerState.animateScrollToPage(selectedIndex)
        }
    }

    Column(modifier = modifier) {
        if (fromLabel.isNotEmpty()) {
            Text(
                text = fromLabel,
                color = TapItWhite,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
            )
        }
        if (cards.isNotEmpty()) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
            ) { page ->
                PaymentCardView(
                    card = cards[page],
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                cards.forEachIndexed { index, _ ->
                    val selected = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (selected) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (selected) TapItTextPrimary else Color.White.copy(alpha = 0.5f),
                            ),
                    )
                }
            }
        }
    }
}
