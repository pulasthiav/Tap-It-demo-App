package com.pulasthi.tapit.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val isCenterEmphasis: Boolean = false,
) {
    Home(
        route = MainRoutes.HOME,
        label = "Home",
        icon = Icons.Default.Home,
    ),
    MyQr(
        route = MainRoutes.MY_QR,
        label = "My QR",
        icon = Icons.Default.QrCode,
    ),
    MyWallet(
        route = MainRoutes.MY_WALLET,
        label = "My Wallet",
        icon = Icons.Default.AccountBalanceWallet,
        isCenterEmphasis = true,
    ),
    History(
        route = MainRoutes.HISTORY,
        label = "History",
        icon = Icons.Default.History,
    ),
}
