package com.pulasthi.tapit.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SecurityMenuItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val route: String? = null,
    val isDeleteAccount: Boolean = false,
)

data class SecurityUiState(
    val menuItems: List<SecurityMenuItem> = listOf(
        SecurityMenuItem("privacy", "Privacy Settings", Icons.Default.Shield, com.pulasthi.tapit.navigation.SettingsRoutes.PRIVACY),
        SecurityMenuItem("notifications", "Notification Settings", Icons.Default.Notifications, com.pulasthi.tapit.navigation.SettingsRoutes.NOTIFICATIONS),
        SecurityMenuItem("pin_change", "PIN Change", Icons.Default.Lock, com.pulasthi.tapit.navigation.SettingsRoutes.PIN_CHANGE),
        SecurityMenuItem("delete", "Delete Account", Icons.Default.Lock, isDeleteAccount = true),
    ),
    val showDeleteDialog: Boolean = false,
)

class SecurityViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SecurityUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigateTo = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val navigateTo = _navigateTo.asSharedFlow()

    private val _navigateToDeletePin = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigateToDeletePin = _navigateToDeletePin.asSharedFlow()

    fun onMenuItemClick(item: SecurityMenuItem) {
        if (item.isDeleteAccount) {
            _uiState.update { it.copy(showDeleteDialog = true) }
            return
        }
        item.route?.let { _navigateTo.tryEmit(it) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onConfirmDeleteAccount() {
        _uiState.update { it.copy(showDeleteDialog = false) }
        _navigateToDeletePin.tryEmit(Unit)
    }
}
