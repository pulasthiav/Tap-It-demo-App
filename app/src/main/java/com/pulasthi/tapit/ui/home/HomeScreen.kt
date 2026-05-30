package com.pulasthi.tapit.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToGovPay: () -> Unit,
    onNavigateToPayBills: () -> Unit,
    onNavigateToReload: () -> Unit,
    onNavigateToSendMoney: () -> Unit,
    onNavigateToQrPay: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    viewModel: HomeViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.serviceSelected.collect { serviceId ->
            when (serviceId) {
                "gov_pay" -> onNavigateToGovPay()
                "pay_bills" -> onNavigateToPayBills()
                "reload" -> onNavigateToReload()
                "send_money" -> onNavigateToSendMoney()
                "qr_pay" -> onNavigateToQrPay()
                "schedule_payment" -> onNavigateToSchedule()
            }
        }
    }

    TapItDashboardScreen(
        onSettingsClick = onNavigateToSettings,
        services = uiState.services,
        onServiceClick = viewModel::onServiceClick,
    )
}
