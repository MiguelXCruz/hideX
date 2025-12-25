package com.hidex.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hidex.app.ui.viewmodels.FirstLaunchUiState
import com.hidex.app.ui.viewmodels.FirstLaunchViewModel

/**
 * First launch screen for password setup.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FirstLaunchScreen(
    viewModel: FirstLaunchViewModel,
    onPasswordCreated: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    
    val uiState by viewModel.uiState.collectAsState()
    
    // Navigate on success
    LaunchedEffect(uiState) {
        if (uiState is FirstLaunchUiState.Success) {
            onPasswordCreated()
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Welcome to hideX") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Create Your Password",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Set up a password to protect your apps",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (uiState is FirstLaunchUiState.Error) {
                        viewModel.clearError()
                    }
                },
                label = { Text("Password") },
                visualTransformation = if (passwordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) 
                                Icons.Default.Visibility 
                            else 
                                Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide" else "Show"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState is FirstLaunchUiState.Error
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Confirm password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    if (uiState is FirstLaunchUiState.Error) {
                        viewModel.clearError()
                    }
                },
                label = { Text("Confirm Password") },
                visualTransformation = if (confirmPasswordVisible) 
                    VisualTransformation.None 
                else 
                    PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) 
                                Icons.Default.Visibility 
                            else 
                                Icons.Default.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide" else "Show"
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        viewModel.createPassword(password, confirmPassword)
                    }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = uiState is FirstLaunchUiState.Error
            )
            
            // Error message
            if (uiState is FirstLaunchUiState.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = (uiState as FirstLaunchUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Create password button
            Button(
                onClick = {
                    viewModel.createPassword(password, confirmPassword)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = password.isNotEmpty() && confirmPassword.isNotEmpty()
            ) {
                Text(
                    text = "Create Password",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Your password will be used to unlock protected apps",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
