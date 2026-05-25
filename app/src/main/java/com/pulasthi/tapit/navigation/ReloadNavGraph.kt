package com.pulasthi.tapit.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.pulasthi.tapit.ui.reload.ReloadConfirmScreen
import com.pulasthi.tapit.ui.reload.ReloadFormScreen
import com.pulasthi.tapit.ui.reload.ReloadScreen
import com.pulasthi.tapit.ui.reload.ReloadSuccessScreen
import com.pulasthi.tapit.viewmodel.ReloadViewModel

fun NavGraphBuilder.reloadNavGraph(
    navController: NavHostController,
    onExitFlow: () -> Unit,
) {
    navigation(
        route = ReloadRoutes.GRAPH,
        startDestination = ReloadRoutes.HOME,
    ) {
        composable(ReloadRoutes.HOME) { entry ->
            val viewModel: ReloadViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(ReloadRoutes.GRAPH)
                },
            )
            ReloadScreen(
                viewModel = viewModel,
                onBack = onExitFlow,
                onNavigateToPaymentForm = {
                    navController.navigate(ReloadRoutes.PAYMENT_FORM)
                },
            )
        }

        composable(ReloadRoutes.PAYMENT_FORM) { entry ->
            val viewModel: ReloadViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(ReloadRoutes.GRAPH)
                },
            )
            ReloadFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToConfirm = {
                    navController.navigate(ReloadRoutes.CONFIRM)
                },
            )
        }

        composable(ReloadRoutes.CONFIRM) { entry ->
            val viewModel: ReloadViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(ReloadRoutes.GRAPH)
                },
            )
            ReloadConfirmScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToSuccess = {
                    navController.navigate(ReloadRoutes.SUCCESS) {
                        popUpTo(ReloadRoutes.HOME) { inclusive = false }
                    }
                },
            )
        }

        composable(ReloadRoutes.SUCCESS) { entry ->
            val viewModel: ReloadViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(ReloadRoutes.GRAPH)
                },
            )
            ReloadSuccessScreen(
                viewModel = viewModel,
                onDone = {
                    navController.popBackStack(ReloadRoutes.GRAPH, inclusive = true)
                },
            )
        }
    }
}
