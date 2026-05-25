package com.pulasthi.tapit.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.R
import com.pulasthi.tapit.ui.components.DashboardBackground
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToGovPay: () -> Unit,
    onNavigateToPayBills: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.serviceSelected.collect { serviceId ->
            when (serviceId) {
                "gov_pay" -> onNavigateToGovPay()
                "pay_bills" -> onNavigateToPayBills()
            }
        }
    }

    DashboardBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 100.dp),
        ) {
            HomeTopBar(onSettingsClick = onNavigateToSettings)

            Spacer(modifier = Modifier.height(16.dp))

            PromoBanner(
                titleLine1 = uiState.bannerTitleLine1,
                titleLine2 = uiState.bannerTitleLine2,
            )

            Spacer(modifier = Modifier.height(16.dp))

            ServiceGridCard(
                services = uiState.services,
                onServiceClick = viewModel::onServiceClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun HomeTopBar(
    onSettingsClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_tap_logo),
            contentDescription = null,
            modifier = Modifier.size(36.dp),
        )
        IconButton(onClick = onSettingsClick) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TapItWhite,
                modifier = Modifier.size(28.dp),
            )
        }
    }
}
