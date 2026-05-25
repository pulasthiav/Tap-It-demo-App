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
) {
    MainScreen(
        onNavigateToSettings = onNavigateToSettings,
        onNavigateToGovPay = onNavigateToGovPay,
        onNavigateToWallet = onNavigateToWallet,
        onNavigateToPayBills = onNavigateToPayBills,
    )
}
