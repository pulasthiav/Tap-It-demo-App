package com.pulasthi.tapit.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.viewmodel.CreatePinViewModel

@Composable
fun CreatePinScreen(
    onNavigateToReenterPin: (String) -> Unit,
    viewModel: CreatePinViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToReenterPin.collect { pin ->
            onNavigateToReenterPin(pin)
        }
    }

    PinEntryScreen(
        title = "Create PIN Code",
        pinLength = uiState.pin.length,
        onDigitClick = viewModel::onDigitPressed,
        onBackspaceClick = viewModel::onBackspace,
    )
}
