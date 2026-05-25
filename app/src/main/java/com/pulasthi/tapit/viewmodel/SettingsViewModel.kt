package com.pulasthi.tapit.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class SettingsMenuItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val route: String,
)

data class SettingsUiState(
    val menuItems: List<SettingsMenuItem> = emptyList(),
)

class SettingsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState(menuItems = menuItems))
    val uiState = _uiState.asStateFlow()

    private val _navigateTo = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navigateTo = _navigateTo.asSharedFlow()

    private val _logout = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val logout = _logout.asSharedFlow()

    private val _navigateToWallet = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToWallet = _navigateToWallet.asSharedFlow()

    fun onMenuItemClick(item: SettingsMenuItem) {
        if (item.id == "my_account") {
            _navigateToWallet.tryEmit(Unit)
            return
        }
        _navigateTo.tryEmit(item.route)
    }

    fun onLogoutClick() {
        _logout.tryEmit(Unit)
    }

    companion object {
        private val menuItems = listOf(
            SettingsMenuItem("my_account", "My Account", Icons.Default.Shield, ""),
            SettingsMenuItem("profile", "Profile", Icons.Default.AccountCircle, com.pulasthi.tapit.navigation.SettingsRoutes.PROFILE),
            SettingsMenuItem("security", "Security", Icons.Default.Security, com.pulasthi.tapit.navigation.SettingsRoutes.SECURITY),
            SettingsMenuItem("language", "Language", Icons.Default.Language, com.pulasthi.tapit.navigation.SettingsRoutes.LANGUAGE),
            SettingsMenuItem("support", "Support", Icons.Default.HeadsetMic, com.pulasthi.tapit.navigation.SettingsRoutes.SUPPORT),
            SettingsMenuItem("terms", "Terms & Condition", Icons.Default.Description, com.pulasthi.tapit.navigation.SettingsRoutes.TERMS),
        )
    }
}
