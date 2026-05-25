package com.pulasthi.tapit.navigation

import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.pulasthi.tapit.ui.beneficiaries.AddEditBeneficiaryScreen
import com.pulasthi.tapit.ui.beneficiaries.BeneficiaryListScreen
import com.pulasthi.tapit.ui.sendmoney.AmountEntryScreen
import com.pulasthi.tapit.ui.sendmoney.ConfirmTransferScreen
import com.pulasthi.tapit.ui.sendmoney.SendMoneyScreen
import com.pulasthi.tapit.ui.sendmoney.SendMoneySuccessScreen
import com.pulasthi.tapit.viewmodel.BeneficiaryViewModel
import com.pulasthi.tapit.viewmodel.SendMoneyViewModel

fun NavGraphBuilder.sendMoneyNavGraph(
    navController: NavHostController,
    onExitFlow: () -> Unit,
) {
    navigation(
        route = SendMoneyRoutes.GRAPH,
        startDestination = SendMoneyRoutes.BENEFICIARY_LIST,
    ) {
        composable(SendMoneyRoutes.BENEFICIARY_LIST) { entry ->
            val beneficiaryViewModel: BeneficiaryViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(SendMoneyRoutes.GRAPH)
                },
            )
            val sendMoneyViewModel: SendMoneyViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(SendMoneyRoutes.GRAPH)
                },
            )
            BeneficiaryListScreen(
                viewModel = beneficiaryViewModel,
                onBack = onExitFlow,
                onAddBeneficiary = {
                    navController.navigate(SendMoneyRoutes.ADD_BENEFICIARY)
                },
                onEditBeneficiary = { id ->
                    navController.navigate(SendMoneyRoutes.editBeneficiary(id))
                },
                onSelectBeneficiary = { beneficiary ->
                    sendMoneyViewModel.onBeneficiarySelected(beneficiary)
                    sendMoneyViewModel.refreshWalletCards()
                    navController.navigate(SendMoneyRoutes.AMOUNT)
                },
                onAnotherAccount = {
                    sendMoneyViewModel.clearRecipientForManualEntry()
                    navController.navigate(SendMoneyRoutes.RECIPIENT)
                },
            )
        }

        composable(SendMoneyRoutes.ADD_BENEFICIARY) { entry ->
            val beneficiaryViewModel: BeneficiaryViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(SendMoneyRoutes.GRAPH)
                },
            )
            AddEditBeneficiaryScreen(
                beneficiaryId = null,
                viewModel = beneficiaryViewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable(
            route = SendMoneyRoutes.EDIT_BENEFICIARY,
            arguments = listOf(
                navArgument(SendMoneyRoutes.ARG_BENEFICIARY_ID) { type = NavType.StringType },
            ),
        ) { entry ->
            val beneficiaryViewModel: BeneficiaryViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(SendMoneyRoutes.GRAPH)
                },
            )
            val beneficiaryId = entry.arguments?.getString(SendMoneyRoutes.ARG_BENEFICIARY_ID)
            AddEditBeneficiaryScreen(
                beneficiaryId = beneficiaryId,
                viewModel = beneficiaryViewModel,
                onBack = { navController.popBackStack() },
            )
        }

        composable(SendMoneyRoutes.RECIPIENT) { entry ->
            val viewModel: SendMoneyViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(SendMoneyRoutes.GRAPH)
                },
            )
            SendMoneyScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToAmount = {
                    navController.navigate(SendMoneyRoutes.AMOUNT)
                },
                onChangeBeneficiary = {
                    navController.popBackStack(
                        route = SendMoneyRoutes.BENEFICIARY_LIST,
                        inclusive = false,
                    )
                },
            )
        }

        composable(SendMoneyRoutes.AMOUNT) { entry ->
            val viewModel: SendMoneyViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(SendMoneyRoutes.GRAPH)
                },
            )
            AmountEntryScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToConfirm = {
                    navController.navigate(SendMoneyRoutes.CONFIRM)
                },
            )
        }

        composable(SendMoneyRoutes.CONFIRM) { entry ->
            val viewModel: SendMoneyViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(SendMoneyRoutes.GRAPH)
                },
            )
            ConfirmTransferScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToSuccess = {
                    navController.navigate(SendMoneyRoutes.SUCCESS) {
                        popUpTo(SendMoneyRoutes.BENEFICIARY_LIST) { inclusive = false }
                    }
                },
            )
        }

        composable(SendMoneyRoutes.SUCCESS) { entry ->
            val viewModel: SendMoneyViewModel = viewModel(
                remember(entry) {
                    navController.getBackStackEntry(SendMoneyRoutes.GRAPH)
                },
            )
            SendMoneySuccessScreen(
                viewModel = viewModel,
                onDone = {
                    navController.popBackStack(SendMoneyRoutes.GRAPH, inclusive = true)
                },
            )
        }
    }
}
