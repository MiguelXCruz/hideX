package com.hidex.app.ui.screens

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.hidex.app.data.model.ProtectedApp
import com.hidex.app.service.AppProtectionAccessibilityService
import com.hidex.app.ui.viewmodels.MainViewModel

/**
 * Main screen showing list of protected apps.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    onNavigateToAppSelection: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val protectedApps by viewModel.protectedApps.collectAsState()
    val context = LocalContext.current
    var showPermissionDialog by remember { mutableStateOf(false) }
    
    // Check if accessibility service is enabled
    val isAccessibilityEnabled = remember(context) {
        AppProtectionAccessibilityService.isRunning
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("hideX") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAppSelection
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add app")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Accessibility service warning
            if (!isAccessibilityEnabled) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .clickable { showPermissionDialog = true },
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Service Disabled",
                                style = MaterialTheme.typography.titleSmall,
                                color = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Enable Accessibility Service to protect apps",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Enable"
                        )
                    }
                }
            }
            
            // Protected apps list
            if (protectedApps.isEmpty()) {
                EmptyState(onAddApps = onNavigateToAppSelection)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(protectedApps, key = { it.packageName }) { app ->
                        ProtectedAppItem(
                            app = app,
                            onLaunch = { viewModel.launchApp(context, app.packageName) },
                            onRemove = { viewModel.removeProtectedApp(app.packageName) }
                        )
                    }
                }
            }
        }
    }
    
    // Permission dialog
    if (showPermissionDialog) {
        AlertDialog(
            onDismissRequest = { showPermissionDialog = false },
            title = { Text("Enable Accessibility Service") },
            text = {
                Text("hideX needs Accessibility Service permission to monitor and protect your apps. Please enable 'hideX App Protection' in the Accessibility settings.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showPermissionDialog = false
                        openAccessibilitySettings(context)
                    }
                ) {
                    Text("Open Settings")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPermissionDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun EmptyState(onAddApps: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "No Protected Apps",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tap the + button to add apps to protect",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onAddApps) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Apps")
        }
    }
}

@Composable
fun ProtectedAppItem(
    app: ProtectedApp,
    onLaunch: () -> Unit,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    val packageManager = context.packageManager
    val appIcon = remember(app.packageName) {
        try {
            packageManager.getApplicationIcon(app.packageName)
        } catch (e: Exception) {
            null
        }
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onLaunch)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon
            if (appIcon != null) {
                Image(
                    bitmap = appIcon.toBitmap(width = 48, height = 48).asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                Icon(
                    Icons.Default.Android,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // App name
            Text(
                text = app.appName,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            
            // Remove button
            IconButton(onClick = onRemove) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Remove",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

private fun openAccessibilitySettings(context: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}
