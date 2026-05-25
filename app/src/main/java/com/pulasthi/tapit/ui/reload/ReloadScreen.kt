package com.pulasthi.tapit.ui.reload

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pulasthi.tapit.ui.reload.components.ReloadOperatorLogo
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItError
import com.pulasthi.tapit.ui.theme.TapItLinkBlue
import com.pulasthi.tapit.ui.theme.TapItReloadPanelBlue
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.ReloadAmountOption
import com.pulasthi.tapit.viewmodel.ReloadOperator
import com.pulasthi.tapit.viewmodel.ReloadType
import com.pulasthi.tapit.viewmodel.ReloadViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun ReloadScreen(
    onBack: () -> Unit,
    onNavigateToPaymentForm: () -> Unit,
    viewModel: ReloadViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val selectedOperator = uiState.selectedOperator

    LaunchedEffect(Unit) {
        viewModel.navigateToPaymentForm.collect { onNavigateToPaymentForm() }
    }

    ReloadFlowScaffold(
        title = "Reload",
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 24.dp),
        ) {
            // Keep pager outside verticalScroll so horizontal swipes are not stolen.
            OperatorSelectorPager(
                operators = uiState.operators,
                selectedOperatorPage = uiState.selectedOperatorPage,
                selectedOperatorIndex = uiState.selectedOperatorIndex,
                pageCount = uiState.operatorPageCount,
                selectedOperatorName = selectedOperator.name,
                onOperatorPageChanged = viewModel::onOperatorPageChanged,
                onOperatorSelected = viewModel::onOperatorSelected,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                ActiveOperatorContextRow(
                    operatorName = selectedOperator.name,
                    prefixes = selectedOperator.prefixes,
                    accentColor = selectedOperator.brand.primaryColor,
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    OutlinedTextField(
                        value = uiState.mobileNumber,
                        onValueChange = viewModel::onMobileNumberChange,
                        placeholder = {
                            Text("Mobile No", color = TapItTextSecondary)
                        },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = selectedOperator.brand.primaryColor,
                            unfocusedBorderColor = TapItTextSecondary.copy(alpha = 0.4f),
                            focusedTextColor = TapItTextPrimary,
                            unfocusedTextColor = TapItTextPrimary,
                        ),
                    )
                    IconButton(onClick = { /* Contacts picker hook */ }) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Select contact",
                            tint = selectedOperator.brand.primaryColor,
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }
                uiState.mobileError?.let { FormError(it) }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ReloadTypeChip(
                        label = "Data",
                        selected = uiState.reloadType == ReloadType.DATA,
                        accentColor = selectedOperator.brand.primaryColor,
                        onClick = { viewModel.onReloadTypeSelected(ReloadType.DATA) },
                        modifier = Modifier.weight(1f),
                    )
                    ReloadTypeChip(
                        label = "Voice",
                        selected = uiState.reloadType == ReloadType.VOICE,
                        accentColor = selectedOperator.brand.primaryColor,
                        onClick = { viewModel.onReloadTypeSelected(ReloadType.VOICE) },
                        modifier = Modifier.weight(1f),
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = TapItReloadPanelBlue,
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        AmountTile(
                            label = "Custom\nAmount",
                            selected = uiState.amountOption == ReloadAmountOption.CUSTOM,
                            accentColor = selectedOperator.brand.primaryColor,
                            onClick = { viewModel.onAmountOptionSelected(ReloadAmountOption.CUSTOM) },
                            modifier = Modifier.weight(1f),
                        )
                        AmountTile(
                            label = "Rs\n50",
                            selected = uiState.amountOption == ReloadAmountOption.RS_50,
                            accentColor = selectedOperator.brand.primaryColor,
                            onClick = { viewModel.onAmountOptionSelected(ReloadAmountOption.RS_50) },
                            modifier = Modifier.weight(1f),
                        )
                        AmountTile(
                            label = "Rs\n100",
                            selected = uiState.amountOption == ReloadAmountOption.RS_100,
                            accentColor = selectedOperator.brand.primaryColor,
                            onClick = { viewModel.onAmountOptionSelected(ReloadAmountOption.RS_100) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                if (uiState.amountOption == ReloadAmountOption.CUSTOM) {
                    OutlinedTextField(
                        value = uiState.customAmount,
                        onValueChange = viewModel::onCustomAmountChange,
                        placeholder = { Text("Enter amount (LKR)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = selectedOperator.brand.primaryColor,
                            unfocusedBorderColor = TapItTextSecondary.copy(alpha = 0.4f),
                        ),
                    )
                }
                uiState.amountError?.let { FormError(it) }

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = viewModel::onHomeNext,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = selectedOperator.brand.primaryColor,
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
private fun ActiveOperatorContextRow(
    operatorName: String,
    prefixes: List<String>,
    accentColor: Color,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accentColor.copy(alpha = 0.12f))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(accentColor),
        )
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = "Active operator: $operatorName",
                color = TapItTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = "Valid prefixes: ${prefixes.joinToString(", ")}",
                color = TapItTextSecondary,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}

@Composable
private fun OperatorSelectorPager(
    operators: List<ReloadOperator>,
    selectedOperatorPage: Int,
    selectedOperatorIndex: Int,
    pageCount: Int,
    selectedOperatorName: String,
    onOperatorPageChanged: (Int) -> Unit,
    onOperatorSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val operatorsPerPage = ReloadViewModel.OPERATORS_PER_PAGE
    val safePageCount = pageCount.coerceAtLeast(1)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = selectedOperatorPage.coerceIn(0, safePageCount - 1),
        pageCount = { safePageCount },
    )

    // Swipe / fling: push page changes into ViewModel as the pager moves.
    LaunchedEffect(pagerState, safePageCount) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page ->
                onOperatorPageChanged(page)
            }
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = TapItReloadPanelBlue,
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                beyondViewportPageCount = 1,
                userScrollEnabled = true,
            ) { page ->
                val startIndex = page * operatorsPerPage
                val pageOperators = operators.drop(startIndex).take(operatorsPerPage)
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    pageOperators.forEachIndexed { indexOnPage, operator ->
                        val globalIndex = startIndex + indexOnPage
                        OperatorPagerItem(
                            operator = operator,
                            selected = globalIndex == selectedOperatorIndex,
                            onClick = { onOperatorSelected(globalIndex) },
                        )
                    }
                    if (pageOperators.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                repeat(safePageCount) { page ->
                    val active = page == selectedOperatorPage
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(page)
                                    onOperatorPageChanged(page)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(if (active) 9.dp else 7.dp)
                                .clip(CircleShape)
                                .background(
                                    if (active) TapItTextPrimary else Color.White.copy(alpha = 0.55f),
                                ),
                        )
                    }
                }
            }

            Text(
                text = selectedOperatorName,
                color = TapItTextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun OperatorPagerItem(
    operator: ReloadOperator,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .background(
                color = if (selected) {
                    operator.brand.primaryColor.copy(alpha = 0.2f)
                } else {
                    Color.Transparent
                },
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        ReloadOperatorLogo(operator = operator, size = 72.dp)
        Text(
            text = operator.name,
            color = TapItTextPrimary,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(top = 6.dp),
        )
    }
}

@Composable
private fun ReloadTypeChip(
    label: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(44.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) accentColor.copy(alpha = 0.22f) else TapItReloadPanelBlue,
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(2.dp, accentColor)
        } else {
            null
        },
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = label,
                color = if (selected) accentColor else TapItTextPrimary,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 15.sp,
            )
        }
    }
}

@Composable
private fun AmountTile(
    label: String,
    selected: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(72.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) accentColor.copy(alpha = 0.15f) else TapItWhite,
        border = if (selected) {
            androidx.compose.foundation.BorderStroke(2.dp, accentColor)
        } else {
            null
        },
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(
                text = label,
                color = if (selected) accentColor else TapItTextPrimary,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                lineHeight = 16.sp,
            )
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
