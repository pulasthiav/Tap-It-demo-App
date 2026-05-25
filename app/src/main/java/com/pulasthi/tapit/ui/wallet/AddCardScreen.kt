package com.pulasthi.tapit.ui.wallet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.components.ColumnLabel
import com.pulasthi.tapit.ui.components.DashboardBackground
import com.pulasthi.tapit.ui.components.TapItTextField
import com.pulasthi.tapit.ui.theme.TapItError
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.ui.wallet.components.AddCardPreview
import com.pulasthi.tapit.ui.wallet.components.WalletTealButton
import com.pulasthi.tapit.viewmodel.AccountViewModel

@Composable
fun AddCardScreen(
    onBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    viewModel: AccountViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val form = uiState.addCardForm

    LaunchedEffect(Unit) {
        viewModel.navigateToAddSuccess.collect { onNavigateToSuccess() }
    }

    DashboardBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TapItWhite,
                )
            }

            Text(
                text = "Add Card",
                color = TapItWhite,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            AddCardPreview(
                form = form,
                modifier = Modifier.padding(bottom = 24.dp),
            )

            ColumnLabel(text = "Cardholder Name")
            TapItTextField(
                value = form.holderName,
                onValueChange = viewModel::onHolderNameChange,
                placeholder = "Cardholder Name",
            )
            form.holderNameError?.let { FieldError(it) }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Card Number")
            TapItTextField(
                value = form.cardNumber,
                onValueChange = viewModel::onCardNumberChange,
                placeholder = "Card Number",
            )
            form.cardNumberError?.let { FieldError(it) }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Expiry Date")
            TapItTextField(
                value = form.expiry,
                onValueChange = viewModel::onExpiryChange,
                placeholder = "MM/YY",
            )
            form.expiryError?.let { FieldError(it) }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "CVV")
            TapItTextField(
                value = form.cvv,
                onValueChange = viewModel::onCvvChange,
                placeholder = "CVV",
            )
            form.cvvError?.let { FieldError(it) }

            WalletTealButton(
                text = "Add Card",
                onClick = viewModel::onSubmitAddCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 28.dp),
            )
        }
    }
}

@Composable
private fun FieldError(message: String) {
    Text(
        text = message,
        color = TapItError,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, start = 4.dp),
    )
}
