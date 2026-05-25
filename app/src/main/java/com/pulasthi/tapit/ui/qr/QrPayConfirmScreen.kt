package com.pulasthi.tapit.ui.qr

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.components.ColumnLabel
import com.pulasthi.tapit.ui.components.DashboardBackground
import com.pulasthi.tapit.ui.components.TapItTextField
import com.pulasthi.tapit.ui.govpay.components.PaymentCardCarousel
import com.pulasthi.tapit.ui.govpay.components.PaymentCardView
import com.pulasthi.tapit.ui.theme.TapItError
import com.pulasthi.tapit.ui.theme.TapItReloadConfirmPurple
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.QRViewModel
import com.pulasthi.tapit.viewmodel.QrPaymentStatus

@Composable
fun QrPayConfirmScreen(
    onBack: () -> Unit,
    onNavigateToSuccess: () -> Unit,
    viewModel: QRViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val details = uiState.paymentDetails
    val card = uiState.cards.getOrNull(uiState.selectedCardIndex)

    LaunchedEffect(Unit) {
        viewModel.refreshWalletCards()
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToSuccess.collect { onNavigateToSuccess() }
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
                text = "Confirm QR Payment",
                color = TapItWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            Text(
                text = "From",
                color = TapItWhite,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp),
            )

            if (uiState.cards.isNotEmpty()) {
                PaymentCardCarousel(
                    cards = uiState.cards,
                    selectedIndex = uiState.selectedCardIndex,
                    onCardSelected = viewModel::onCardSelected,
                    fromLabel = "",
                )
            } else if (card != null) {
                PaymentCardView(card = card, modifier = Modifier.padding(bottom = 16.dp))
            }

            Text(
                text = "To",
                color = TapItWhite,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(16.dp),
                color = TapItWhite,
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = details?.merchantName ?: "Merchant",
                        color = com.pulasthi.tapit.ui.theme.TapItTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    details?.bankName?.takeIf { it.isNotBlank() }?.let { bank ->
                        Text(
                            text = bank,
                            color = TapItTextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    details?.accountReference?.takeIf { it.isNotBlank() }?.let { account ->
                        Text(
                            text = account,
                            color = TapItTextSecondary,
                            fontSize = 13.sp,
                        )
                    }
                }
            }

            ColumnLabel(text = "Amount (LKR)")
            TapItTextField(
                value = uiState.amount,
                onValueChange = viewModel::onAmountChange,
                placeholder = "LKR",
                keyboardType = KeyboardType.Decimal,
            )
            uiState.amountError?.let {
                Text(
                    text = it,
                    color = TapItError,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                color = TapItWhite.copy(alpha = 0.35f),
            )

            SummaryRow(label = "Pay amount", value = uiState.formattedAmount)

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::onConfirmPayment,
                enabled = uiState.paymentStatus != QrPaymentStatus.Processing,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TapItReloadConfirmPurple,
                    contentColor = TapItWhite,
                ),
            ) {
                if (uiState.paymentStatus == QrPaymentStatus.Processing) {
                    CircularProgressIndicator(
                        color = TapItWhite,
                        modifier = Modifier.height(22.dp),
                    )
                } else {
                    Text("Confirm Payment", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = TapItWhite,
            fontSize = 15.sp,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            color = TapItWhite,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
