package com.pulasthi.tapit.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.viewmodel.EnterPinViewModel

@Composable
fun EnterPinScreen(
    onNavigateToDashboard: () -> Unit,
    viewModel: EnterPinViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToDashboard.collect {
            onNavigateToDashboard()
        }
    }

    PinEntryScreen(
        title = "Enter PIN Code",
        pinLength = uiState.pin.length,
        onDigitClick = viewModel::onDigitPressed,
        onBackspaceClick = viewModel::onBackspace,
    )
}
