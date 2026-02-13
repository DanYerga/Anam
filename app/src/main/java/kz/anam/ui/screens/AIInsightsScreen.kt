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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kz.anam.data.repository.FoodTrigger
import kz.anam.data.repository.RiskAssessment
import kz.anam.data.repository.RiskLevel
import kz.anam.ui.theme.*
import kz.anam.viewmodels.AIInsightsViewModel

/**
 * Экран AI аналитики с паттернами и рисками
 * Путь: app/src/main/java/kz/anam/ui/screens/AIInsightsScreen.kt
 */

@Composable
fun AIInsightsScreen(viewModel: AIInsightsViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(PureWhite)) {
        Column(modifier = Modifier.fillMaxSize()) {
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🧠", style = MaterialTheme.typography.displayMedium)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                "AI Analytics",
                                style = MaterialTheme.typography.displaySmall,
                                color = PureWhite
                            )
                            Text(
                                "Patterns and risks",
                                style = MaterialTheme.typography.bodyMedium,
                                color = PureWhite.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RoyalViolet)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Risk Assessment Card
                    uiState.riskAssessment?.let { risk ->
                        item {
                            RiskCard(risk)
                        }
                    }

                    // Food Triggers
                    if (uiState.triggers.isNotEmpty()) {
                        item {
                            Text(
                                "TRIGGERS IDENTIFICATED",
                                style = MaterialTheme.typography.labelLarge,
                                color = TextSecondary
                            )
                        }

                        items(uiState.triggers) { trigger ->
                            TriggerCard(trigger)
                        }

                        // AI Explanation Button
                        item {
                            Button(
                                onClick = { viewModel.getAIExplanation() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricViolet),
                                shape = RoundedCornerShape(20.dp),
                                enabled = !uiState.isLoadingExplanation
                            ) {
                                if (uiState.isLoadingExplanation) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = PureWhite,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.Default.Psychology, null)
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (uiState.isLoadingExplanation) "AI analyzes..."
                                    else "Ask AI about triggers"
                                )
                            }
                        }
                    } else {
                        item {
                            EmptyInsightsState()
                        }
                    }
                }
            }
        }

        // AI Explanation Dialog
        uiState.aiExplanation?.let { explanation ->
            AIExplanationDialog(
                explanation = explanation,
                onDismiss = { viewModel.dismissExplanation() }
            )
        }
    }
}

@Composable
fun RiskCard(risk: RiskAssessment) {
    val (bgColor, iconColor, textColor) = when (risk.level) {
        RiskLevel.LOW -> Triple(Success.copy(alpha = 0.1f), Success, Success)
        RiskLevel.MEDIUM -> Triple(Warning.copy(alpha = 0.1f), Warning, Warning)
        RiskLevel.HIGH -> Triple(Error.copy(alpha = 0.15f), Error, Error)
        RiskLevel.CRITICAL -> Triple(Error.copy(alpha = 0.2f), Error, Error)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        when (risk.level) {
                            RiskLevel.LOW -> Icons.Default.CheckCircle
                            RiskLevel.MEDIUM -> Icons.Default.Warning
                            RiskLevel.HIGH, RiskLevel.CRITICAL -> Icons.Default.Error
                        },
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = when (risk.level) {
                                RiskLevel.LOW -> "Low risk"
                                RiskLevel.MEDIUM -> "Medium risk"
                                RiskLevel.HIGH -> "High risk"
                                RiskLevel.CRITICAL -> "Critical!"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            color = textColor
                        )
                        Text(
                            "Level: ${risk.score}/100",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
            }

            if (risk.reasons.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Reasons:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                risk.reasons.forEach { reason ->
                    Text(
                        "• $reason",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }
            }

            if (risk.recommendations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Recommendations:",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                risk.recommendations.forEach { recommendation ->
                    Text(
                        "→ $recommendation",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun TriggerCard(trigger: FoodTrigger) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
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
                    trigger.foodName.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "→ ${trigger.symptomType}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "${trigger.occurrences} случаев",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextTertiary
                )
            }

            // Correlation badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        when {
                            trigger.correlation >= 0.8 -> Error.copy(alpha = 0.2f)
                            trigger.correlation >= 0.6 -> Warning.copy(alpha = 0.2f)
                            else -> ElectricViolet.copy(alpha = 0.2f)
                        }
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    "${(trigger.correlation * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    color = when {
                        trigger.correlation >= 0.8 -> Error
                        trigger.correlation >= 0.6 -> Warning
                        else -> ElectricViolet
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyInsightsState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📊", style = MaterialTheme.typography.displayLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Still no data yet, if you added something it will appear later",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Proceed writing notes",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

@Composable
fun AIExplanationDialog(explanation: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("🤖", style = MaterialTheme.typography.displaySmall)
                Spacer(modifier = Modifier.width(12.dp))
                Text("AI Reasoning")
            }
        },
        text = {
            Text(
                explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = TextPrimary
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = RoyalViolet)
            ) {
                Text("Okay")
            }
        }
    )
}