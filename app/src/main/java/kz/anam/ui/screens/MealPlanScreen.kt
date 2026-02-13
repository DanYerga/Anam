package kz.anam.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kz.anam.ui.theme.*
import kz.anam.viewmodels.MealGeneratorViewModel
import kz.anam.viewmodels.MealSuggestion

@Composable
fun MealPlanScreen(viewModel: MealGeneratorViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

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
                        Brush.verticalGradient(
                            colors = listOf(RoyalViolet, DeepEggplant)
                        )
                    )
                    .padding(horizontal = 28.dp, vertical = 32.dp)
            ) {
                Column {
                    Text(
                        "Personal",
                        style = MaterialTheme.typography.displaySmall,
                        color = PureWhite
                    )
                    Text(
                        "Menu",
                        style = MaterialTheme.typography.displaySmall,
                        color = ElectricViolet
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "AI chooses safe meal for you",
                        style = MaterialTheme.typography.bodyMedium,
                        color = PureWhite.copy(alpha = 0.7f)
                    )
                }

            }

                // Content
                if (uiState.suggestions.isEmpty() && !uiState.isGenerating) {
                    // Empty state
                    EmptyMealPlanState(
                        onGenerate = { viewModel.generateMealPlan() }
                    )
                } else {
                    // Meal list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 28.dp, vertical = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (uiState.isGenerating) {
                            item {
                                GeneratingIndicator()
                            }
                        }

                        items(uiState.suggestions) { meal ->
                            MealCard(
                                meal = meal,
                                isGeneratingImage = uiState.isGeneratingImages,
                                onSave = {
                                    scope.launch {
                                        viewModel.saveMealToTracker(meal)
                                    }
                                }
                            )
                        }

                        // Regenerate button
                        if (uiState.suggestions.isNotEmpty() && !uiState.isGenerating) {
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                OutlinedButton(
                                    onClick = { viewModel.generateMealPlan() },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        contentColor = RoyalViolet
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                ) {
                                    Icon(Icons.Default.Refresh, null)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Generate again")
                                }
                            }
                        }
                    }
                }
            }

            // Image generation prompt (dialog)
            if (uiState.showImagePrompt) {
                ImagePromptDialog(
                    onYes = { viewModel.generateImages() },
                    onNo = { viewModel.skipImages() }
                )
            }
        }
    }

    @Composable
    fun EmptyMealPlanState(onGenerate: () -> Unit) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "✨",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "AI will create a personal menu",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Considering your nausea triggers and pregnancy week",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onGenerate,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AutoAwesome, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Generate a menu", style = MaterialTheme.typography.titleMedium)
            }
        }
    }

    @Composable
    fun GeneratingIndicator() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(LightSurface)
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = RoyalViolet)
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "AI is creating personal menu...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
        }
    }

    @Composable
    fun MealCard(
        meal: MealSuggestion,
        isGeneratingImage: Boolean,
        onSave: () -> Unit
    ) {
        var showDetails by remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.verticalGradient(
                        colors = when (meal.mealType) {
                            kz.anam.data.models.FoodCategory.BREAKFAST ->
                                listOf(ElectricViolet.copy(alpha = 0.1f), PureWhite)
                            kz.anam.data.models.FoodCategory.LUNCH ->
                                listOf(RoyalViolet.copy(alpha = 0.1f), PureWhite)
                            kz.anam.data.models.FoodCategory.DINNER ->
                                listOf(DeepEggplant.copy(alpha = 0.1f), PureWhite)
                            else -> listOf(LightSurface, PureWhite)
                        }
                    )
                )
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            meal.emoji,
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                meal.mealType.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = ElectricViolet
                            )
                            Text(
                                meal.dishName,
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Description
                Text(
                    meal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )

                // Expandable benefits
                AnimatedVisibility(visible = showDetails) {
                    Column {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Success.copy(alpha = 0.1f))
                                .padding(12.dp)
                        ) {
                            Row {
                                Icon(
                                    Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Success,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    meal.benefits,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = { showDetails = !showDetails }) {
                        Text(
                            if (showDetails) "Скрыть пользу" else "Почему полезно?",
                            color = RoyalViolet
                        )
                        Icon(
                            if (showDetails) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = RoyalViolet
                        )
                    }

                    Button(
                        onClick = onSave,
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Add to tracker")
                    }
                }
            }
        }
    }

    @Composable
    fun ImagePromptDialog(onYes: () -> Unit, onNo: () -> Unit) {
        AlertDialog(
            onDismissRequest = onNo,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎨", style = MaterialTheme.typography.displaySmall)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Add a photo?", style = MaterialTheme.typography.headlineSmall)
                }
            },
            text = {
                Text(
                    "AI can generate meal examples. It will activate in the future",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = onYes,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet)
                ) {
                    Icon(Icons.Default.Image, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Yes, generate")
                }
            },
            dismissButton = {
                TextButton(onClick = onNo) {
                    Text("No", color = TextSecondary)
                }
            }
        )
    }
