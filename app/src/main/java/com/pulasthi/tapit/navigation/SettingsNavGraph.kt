package com.pulasthi.tapit.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pulasthi.tapit.ui.settings.AccountDeletedScreen
import com.pulasthi.tapit.ui.settings.DeleteAccountPinScreen
import com.pulasthi.tapit.ui.settings.LanguageScreen
import com.pulasthi.tapit.ui.settings.ProfileScreen
import com.pulasthi.tapit.ui.settings.SecurityScreen
import com.pulasthi.tapit.ui.settings.SettingsScreen
import com.pulasthi.tapit.ui.settings.SettingsSubScreen
import com.pulasthi.tapit.ui.settings.SupportScreen
import com.pulasthi.tapit.ui.settings.TermsConditionsScreen

@Composable
fun SettingsNavGraph(
    onBackFromSettings: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = SettingsRoutes.HOME,
        modifier = modifier,
    ) {
        settingsGraph(
            navController = navController,
            onBackFromSettings = onBackFromSettings,
            onNavigateToWallet = onNavigateToWallet,
            onLogout = onLogout,
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}

private fun NavGraphBuilder.settingsGraph(
    navController: NavHostController,
    onBackFromSettings: () -> Unit,
    onNavigateToWallet: () -> Unit,
    onLogout: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    composable(SettingsRoutes.HOME) {
        SettingsScreen(
            onNavigateTo = { route -> navController.navigate(route) },
            onNavigateToWallet = onNavigateToWallet,
            onLogout = onLogout,
            onBack = onBackFromSettings,
        )
    }
    composable(SettingsRoutes.PROFILE) {
        ProfileScreen(onBack = { navController.popBackStack() })
    }
    composable(SettingsRoutes.SECURITY) {
        SecurityScreen(
            onBack = { navController.popBackStack() },
            onNavigateTo = { route -> navController.navigate(route) },
            onNavigateToDeletePin = { navController.navigate(SettingsRoutes.DELETE_PIN_VERIFY) },
        )
    }
    composable(SettingsRoutes.LANGUAGE) {
        LanguageScreen(onBack = { navController.popBackStack() })
    }
    composable(SettingsRoutes.SUPPORT) {
        SupportScreen(onBack = { navController.popBackStack() })
    }
    composable(SettingsRoutes.TERMS) {
        TermsConditionsScreen(onBack = { navController.popBackStack() })
    }
    composable(SettingsRoutes.PRIVACY) {
        SettingsSubScreen(
            title = "Privacy Settings",
            message = "Privacy settings — coming soon",
            onBack = { navController.popBackStack() },
        )
    }
    composable(SettingsRoutes.NOTIFICATIONS) {
        SettingsSubScreen(
            title = "Notification Settings",
            message = "Notification settings — coming soon",
            onBack = { navController.popBackStack() },
        )
    }
    composable(SettingsRoutes.PIN_CHANGE) {
        SettingsSubScreen(
            title = "PIN Change",
            message = "PIN change flow — coming soon",
            onBack = { navController.popBackStack() },
        )
    }
    composable(SettingsRoutes.DELETE_PIN_VERIFY) {
        DeleteAccountPinScreen(
            onNavigateToAccountDeleted = {
                navController.navigate(SettingsRoutes.ACCOUNT_DELETED) {
                    popUpTo(SettingsRoutes.HOME) { inclusive = false }
                }
            },
        )
    }
    composable(SettingsRoutes.ACCOUNT_DELETED) {
        AccountDeletedScreen(
            onNavigateToLogin = onNavigateToLogin,
        )
    }
}
