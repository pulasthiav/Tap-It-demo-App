package com.pulasthi.tapit.ui.reload.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pulasthi.tapit.ui.theme.TapItReloadPanelBlue
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.viewmodel.ReloadOperator

@Composable
fun OperatorCarousel(
    operators: List<ReloadOperator>,
    selectedIndex: Int,
    onOperatorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val pageSize = 2
    val pageCount = ((operators.size + pageSize - 1) / pageSize).coerceAtLeast(1)
    val pagerState = rememberPagerState(
        initialPage = (selectedIndex / pageSize).coerceIn(0, pageCount - 1),
        pageCount = { pageCount },
    )

    LaunchedEffect(pagerState.currentPage) {
        val firstIndexOnPage = pagerState.currentPage * pageSize
        if (firstIndexOnPage in operators.indices && selectedIndex / pageSize != pagerState.currentPage) {
            onOperatorSelected(firstIndexOnPage)
        }
    }

    LaunchedEffect(selectedIndex) {
        val targetPage = selectedIndex / pageSize
        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = TapItReloadPanelBlue,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth(),
            ) { page ->
                val start = page * pageSize
                val pageOperators = operators.drop(start).take(pageSize)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    pageOperators.forEachIndexed { indexOnPage, operator ->
                        val globalIndex = start + indexOnPage
                        val selected = globalIndex == selectedIndex
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .clickable { onOperatorSelected(globalIndex) }
                                .then(
                                    if (selected) {
                                        Modifier.background(Color.White.copy(alpha = 0.35f), CircleShape)
                                    } else {
                                        Modifier
                                    },
                                )
                                .padding(6.dp),
                        ) {
                            ReloadOperatorLogo(operator = operator)
                        }
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(pageCount) { index ->
                    val active = index == pagerState.currentPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(if (active) 8.dp else 6.dp)
                            .clip(CircleShape)
                            .background(
                                if (active) TapItTextPrimary else Color.White.copy(alpha = 0.6f),
                            ),
                    )
                }
            }
        }
    }
}
