package com.pulasthi.tapit.ui.dashboard

import androidx.compose.runtime.Composable
import com.pulasthi.tapit.navigation.MainScreen

/**
 * Entry point for the post-auth dashboard shell (Home grid + bottom navigation).
 * [MainScreen] hosts the nested tab NavHost and wires the settings gear icon.
 */
@Composable
fun DashboardScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToGovPay: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToPayBills: () -> Unit,
    onNavigateToReload: () -> Unit,
    onNavigateToSendMoney: () -> Unit,
    onNavigateToQrPay: () -> Unit,
    onNavigateToSchedule: () -> Unit,
) {
    MainScreen(
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToGovPay = onNavigateToGovPay,
        onNavigateToWallet = onNavigateToWallet,
        onNavigateToPayBills = onNavigateToPayBills,
        onNavigateToReload = onNavigateToReload,
        onNavigateToSendMoney = onNavigateToSendMoney,
        onNavigateToQrPay = onNavigateToQrPay,
        onNavigateToSchedule = onNavigateToSchedule,
    )
}
