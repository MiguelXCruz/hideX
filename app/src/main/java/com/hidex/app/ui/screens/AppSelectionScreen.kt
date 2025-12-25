package com.hidex.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.hidex.app.data.model.InstalledApp
import com.hidex.app.ui.viewmodels.AppSelectionUiState
import com.hidex.app.ui.viewmodels.AppSelectionViewModel

/**
 * Screen for selecting apps to protect.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSelectionScreen(
    viewModel: AppSelectionViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedApps by viewModel.selectedApps.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Apps to Protect") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState is AppSelectionUiState.Success) {
                        IconButton(
                            onClick = {
                                viewModel.saveSelection(onNavigateBack)
                            }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Save")
                        }
                    }
                }
            )
        }
    ) { padding ->
        when (val state = uiState) {
            is AppSelectionUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading apps...")
                    }
                }
            }
            
            is AppSelectionUiState.Success -> {
                if (state.apps.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No apps found",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.apps, key = { it.packageName }) { app ->
                            AppSelectionItem(
                                app = app,
                                isSelected = app.packageName in selectedApps,
                                onToggle = { viewModel.toggleAppSelection(app.packageName) }
                            )
                        }
                    }
                }
            }
            
            is AppSelectionUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Text(
                            text = "Error: ${state.message}",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.loadInstalledApps() }) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppSelectionItem(
    app: InstalledApp,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onToggle() }
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // App icon
            if (app.icon != null) {
                Image(
                    bitmap = app.icon.toBitmap(width = 48, height = 48).asImageBitmap(),
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
            
            // App name and package
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.appName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = app.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}
