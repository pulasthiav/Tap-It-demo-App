package com.pulasthi.tapit.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class DashboardServiceItem(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val iconTint: Color,
)

data class HomeUiState(
    val bannerTitleLine1: String = "The Secret to Safe",
    val bannerTitleLine2: String = "Online Money Transfers",
    val services: List<DashboardServiceItem> = emptyList(),
)

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        HomeUiState(services = defaultServices),
    )
    val uiState = _uiState.asStateFlow()

    private val _serviceSelected = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val serviceSelected = _serviceSelected.asSharedFlow()

    fun onServiceClick(serviceId: String) {
        _serviceSelected.tryEmit(serviceId)
    }

    companion object {
        private val defaultServices = listOf(
            DashboardServiceItem(
                id = "send_money",
                title = "Send Money",
                icon = Icons.AutoMirrored.Filled.Send,
                iconTint = Color(0xFFFFB300),
            ),
            DashboardServiceItem(
                id = "reload",
                title = "Reload",
                icon = Icons.Default.PhoneAndroid,
                iconTint = Color(0xFF42A5F5),
            ),
            DashboardServiceItem(
                id = "pay_bills",
                title = "Pay Bills",
                icon = Icons.Default.Payments,
                iconTint = Color(0xFF66BB6A),
            ),
            DashboardServiceItem(
                id = "qr_pay",
                title = "QR Pay",
                icon = Icons.Default.QrCodeScanner,
                iconTint = Color(0xFFEF5350),
            ),
            DashboardServiceItem(
                id = "schedule_payment",
                title = "Schedule payment",
                icon = Icons.Default.CalendarMonth,
                iconTint = Color(0xFFAB47BC),
            ),
            DashboardServiceItem(
                id = "gov_pay",
                title = "Gov Pay",
                icon = Icons.Default.AccountBalance,
                iconTint = Color(0xFF26A69A),
            ),
        )
    }
}
