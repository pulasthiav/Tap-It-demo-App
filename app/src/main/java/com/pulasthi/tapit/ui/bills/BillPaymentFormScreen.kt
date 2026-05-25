package com.pulasthi.tapit.ui.bills

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.components.ColumnLabel
import com.pulasthi.tapit.ui.components.TapItTextField
import com.pulasthi.tapit.ui.govpay.components.PaymentCardCarousel
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItError
import com.pulasthi.tapit.ui.theme.TapItInputBackground
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.BillPaymentStatus
import com.pulasthi.tapit.viewmodel.BillProvider
import com.pulasthi.tapit.viewmodel.PayBillsViewModel

@Composable
fun BillPaymentFormScreen(
    onBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    viewModel: PayBillsViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val provider = uiState.selectedProvider

    LaunchedEffect(Unit) {
        viewModel.refreshWalletCards()
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToSuccess.collect { onNavigateToSuccess() }
    }

    if (provider == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TapItBluePrimary),
    ) {
        BillPaymentBlueHeader(
            title = uiState.paymentFormTitle,
            onBack = onBack,
            cards = uiState.cards,
            selectedCardIndex = uiState.selectedCardIndex,
            onCardSelected = viewModel::onCardSelected,
        )

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            color = TapItInputBackground,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 24.dp),
            ) {
                BillerBrandingRow(provider = provider)

                Spacer(modifier = Modifier.height(20.dp))

                ColumnLabel(text = "Account Number")
                TapItTextField(
                    value = uiState.accountNumber,
                    onValueChange = viewModel::onAccountNumberChange,
                    placeholder = "Account No",
                    keyboardType = KeyboardType.Text,
                )
                uiState.accountNumberError?.let { error ->
                    FormErrorText(message = error)
                }

                Spacer(modifier = Modifier.height(16.dp))

                ColumnLabel(text = "Amount")
                TapItTextField(
                    value = uiState.amount,
                    onValueChange = viewModel::onAmountChange,
                    placeholder = "LKR",
                    keyboardType = KeyboardType.Decimal,
                )
                uiState.amountError?.let { error ->
                    FormErrorText(message = error)
                }

                Spacer(modifier = Modifier.height(12.dp))

                BillPaymentOptions()

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = viewModel::onProcessPayment,
                    enabled = uiState.paymentStatus != BillPaymentStatus.Processing &&
                        uiState.cards.isNotEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TapItBluePrimary,
                        contentColor = TapItWhite,
                    ),
                ) {
                    if (uiState.paymentStatus == BillPaymentStatus.Processing) {
                        CircularProgressIndicator(
                            color = TapItWhite,
                            modifier = Modifier.height(22.dp),
                        )
                    } else {
                        Text(
                            text = "Process Payment",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BillerBrandingRow(provider: BillProvider) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BillProviderLogo(provider = provider, size = 52.dp)
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(
                text = "Pay To",
                color = TapItTextSecondary,
                fontSize = 12.sp,
            )
            Text(
                text = provider.name,
                color = TapItTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun BillPaymentOptions() {
    var sendSms by remember { mutableStateOf(false) }
    var saveFavourite by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = sendSms,
            onCheckedChange = { sendSms = it },
            colors = CheckboxDefaults.colors(checkedColor = TapItBluePrimary),
        )
        Text(
            text = "Send SMS to Receiver",
            color = TapItTextSecondary,
            fontSize = 14.sp,
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = saveFavourite,
            onCheckedChange = { saveFavourite = it },
            colors = CheckboxDefaults.colors(checkedColor = TapItBluePrimary),
        )
        Text(
            text = "Save As Favourite",
            color = TapItTextSecondary,
            fontSize = 14.sp,
        )
    }
}

@Composable
private fun BillPaymentBlueHeader(
    title: String,
    onBack: () -> Unit,
    cards: List<com.pulasthi.tapit.viewmodel.GovPaymentCard>,
    selectedCardIndex: Int,
    onCardSelected: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = TapItWhite,
            )
        }
        Text(
            text = title,
            color = TapItWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 12.dp),
        )
        PaymentCardCarousel(
            cards = cards,
            selectedIndex = selectedCardIndex,
            onCardSelected = onCardSelected,
            modifier = Modifier.padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun FormErrorText(message: String) {
    Text(
        text = message,
        color = TapItError,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp, start = 4.dp),
    )
}
