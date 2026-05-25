package com.pulasthi.tapit.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.pulasthi.tapit.ui.wallet.AddCardScreen
import com.pulasthi.tapit.ui.wallet.AddCardSuccessScreen
import com.pulasthi.tapit.ui.wallet.RemoveCardScreen
import com.pulasthi.tapit.ui.wallet.WalletScreen
import com.pulasthi.tapit.viewmodel.AccountViewModel

fun NavGraphBuilder.walletNavGraph(
    navController: NavHostController,
    onExitFlow: () -> Unit,
) {
    navigation(
        route = WalletRoutes.GRAPH,
        startDestination = WalletRoutes.HOME,
    ) {
        composable(WalletRoutes.HOME) { entry ->
            val viewModel: AccountViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(WalletRoutes.GRAPH)
                },
            )
            WalletScreen(
                viewModel = viewModel,
                onBack = onExitFlow,
                onNavigateToAddCard = {
                    navController.navigate(WalletRoutes.ADD_CARD)
                },
                onNavigateToRemoveSelect = {
                    navController.navigate(WalletRoutes.REMOVE_SELECT)
                },
            )
        }

        composable(WalletRoutes.ADD_CARD) { entry ->
            val viewModel: AccountViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(WalletRoutes.GRAPH)
                },
            )
            AddCardScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToSuccess = {
                    navController.navigate(WalletRoutes.ADD_SUCCESS) {
                        popUpTo(WalletRoutes.ADD_CARD) { inclusive = true }
                    }
                },
            )
        }

        composable(WalletRoutes.ADD_SUCCESS) {
            AddCardSuccessScreen(
                onDone = {
                    navController.popBackStack(WalletRoutes.HOME, inclusive = false)
                },
            )
        }

        composable(WalletRoutes.REMOVE_SELECT) { entry ->
            val viewModel: AccountViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(WalletRoutes.GRAPH)
                },
            )
            RemoveCardScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onCardsRemoved = {
                    navController.popBackStack(WalletRoutes.HOME, inclusive = false)
                },
            )
        }
    }
}
