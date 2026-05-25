package com.pulasthi.tapit.ui.beneficiaries

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.pulasthi.tapit.ui.components.TapItTextField
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItError
import com.pulasthi.tapit.ui.theme.TapItLinkBlue
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.BeneficiaryViewModel

@Composable
fun AddEditBeneficiaryScreen(
    beneficiaryId: String?,
    onBack: () -> Unit,
    viewModel: BeneficiaryViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(beneficiaryId) {
        if (beneficiaryId != null) {
            viewModel.startEditForm(beneficiaryId)
        } else {
            viewModel.startAddForm()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigateBackAfterSave.collect { onBack() }
    }

    BeneficiaryFlowScaffold(
        title = uiState.formTitle,
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
        ) {
            ColumnLabel(text = "Beneficiary Name")
            TapItTextField(
                value = uiState.formName,
                onValueChange = viewModel::onFormNameChange,
                placeholder = "Beneficiary Name",
            )
            uiState.formNameError?.let { FormError(it) }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Bank Name")
            TapItTextField(
                value = uiState.formBank,
                onValueChange = viewModel::onFormBankChange,
                placeholder = "Bank Name",
            )
            uiState.formBankError?.let { FormError(it) }

            Spacer(modifier = Modifier.height(12.dp))

            ColumnLabel(text = "Account Number")
            TapItTextField(
                value = uiState.formAccountNumber,
                onValueChange = viewModel::onFormAccountChange,
                placeholder = "Account Number",
                keyboardType = KeyboardType.Number,
            )
            uiState.formAccountError?.let { FormError(it) }

            Spacer(modifier = Modifier.height(28.dp))

            Button(
                onClick = viewModel::onSaveForm,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TapItBluePrimary,
                    contentColor = TapItWhite,
                ),
            ) {
                Text(
                    text = if (uiState.isEditMode) "Save Beneficiary" else "Save Beneficiary",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            TextButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            ) {
                Text(
                    text = "Cancel",
                    color = TapItLinkBlue,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun FormError(message: String) {
    Text(
        text = message,
        color = TapItError,
        fontSize = 12.sp,
        modifier = Modifier.padding(top = 4.dp),
    )
}
