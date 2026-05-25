package com.pulasthi.tapit.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.pulasthi.tapit.ui.bills.BillPaymentFormScreen
import com.pulasthi.tapit.ui.bills.BillPaymentSuccessScreen
import com.pulasthi.tapit.ui.bills.PayBillsScreen
import com.pulasthi.tapit.viewmodel.PayBillsViewModel

fun NavGraphBuilder.payBillsNavGraph(
    navController: NavHostController,
    onExitFlow: () -> Unit,
) {
    navigation(
        route = PayBillsRoutes.GRAPH,
        startDestination = PayBillsRoutes.CATEGORIES,
    ) {
        composable(PayBillsRoutes.CATEGORIES) { entry ->
            val viewModel: PayBillsViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(PayBillsRoutes.GRAPH)
                },
            )
            PayBillsScreen(
                viewModel = viewModel,
                onBack = onExitFlow,
                onNavigateToPaymentForm = {
                    navController.navigate(PayBillsRoutes.PAYMENT_FORM)
                },
            )
        }

        composable(PayBillsRoutes.PAYMENT_FORM) { entry ->
            val viewModel: PayBillsViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(PayBillsRoutes.GRAPH)
                },
            )
            BillPaymentFormScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack(PayBillsRoutes.CATEGORIES, inclusive = false)
                },
                onNavigateToSuccess = {
                    navController.navigate(PayBillsRoutes.SUCCESS) {
                        popUpTo(PayBillsRoutes.CATEGORIES) { inclusive = false }
                    }
                },
            )
        }

        composable(PayBillsRoutes.SUCCESS) { entry ->
            val viewModel: PayBillsViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(PayBillsRoutes.GRAPH)
                },
            )
            BillPaymentSuccessScreen(
                viewModel = viewModel,
                onDone = {
                    navController.popBackStack(PayBillsRoutes.GRAPH, inclusive = true)
                },
            )
        }
    }
}
