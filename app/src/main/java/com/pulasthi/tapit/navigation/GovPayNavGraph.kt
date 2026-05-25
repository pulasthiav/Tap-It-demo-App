package com.pulasthi.tapit.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.pulasthi.tapit.ui.govpay.GovPayFinesFormScreen
import com.pulasthi.tapit.ui.govpay.GovPayPayScreen
import com.pulasthi.tapit.ui.govpay.GovPayScreen
import com.pulasthi.tapit.ui.govpay.GovPaySuccessScreen
import com.pulasthi.tapit.viewmodel.GovPayViewModel

fun NavGraphBuilder.govPayNavGraph(
    navController: NavHostController,
    onExitFlow: () -> Unit,
) {
    navigation(
        route = GovPayRoutes.GRAPH,
        startDestination = GovPayRoutes.HOME,
    ) {
        composable(GovPayRoutes.HOME) { entry ->
            val viewModel: GovPayViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(GovPayRoutes.GRAPH)
                },
            )
            GovPayScreen(
                viewModel = viewModel,
                onBack = onExitFlow,
                onNavigateToFinesForm = {
                    navController.navigate(GovPayRoutes.FINES_FORM)
                },
            )
        }

        composable(GovPayRoutes.FINES_FORM) { entry ->
            val viewModel: GovPayViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(GovPayRoutes.GRAPH)
                },
            )
            GovPayFinesFormScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToPayFines = {
                    navController.navigate(GovPayRoutes.PAY_FINES)
                },
            )
        }

        composable(GovPayRoutes.PAY_FINES) { entry ->
            val viewModel: GovPayViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(GovPayRoutes.GRAPH)
                },
            )
            GovPayPayScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToSuccess = {
                    navController.navigate(GovPayRoutes.SUCCESS) {
                        popUpTo(GovPayRoutes.HOME) { inclusive = false }
                    }
                },
            )
        }

        composable(GovPayRoutes.SUCCESS) { entry ->
            val viewModel: GovPayViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(GovPayRoutes.GRAPH)
                },
            )
            GovPaySuccessScreen(
                viewModel = viewModel,
                onDone = {
                    navController.popBackStack(GovPayRoutes.GRAPH, inclusive = true)
                },
            )
        }
    }
}
