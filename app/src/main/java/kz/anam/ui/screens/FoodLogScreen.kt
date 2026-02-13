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
import kz.anam.data.models.FoodCategory
import kz.anam.data.models.FoodEntry
import kz.anam.data.models.displayName
import kz.anam.ui.theme.*
import kz.anam.viewmodels.FoodLogViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FoodLogScreen(viewModel: FoodLogViewModel) {
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
                            colors = listOf(NightPlum, DeepEggplant)
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
                        "of nutrition",
                        style = MaterialTheme.typography.displaySmall,
                        color = ElectricViolet
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Write everything you eat",
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
                        EmptyStateCard()
                    }
                } else {
                    items(uiState.todayEntries) { entry ->
                        FoodEntryCard(
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
            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(28.dp))
        }
    }

    // Add dialog
    if (showAddDialog) {
        AddFoodDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, category, portion ->
                viewModel.addFoodEntry(name, category, portion)
                showAddDialog = false
            }
        )
    }

    // Success snackbar
    if (uiState.saveSuccess) {
        Text("Added!", color = Success)
    }
}

@Composable
fun FoodEntryCard(entry: FoodEntry, onDelete: () -> Unit) {
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
                    entry.foodName,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        entry.category.displayName(),
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricViolet
                    )
                    if (entry.portionSize.isNotEmpty()) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(TextTertiary)
                        )
                        Text(
                            entry.portionSize,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(entry.timestamp)),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TextTertiary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard() {
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
                "🍽️",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "No notes",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary
            )
            Text(
                "Add your first meal",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary
            )
        }
    }
}

@Composable
fun AddFoodDialog(
    onDismiss: () -> Unit,
    onSave: (String, FoodCategory, String) -> Unit
) {
    var foodName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(FoodCategory.BREAKFAST) }
    var portionSize by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Add meal", style = MaterialTheme.typography.headlineSmall)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text("Name") },
                    placeholder = { Text("Apple, chicken...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalViolet,
                        focusedLabelColor = RoyalViolet
                    )
                )

                // Category selector
                Column {
                    Text("Category", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    FoodCategory.values().forEach { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedCategory = category }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                colors = RadioButtonDefaults.colors(selectedColor = RoyalViolet)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(category.displayName())
                        }
                    }
                }

                OutlinedTextField(
                    value = portionSize,
                    onValueChange = { portionSize = it },
                    label = { Text("Portion (optional)") },
                    placeholder = { Text("200г, 1 шт...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = RoyalViolet,
                        focusedLabelColor = RoyalViolet
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (foodName.isNotBlank()) {
                        onSave(foodName, selectedCategory, portionSize)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet),
                enabled = foodName.isNotBlank()
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