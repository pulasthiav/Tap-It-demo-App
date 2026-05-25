package com.pulasthi.tapit.viewmodel

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileField(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val isEditable: Boolean = false,
)

data class ProfileUiState(
    val fields: List<ProfileField> = emptyList(),
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        ProfileUiState(
            fields = listOf(
                ProfileField("Last Name", "Wanninayaka Mudiyaselage", Icons.Default.Person),
                ProfileField("Other Name", "Avinash", Icons.Default.Person),
                ProfileField("Preferred Name.", "Pulasthi", Icons.Default.Person),
                ProfileField("E-Mail Address", "pulasthiavinash7@gmail.com", Icons.Default.Email, isEditable = true),
                ProfileField("Mobile Number", "0742214144", Icons.Default.Phone),
                ProfileField("National Identity Card", "200226802976", Icons.Default.Person),
            ),
        ),
    )
    val uiState = _uiState.asStateFlow()
}
