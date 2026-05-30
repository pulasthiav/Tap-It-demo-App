package com.pulasthi.tapit.ui.auth

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
import com.pulasthi.tapit.ui.components.TapItPasswordField
import com.pulasthi.tapit.ui.theme.TapItBluePrimary
import com.pulasthi.tapit.ui.theme.TapItLinkBlue
import com.pulasthi.tapit.ui.theme.TapItTextSecondary
import com.pulasthi.tapit.ui.theme.TapItWhite
import com.pulasthi.tapit.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToEnterPin: () -> Unit,
    onForgotPassword: () -> Unit = {},
    viewModel: LoginViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.navigateToEnterPin.collect {
            onNavigateToEnterPin()
        }
    }

    TapItGradientBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            AuthTopSection(
                title = "Login",
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(top = 32.dp),
            )
            AuthFormCard(
                modifier = Modifier.weight(1f),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                ) {
                    Text(
                        text = "Login to your Tap-It account",
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

                    ColumnLabel(text = "Password")
                    TapItPasswordField(
                        value = uiState.password,
                        onValueChange = viewModel::onPasswordChange,
                        isVisible = uiState.isPasswordVisible,
                        onToggleVisibility = viewModel::onTogglePasswordVisibility,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Button(
                            onClick = viewModel::onLoginClick,
                            enabled = uiState.isLoginEnabled && !uiState.isLoading,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = TapItBluePrimary,
                                contentColor = TapItWhite,
                            ),
                            modifier = Modifier.height(44.dp),
                        ) {
                            if (uiState.isLoading) {
                                CircularProgressIndicator(
                                    color = TapItWhite,
                                    modifier = Modifier.height(20.dp),
                                )
                            } else {
                                Text(text = "Login", fontSize = 14.sp)
                            }
                        }
                        Text(
                            text = "Forgot Password?",
                            color = TapItLinkBlue,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable(onClick = onForgotPassword),
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    RegisterFooter(
                        onRegisterClick = onNavigateToRegister,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
private fun RegisterFooter(
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val annotated = buildAnnotatedString {
        append("Don't have a Tap-It account? ")
        pushStringAnnotation(tag = "register", annotation = "register")
        withStyle(SpanStyle(color = TapItLinkBlue, fontWeight = FontWeight.Bold)) {
            append("Register Now")
        }
        pop()
    }

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Text(
            text = annotated,
            fontSize = 14.sp,
            color = TapItTextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.clickable {
                onRegisterClick()
            },
        )
    }
}
