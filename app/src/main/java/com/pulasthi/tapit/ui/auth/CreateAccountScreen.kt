package com.pulasthi.tapit.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pulasthi.tapit.ui.components.AuthFormCard
import com.pulasthi.tapit.ui.components.AuthTopSection
import com.pulasthi.tapit.ui.components.ColumnLabel
import com.pulasthi.tapit.ui.components.PhoneNumberField
import com.pulasthi.tapit.ui.components.TapItGradientBackground
import com.pulasthi.tapit.ui.components.TapItTextField
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItDisabledButton
import com.pulasthi.tapit.ui.theme.TapItInputBackground
import com.pulasthi.tapit.ui.theme.TapItLinkBlue
import com.pulasthi.tapit.ui.theme.TapItTextPrimary
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.CreateAccountViewModel

@Composable
fun CreateAccountScreen(
    onNavigateToCreatePin: () -> Unit,
    onPrivacyPolicyClick: () -> Unit = {},
    onTermsClick: () -> Unit = {},
    viewModel: CreateAccountViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToCreatePin.collect {
            onNavigateToCreatePin()
        }
    }

    TapItGradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            AuthTopSection(title = "Create Account")
            AuthFormCard(modifier = Modifier.weight(1f)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = "Enter your mobile number and email",
                        color = TapItTextSecondary,
                        fontSize = 15.sp,
                        modifier = Modifier.padding(bottom = 20.dp),
                    )

                    PhoneNumberField(
                        countryCode = uiState.countryCode,
                        mobileNumber = uiState.mobileNumber,
                        onMobileNumberChange = viewModel::onMobileNumberChange,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    ColumnLabel(text = "E-Mail Address")
                    TapItTextField(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        placeholder = "example@domain.com",
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AgreementCheckboxRow(
                        checked = uiState.privacyPolicyAccepted,
                        onCheckedChange = viewModel::onPrivacyPolicyToggle,
                        prefix = "I have agreed to the ",
                        linkText = "privacy policy.",
                        onLinkClick = onPrivacyPolicyClick,
                    )

                    AgreementCheckboxRow(
                        checked = uiState.termsAccepted,
                        onCheckedChange = viewModel::onTermsToggle,
                        prefix = "I have agreed to the ",
                        linkText = "terms and conditions.",
                        onLinkClick = onTermsClick,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = viewModel::onDoneClick,
                        enabled = uiState.isDoneEnabled && !uiState.isLoading,
                        modifier = Modifier.height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (uiState.isDoneEnabled) {
                                TapItBluePrimary
                            } else {
                                TapItDisabledButton
                            },
                            contentColor = TapItWhite,
                            disabledContainerColor = TapItDisabledButton,
                            disabledContentColor = TapItWhite,
                        ),
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(
                                color = TapItWhite,
                                modifier = Modifier.height(20.dp),
                            )
                        } else {
                            Text(text = "Done", fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgreementCheckboxRow(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    prefix: String,
    linkText: String,
    onLinkClick: () -> Unit,
) {
    val label = buildAnnotatedString {
        withStyle(SpanStyle(color = TapItTextPrimary)) {
            append(prefix)
        }
        withLink(
            LinkAnnotation.Clickable(
                tag = linkText,
                styles = TextLinkStyles(style = SpanStyle(color = TapItLinkBlue)),
            ) {
                onLinkClick()
            },
        ) {
            append(linkText)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = TapItBluePrimary,
                uncheckedColor = TapItInputBackground,
            ),
        )
        Text(
            text = label,
            fontSize = 13.sp,
            modifier = Modifier.padding(end = 8.dp),
        )
    }
}
