package com.pulasthi.tapit.ui.sendmoney

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItError
import com.pulasthi.tapit.ui.theme.TapItLinkBlue
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.SendMoneyViewModel

@Composable
fun SendMoneyScreen(
    onBack: () -> Unit,
    onNavigateToAmount: () -> Unit,
    onChangeBeneficiary: () -> Unit,
    viewModel: SendMoneyViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToAmount.collect { onNavigateToAmount() }
    }

    SendMoneyScaffold(
        title = "Send Money",
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            if (uiState.hasSelectedBeneficiary) {
                SelectedRecipientCard(
                    name = uiState.recipientName,
                    bank = uiState.recipientBank,
                    account = uiState.recipientAccount,
                    onChangeClick = onChangeBeneficiary,
                )
                Spacer(modifier = Modifier.height(16.dp))
            } else {
                Text(
                    text = "Enter recipient details",
                    color = TapItTextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onChangeBeneficiary) {
                    Text(
                        text = "Pick from saved beneficiaries",
                        color = TapItLinkBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            ColumnLabel(text = "Mobile No")
            TapItTextField(
                value = uiState.recipientMobile,
                onValueChange = viewModel::onRecipientMobileChange,
                placeholder = "Mobile No",
                keyboardType = KeyboardType.Phone,
            )

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Receiver Name")
            TapItTextField(
                value = uiState.recipientName,
                onValueChange = viewModel::onRecipientNameChange,
                placeholder = "Name",
            )
            uiState.recipientNameError?.let { FieldError(it) }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Receiver Bank")
            TapItTextField(
                value = uiState.recipientBank,
                onValueChange = viewModel::onRecipientBankChange,
                placeholder = "Bank Name",
            )
            uiState.recipientBankError?.let { FieldError(it) }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Receiver Account No")
            TapItTextField(
                value = uiState.recipientAccount,
                onValueChange = viewModel::onRecipientAccountChange,
                placeholder = "Account No",
                keyboardType = KeyboardType.Number,
            )
            uiState.recipientAccountError?.let { FieldError(it) }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Remarks")
            TapItTextField(
                value = uiState.remarks,
                onValueChange = viewModel::onRemarksChange,
                placeholder = "Transaction Remark",
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = uiState.sendSmsToReceiver,
                    onCheckedChange = viewModel::onSendSmsChanged,
                    colors = CheckboxDefaults.colors(checkedColor = TapItBluePrimary),
                )
                Text("Send SMS to Receiver", color = TapItTextSecondary, fontSize = 14.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = uiState.saveAsFavourite,
                    onCheckedChange = viewModel::onSaveAsFavouriteChanged,
                    colors = CheckboxDefaults.colors(checkedColor = TapItBluePrimary),
                )
                Text("Save As Favourite", color = TapItTextSecondary, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::onRecipientNext,
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

@Composable
private fun SelectedRecipientCard(
    name: String,
    bank: String,
    account: String,
    onChangeClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = TapItWhite,
        shadowElevation = 2.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color = TapItBluePrimary.copy(alpha = 0.15f),
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = TapItBluePrimary,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(24.dp),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    text = name,
                    color = TapItTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(text = bank, color = TapItTextSecondary, fontSize = 13.sp)
                Text(text = account, color = TapItTextSecondary, fontSize = 13.sp)
            }
            Text(
                text = "Change",
                color = TapItLinkBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onChangeClick),
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
        modifier = Modifier.padding(top = 4.dp),
    )
}
