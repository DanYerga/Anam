package kz.anam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kz.anam.data.models.SymptomEntry
import kz.anam.data.models.SymptomSeverity
import kz.anam.data.models.SymptomType
import kz.anam.data.models.displayName
import kz.anam.ui.theme.*
import kz.anam.viewmodels.SymptomLogViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SymptomLogScreen(viewModel: SymptomLogViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(DeepEggplant, NightPlum)
                        )
                    )
                    .padding(horizontal = 28.dp, vertical = 32.dp)
            ) {
                Column {
                    Text(
                        "Tracker",
                        style = MaterialTheme.typography.displaySmall,
                        color = PureWhite
                    )
                    Text(
                        "of symptoms",
                        style = MaterialTheme.typography.displaySmall,
                        color = ElectricViolet
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Track how you feel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PureWhite.copy(alpha = 0.7f)
                    )
                }
            }

            // Today's entries
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "TODAY",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (uiState.todayEntries.isEmpty()) {
                    item {
                        EmptySymptomCard()
                    }
                } else {
                    items(uiState.todayEntries) { entry ->
                        SymptomEntryCard(
                            entry = entry,
                            onDelete = { viewModel.deleteEntry(entry) }
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(28.dp),
            containerColor = RoyalViolet,
            contentColor = PureWhite
        ) {
            Icon(Icons.Default.Add, contentDescription = "Добавить", modifier = Modifier.size(28.dp))
        }
    }

    // Add dialog
    if (showAddDialog) {
        AddSymptomDialog(
            onDismiss = { showAddDialog = false },
            onSave = { type, severity, notes ->
                viewModel.addSymptomEntry(type, severity, notes = notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SymptomEntryCard(entry: SymptomEntry, onDelete: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(LightSurface)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    entry.symptomType.displayName(),
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        entry.severity.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = when (entry.severity) {
                            SymptomSeverity.CRITICAL, SymptomSeverity.VERY_SEVERE -> Error
                            SymptomSeverity.SEVERE -> Warning
                            else -> ElectricViolet
                        }
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(TextTertiary)
                    )
                    Text(
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(entry.timestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
                if (entry.notes.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        entry.notes,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Удалить",
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptySymptomCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(LightSurface)
            .padding(48.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "💚",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No notes",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Text(
                "Excellent!Write if you feeling bad",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
fun AddSymptomDialog(
    onDismiss: () -> Unit,
    onSave: (SymptomType, SymptomSeverity, String) -> Unit
) {
    var selectedType by remember { mutableStateOf(SymptomType.NAUSEA) }
    var selectedSeverity by remember { mutableStateOf(SymptomSeverity.MODERATE) }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add a symptom", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Symptom type selector
                Column {
                    Text("Symptom", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))

                    LazyColumn(
                        modifier = Modifier.height(200.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(SymptomType.values().toList()) { type ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedType = type }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selectedType == type,
                                    onClick = { selectedType = type },
                                    colors = RadioButtonDefaults.colors(selectedColor = RoyalViolet)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(type.displayName())
                            }
                        }
                    }
                }

                // Severity selector
                Column {
                    Text("How harsh is it?", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    SymptomSeverity.values().forEach { severity ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedSeverity = severity }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedSeverity == severity,
                                onClick = { selectedSeverity = severity },
                                colors = RadioButtonDefaults.colors(selectedColor = RoyalViolet)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(severity.label)
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalViolet,
                        focusedLabelColor = RoyalViolet
                    ),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(selectedType, selectedSeverity, notes) },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}