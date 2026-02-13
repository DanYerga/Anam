package kz.anam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kz.anam.ui.theme.*

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLogin by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Hero section
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                ElectricViolet.copy(alpha = 0.2f),
                                RoyalViolet.copy(alpha = 0.1f)
                            )
                        ),
                        shape = RoundedCornerShape(40.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "💜",
                    style = MaterialTheme.typography.displayLarge
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = if (isLogin) "Coming back?" else "Welcome!",
                style = MaterialTheme.typography.displaySmall,
                color = TextPrimary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isLogin) "Log in" else "Create the new one",
                style = MaterialTheme.typography.bodyLarge,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email", style = MaterialTheme.typography.bodyMedium) },
                placeholder = { Text("your@email.com") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Email,
                        contentDescription = null,
                        tint = RoyalViolet
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoyalViolet,
                    unfocusedBorderColor = LightBorder,
                    focusedContainerColor = LightSurface,
                    unfocusedContainerColor = LightSurface,
                    focusedLabelColor = RoyalViolet,
                    unfocusedLabelColor = TextSecondary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Password", style = MaterialTheme.typography.bodyMedium) },
                placeholder = { Text("••••••••") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = RoyalViolet
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.Visibility
                            else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide" else "Show",
                            tint = TextTertiary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                else PasswordVisualTransformation(),
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = RoyalViolet,
                    unfocusedBorderColor = LightBorder,
                    focusedContainerColor = LightSurface,
                    unfocusedContainerColor = LightSurface,
                    focusedLabelColor = RoyalViolet,
                    unfocusedLabelColor = TextSecondary
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium
            )

            if (isLogin) {
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    onClick = { /* TODO: Forgot password */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        "Forgot password?",
                        style = MaterialTheme.typography.bodySmall,
                        color = RoyalViolet
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Main CTA button
            Button(
                onClick = {
                    // TODO: Validate and authenticate
                    if (email.isNotBlank() && password.isNotBlank()) {
                        onAuthSuccess()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalViolet
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Text(
                    if (isLogin) "Log in" else "Register",
                    style = MaterialTheme.typography.labelLarge,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Toggle between login/register
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isLogin) "Do not have account?" else "Having an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { isLogin = !isLogin }) {
                    Text(
                        if (isLogin) "Sign up" else "Sign in",
                        style = MaterialTheme.typography.bodyMedium,
                        color = RoyalViolet
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}