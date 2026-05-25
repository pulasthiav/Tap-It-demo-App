package com.pulasthi.tapit.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.viewmodel.ReEnterPinViewModel

@Composable
fun ReEnterPinScreen(
    expectedPin: String,
    onNavigateToSuccess: () -> Unit,
) {
    val viewModel: ReEnterPinViewModel = viewModel(key = "reenter_pin_$expectedPin") {
        ReEnterPinViewModel(expectedPin)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToSuccess.collect {
            onNavigateToSuccess()
        }
    }

    PinEntryScreen(
        title = "Re-Enter PIN Code",
        pinLength = uiState.pin.length,
        onDigitClick = viewModel::onDigitPressed,
        onBackspaceClick = viewModel::onBackspace,
        errorMessage = uiState.errorMessage,
    )
}
