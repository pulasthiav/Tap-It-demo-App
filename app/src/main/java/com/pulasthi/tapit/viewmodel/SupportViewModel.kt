package com.pulasthi.tapit.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SupportContactItem(
    val id: String,
    val title: String,
    val subtitle: String?,
    val icon: ImageVector,
)

data class SupportUiState(
    val contacts: List<SupportContactItem> = listOf(
        SupportContactItem("mobile", "Contact by Mobile", "+94 (11) 771 4154", Icons.Default.Phone),
        SupportContactItem("email", "Contact by Email", "support@tapit.lk.", Icons.Default.Email),
        SupportContactItem("feedback", "Leave a Feedback", null, Icons.Default.Feedback),
    ),
)

class SupportViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SupportUiState())
    val uiState = _uiState.asStateFlow()
}
