package com.pulasthi.tapit.ui.govpay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
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
import com.pulasthi.tapit.ui.components.TapItTextField
import com.pulasthi.tapit.ui.govpay.components.PayToRow
import com.pulasthi.tapit.ui.govpay.components.PaymentCardCarousel
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItInputBackground
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.GovPayViewModel

@Composable
fun GovPayFinesFormScreen(
    onNavigateToPayFines: () -> Unit,
    onBack: () -> Unit,
    viewModel: GovPayViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToPayFines.collect { onNavigateToPayFines() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TapItBluePrimary),
    ) {
        FinesBlueHeader(
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
                PayToRow(
                    payToLabel = "Pay To",
                    recipientName = "Sri Lanka Police",
                    modifier = Modifier.padding(bottom = 24.dp),
                )

                ColumnLabel(text = "Amount", modifier = Modifier)
                TapItTextField(
                    value = uiState.amount,
                    onValueChange = viewModel::onAmountChange,
                    placeholder = "",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal,
                )

                Spacer(modifier = Modifier.height(16.dp))

                ColumnLabel(text = "Mobile No")
                TapItTextField(
                    value = uiState.mobileNumber,
                    onValueChange = viewModel::onMobileNumberChange,
                    placeholder = "Enter Mobile Number",
                    keyboardType = androidx.compose.ui.text.input.KeyboardType.Phone,
                )

                Spacer(modifier = Modifier.height(16.dp))

                ColumnLabel(text = "Remarks")
                TapItTextField(
                    value = uiState.remarks,
                    onValueChange = viewModel::onRemarksChange,
                    placeholder = "",
                )

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = viewModel::onFinesFormNext,
                    enabled = uiState.isFinesFormValid,
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
                    Text(text = "Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun FinesBlueHeader(
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
            text = "Fines",
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
