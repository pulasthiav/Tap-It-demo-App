package com.pulasthi.tapit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pulasthi.tapit.ui.auth.CreateAccountScreen
import com.pulasthi.tapit.ui.auth.CreatePinScreen
import com.pulasthi.tapit.ui.auth.EnterPinScreen
import com.pulasthi.tapit.ui.auth.LoginScreen
import com.pulasthi.tapit.ui.auth.PinCreatedSuccessScreen
import com.pulasthi.tapit.ui.auth.ReEnterPinScreen
import com.pulasthi.tapit.ui.dashboard.DashboardScreen
import com.pulasthi.tapit.ui.splash.SplashScreen

@Composable
fun TapItNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = TapItRoutes.SPLASH,
        modifier = modifier,
    ) {
        composable(TapItRoutes.SPLASH) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(TapItRoutes.LOGIN) {
                        popUpTo(TapItRoutes.SPLASH) { inclusive = true }
                    }
                },
            )
        }

        composable(TapItRoutes.LOGIN) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(TapItRoutes.CREATE_ACCOUNT)
                },
                onNavigateToEnterPin = {
                    navController.navigate(TapItRoutes.ENTER_PIN)
                },
            )
        }

        composable(TapItRoutes.CREATE_ACCOUNT) {
            CreateAccountScreen(
                onNavigateToCreatePin = {
                    navController.navigate(TapItRoutes.CREATE_PIN)
                },
            )
        }

        composable(TapItRoutes.CREATE_PIN) {
            CreatePinScreen(
                onNavigateToReenterPin = { pin ->
                    navController.navigate(TapItRoutes.reenterPin(pin))
                },
            )
        }

        composable(
            route = TapItRoutes.REENTER_PIN,
            arguments = listOf(
                navArgument(TapItRoutes.ARG_EXPECTED_PIN) { type = NavType.StringType },
            ),
        ) { backStackEntry ->
            val expectedPin = backStackEntry.arguments?.getString(TapItRoutes.ARG_EXPECTED_PIN).orEmpty()
            ReEnterPinScreen(
                expectedPin = expectedPin,
                onNavigateToSuccess = {
                    navController.navigate(TapItRoutes.PIN_CREATED_SUCCESS)
                },
            )
        }

        composable(TapItRoutes.PIN_CREATED_SUCCESS) {
            PinCreatedSuccessScreen(
                onNavigateToLogin = {
                    navController.navigate(TapItRoutes.LOGIN) {
                        popUpTo(TapItRoutes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(TapItRoutes.ENTER_PIN) {
            EnterPinScreen(
                onNavigateToDashboard = {
                    navController.navigate(TapItRoutes.MAIN) {
                        popUpTo(TapItRoutes.LOGIN) { inclusive = false }
                    }
                },
            )
        }

        composable(TapItRoutes.MAIN) {
            DashboardScreen(
                onNavigateToSettings = {
                    navController.navigate(TapItRoutes.SETTINGS) {
                        launchSingleTop = true
                    }
                },
                onNavigateToGovPay = {
                    navController.navigate(TapItRoutes.GOV_PAY) {
                        launchSingleTop = true
                    }
                },
                onNavigateToWallet = {
                    navController.navigate(TapItRoutes.WALLET) {
                        launchSingleTop = true
                    }
                },
                onNavigateToPayBills = {
                    navController.navigate(TapItRoutes.PAY_BILLS) {
                        launchSingleTop = true
                    }
                },
                onNavigateToReload = {
                    navController.navigate(TapItRoutes.RELOAD) {
                        launchSingleTop = true
                    }
                },
                onNavigateToSendMoney = {
                    navController.navigate(TapItRoutes.SEND_MONEY) {
                        launchSingleTop = true
                    }
                },
                onNavigateToQrPay = {
                    navController.navigate(TapItRoutes.QR_PAY) {
                        launchSingleTop = true
                    }
                },
                onNavigateToSchedule = {
                    navController.navigate(TapItRoutes.SCHEDULE) {
                        launchSingleTop = true
                    }
                },
            )
        }

        scheduleNavGraph(
            navController = navController,
            onExitFlow = { navController.popBackStack() },
        )

        qrPayNavGraph(
            navController = navController,
            onExitFlow = { navController.popBackStack() },
        )

        sendMoneyNavGraph(
            navController = navController,
            onExitFlow = { navController.popBackStack() },
        )

        reloadNavGraph(
            navController = navController,
            onExitFlow = { navController.popBackStack() },
        )

        payBillsNavGraph(
            navController = navController,
            onExitFlow = { navController.popBackStack() },
        )

        walletNavGraph(
            navController = navController,
            onExitFlow = { navController.popBackStack() },
        )

        govPayNavGraph(
            navController = navController,
            onExitFlow = { navController.popBackStack() },
        )

        composable(TapItRoutes.SETTINGS) {
            SettingsNavGraph(
                onBackFromSettings = { navController.popBackStack() },
                onNavigateToWallet = {
                    navController.navigate(TapItRoutes.WALLET) {
                        launchSingleTop = true
                    }
                },
                onLogout = {
                    navController.navigate(TapItRoutes.LOGIN) {
                        popUpTo(TapItRoutes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(TapItRoutes.LOGIN) {
                        popUpTo(TapItRoutes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
