package com.pulasthi.tapit.ui.reload

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.pulasthi.tapit.ui.theme.TapItInputBackground
import com.pulasthi.tapit.ui.theme.TapItReloadPrepaidTeal
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.ReloadAmountOption
import com.pulasthi.tapit.viewmodel.ReloadViewModel

@Composable
fun ReloadFormScreen(
    onBack: () -> Unit,
    onNavigateToConfirm: () -> Unit,
    viewModel: ReloadViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.refreshWalletCards()
    }

    LaunchedEffect(Unit) {
        viewModel.navigateToConfirm.collect { onNavigateToConfirm() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TapItBluePrimary),
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
                text = "Reload",
                color = TapItWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, bottom = 8.dp),
            )
            PaymentCardCarousel(
                cards = uiState.cards,
                selectedIndex = uiState.selectedCardIndex,
                onCardSelected = viewModel::onCardSelected,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }

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
                MobilePrepaidRow(operatorName = uiState.selectedOperator.name)

                Spacer(modifier = Modifier.height(20.dp))

                ColumnLabel(text = "Amount")
                TapItTextField(
                    value = when (uiState.amountOption) {
                        ReloadAmountOption.CUSTOM -> uiState.customAmount
                        ReloadAmountOption.RS_50 -> "50"
                        ReloadAmountOption.RS_100 -> "100"
                    },
                    onValueChange = viewModel::onCustomAmountChange,
                    placeholder = "LKR",
                    keyboardType = KeyboardType.Decimal,
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
                    onClick = viewModel::onPaymentFormNext,
                    enabled = uiState.cards.isNotEmpty(),
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
                    Text("Next", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun MobilePrepaidRow(operatorName: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = CircleShape,
            color = TapItReloadPrepaidTeal,
        ) {
            Icon(
                imageVector = Icons.Default.PhoneAndroid,
                contentDescription = null,
                tint = TapItWhite,
                modifier = Modifier
                    .padding(10.dp)
                    .size(32.dp),
            )
        }
        Column(modifier = Modifier.padding(start = 14.dp)) {
            Text(
                text = "Pay To",
                color = TapItTextSecondary,
                fontSize = 12.sp,
            )
            Text(
                text = "Mobile Prepaid",
                color = TapItTextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = operatorName,
                color = TapItTextSecondary,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
