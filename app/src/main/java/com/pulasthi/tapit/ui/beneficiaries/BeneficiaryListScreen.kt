package com.pulasthi.tapit.ui.beneficiaries

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItLinkBlue
import com.pulasthi.tapit.ui.theme.TapItReloadConfirmPurple
import com.pulasthi.tapit.ui.theme.TapItTextHint
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.BeneficiaryViewModel
import com.pulasthi.tapit.viewmodel.TransferBeneficiary

@Composable
fun BeneficiaryListScreen(
    onBack: () -> Unit,
    onAddBeneficiary: () -> Unit,
    onEditBeneficiary: (String) -> Unit,
    onSelectBeneficiary: (TransferBeneficiary) -> Unit,
    onAnotherAccount: () -> Unit,
    viewModel: BeneficiaryViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.showDeleteSuccess.collect {
            snackbarHostState.showSnackbar("Beneficiary deleted successfully")
        }
    }

    uiState.pendingDelete?.let { beneficiary ->
        DeleteBeneficiaryDialog(
            beneficiary = beneficiary,
            onConfirm = viewModel::confirmDelete,
            onDismiss = viewModel::dismissDeleteDialog,
        )
    }

    BeneficiaryFlowScaffold(
        title = "Saved Beneficiaries",
        onBack = onBack,
        onAddClick = onAddBeneficiary,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
            ) {
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text("Search by name or Bank", color = TapItTextHint) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = TapItWhite,
                        unfocusedContainerColor = TapItWhite,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                ) {
                    items(
                        items = uiState.filteredBeneficiaries,
                        key = { it.id },
                    ) { beneficiary ->
                        BeneficiaryListItem(
                            beneficiary = beneficiary,
                            onSelect = { onSelectBeneficiary(beneficiary) },
                            onEdit = { onEditBeneficiary(beneficiary.id) },
                            onDelete = { viewModel.requestDelete(beneficiary) },
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                Button(
                    onClick = onAnotherAccount,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(bottom = 8.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TapItReloadConfirmPurple,
                        contentColor = TapItWhite,
                    ),
                ) {
                    Text("Another Account", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
            )
        }
    }
}

@Composable
private fun BeneficiaryListItem(
    beneficiary: TransferBeneficiary,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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
                .clickable(onClick = onSelect)
                .padding(horizontal = 12.dp, vertical = 12.dp),
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
                    text = beneficiary.name,
                    color = TapItTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = beneficiary.bankName,
                    color = TapItTextSecondary,
                    fontSize = 13.sp,
                )
                Text(
                    text = beneficiary.accountNumber,
                    color = TapItTextSecondary,
                    fontSize = 13.sp,
                )
            }

            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit beneficiary",
                    tint = TapItLinkBlue,
                    modifier = Modifier.size(22.dp),
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(40.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete beneficiary",
                    tint = TapItLinkBlue,
                    modifier = Modifier.size(22.dp),
                )
            }
        }
    }
}
