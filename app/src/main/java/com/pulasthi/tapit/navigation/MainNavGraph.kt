package com.pulasthi.tapit.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pulasthi.tapit.ui.components.TapItBottomNavBar
import com.pulasthi.tapit.ui.history.HistoryScreen
import com.pulasthi.tapit.ui.home.HomeScreen
import com.pulasthi.tapit.ui.myqr.MyQrScreen

@Composable
fun MainScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToGovPay: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToPayBills: () -> Unit,
) {
    val mainNavController = rememberNavController()
    val navBackStackEntry by mainNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = mainNavController,
            startDestination = MainRoutes.HOME,
            modifier = Modifier.fillMaxSize(),
        ) {
            mainNavGraph(
                onNavigateToSettings = onNavigateToSettings,
                onNavigateToGovPay = onNavigateToGovPay,
                onNavigateToPayBills = onNavigateToPayBills,
            )
        }

        TapItBottomNavBar(
            items = BottomNavItem.entries,
            currentRoute = currentRoute,
            onItemClick = { item ->
                if (item == BottomNavItem.MyWallet) {
                    onNavigateToWallet()
                } else {
                    mainNavController.navigate(item.route) {
                        popUpTo(MainRoutes.HOME) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

private fun NavGraphBuilder.mainNavGraph(
    onNavigateToSettings: () -> Unit,
    onNavigateToGovPay: () -> Unit,
    onNavigateToPayBills: () -> Unit,
) {
    composable(MainRoutes.HOME) {
        HomeScreen(
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToGovPay = onNavigateToGovPay,
            onNavigateToPayBills = onNavigateToPayBills,
        )
    }
    composable(MainRoutes.MY_QR) {
        MyQrScreen()
    }
    composable(MainRoutes.HISTORY) {
        HistoryScreen()
    }
}
