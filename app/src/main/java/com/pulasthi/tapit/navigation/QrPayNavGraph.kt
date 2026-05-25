package com.pulasthi.tapit.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.pulasthi.tapit.ui.qr.QRPayScreen
import com.pulasthi.tapit.ui.qr.QrPayConfirmScreen
import com.pulasthi.tapit.ui.qr.QrPaySuccessScreen
import com.pulasthi.tapit.viewmodel.QRViewModel

fun NavGraphBuilder.qrPayNavGraph(
    navController: NavHostController,
    onExitFlow: () -> Unit,
) {
    navigation(
        route = QrPayRoutes.GRAPH,
        startDestination = QrPayRoutes.SCAN,
    ) {
        composable(QrPayRoutes.SCAN) { entry ->
            val viewModel: QRViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(QrPayRoutes.GRAPH)
                },
            )
            QRPayScreen(
                viewModel = viewModel,
                onBack = onExitFlow,
                onNavigateToConfirm = {
                    navController.navigate(QrPayRoutes.CONFIRM)
                },
            )
        }

        composable(QrPayRoutes.CONFIRM) { entry ->
            val viewModel: QRViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(QrPayRoutes.GRAPH)
                },
            )
            QrPayConfirmScreen(
                viewModel = viewModel,
                onBack = {
                    viewModel.resetForNewScan()
                    navController.popBackStack()
                },
                onNavigateToSuccess = {
                    navController.navigate(QrPayRoutes.SUCCESS) {
                        popUpTo(QrPayRoutes.SCAN) { inclusive = false }
                    }
                },
            )
        }

        composable(QrPayRoutes.SUCCESS) { entry ->
            val viewModel: QRViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(QrPayRoutes.GRAPH)
                },
            )
            QrPaySuccessScreen(
                viewModel = viewModel,
                onDone = {
                    navController.popBackStack(QrPayRoutes.GRAPH, inclusive = true)
                },
            )
        }
    }
}
