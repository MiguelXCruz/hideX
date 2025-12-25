package com.hidex.app.ui.screens

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.hidex.app.data.database.SecureStorageManager
import com.hidex.app.ui.theme.HideXTheme

/**
 * Full-screen password overlay activity that appears over protected apps.
 *
 * Features:
 * - Prevents back button from dismissing (security)
 * - Shows password input field
 * - Optional biometric authentication
 * - Blocks access until correct password is entered
 */
class PasswordOverlayActivity : AppCompatActivity() {

    private lateinit var secureStorage: SecureStorageManager
    private var packageName: String? = null

    companion object {
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        secureStorage = SecureStorageManager.getInstance(this)
        packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME)

        // Prevent dismissing with back button
        setFinishOnTouchOutside(false)

        setContent {
            HideXTheme {
                PasswordOverlayScreen(
                    secureStorage = secureStorage,
                    onPasswordCorrect = {
                        finish()
                    },
                    onCancel = {
                        // Return to home instead of allowing access
                        val homeIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                            addCategory(android.content.Intent.CATEGORY_HOME)
                            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        startActivity(homeIntent)
                        finish()
                    }
                )
            }
        }
    }

    override fun onBackPressed() {
        // Prevent back button from dismissing the overlay
        // Instead, return to home screen
        val homeIntent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
            addCategory(android.content.Intent.CATEGORY_HOME)
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(homeIntent)
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordOverlayScreen(
    secureStorage: SecureStorageManager,
    onPasswordCorrect: () -> Unit,
    onCancel: () -> Unit
) {
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val isBiometricEnabled = remember { secureStorage.isBiometricEnabled() }
    val isBiometricAvailable = remember {
        val biometricManager = BiometricManager.from(context)
        biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) ==
                BiometricManager.BIOMETRIC_SUCCESS
    }

    // Setup biometric prompt - properly cast context to FragmentActivity
    val biometricPrompt = remember {
        val activity = context as? FragmentActivity
            ?: throw IllegalStateException("Context must be a FragmentActivity")

        BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(context),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onPasswordCorrect()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    errorMessage = errString.toString()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    errorMessage = "Authentication failed"
                }
            }
        )
    }

    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Unlock App")
            .setSubtitle("Use your fingerprint to unlock")
            .setNegativeButtonText("Use Password")
            .build()
    }

    // Show biometric prompt on launch if enabled
    LaunchedEffect(Unit) {
        if (isBiometricEnabled && isBiometricAvailable) {
            biometricPrompt.authenticate(promptInfo)
        }
    }

    fun verifyPassword() {
        if (password.isEmpty()) {
            errorMessage = "Please enter password"
            return
        }

        if (secureStorage.verifyPassword(password)) {
            onPasswordCorrect()
        } else {
            errorMessage = "Incorrect password"
            password = ""
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Lock icon or app name
                Icon(
                    imageVector = Icons.Default.Fingerprint,
                    contentDescription = "Lock",
                    modifier = Modifier.size(80.dp),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "App Locked",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter your password to continue",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = null
                    },
                    label = { Text("Password", color = Color.White.copy(alpha = 0.7f)) },
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
                                contentDescription = if (passwordVisible) "Hide" else "Show",
                                tint = Color.White
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { verifyPassword() }
                    ),
                    singleLine = true,
                    isError = errorMessage != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.White,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        cursorColor = Color.White,
                        errorBorderColor = Color.Red,
                        errorTextColor = Color.White
                    )
                )

                // Error message
                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Unlock button
                Button(
                    onClick = { verifyPassword() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("Unlock", style = MaterialTheme.typography.titleMedium)
                }

                // Biometric button
                if (isBiometricEnabled && isBiometricAvailable) {
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedButton(
                        onClick = {
                            biometricPrompt.authenticate(promptInfo)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Fingerprint",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Use Biometric")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel button
                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White.copy(alpha = 0.7f)
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
