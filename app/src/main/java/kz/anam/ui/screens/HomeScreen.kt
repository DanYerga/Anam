package kz.anam.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kz.anam.ui.theme.*
import kz.anam.viewmodels.HomeViewModel

/**
 * 🎨 СОВРЕМЕННЫЙ ДИЗАЙН HomeScreen
 *
 * Особенности:
 * - Нежные фиолетовые градиенты
 * - Glassmorphism эффекты
 * - Плавные тени и закругления
 * - Современные анимации
 * - Глубина и объём
 */

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToFoodLog: () -> Unit,
    onNavigateToSymptomLog: () -> Unit,
    onNavigateToMealPlan: () -> Unit,
    onNavigateToAIInsights: () -> Unit,
    onNavigateToDocuments: () -> Unit,
    onNavigateToMedia: () -> Unit
) {
    val recentFood by viewModel.recentFood.collectAsState()
    val recentSymptoms by viewModel.recentSymptoms.collectAsState()
    val todayStats by viewModel.todayStats.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        GradientPurpleStart,
                        OffWhite,
                        PureWhite
                    )
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            item { ModernHeroSection(pregnancyWeek = 24) }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                ModernStatsSection(
                    foodCount = todayStats.foodCount,
                    symptomCount = todayStats.symptomCount,
                    waterGlasses = todayStats.waterGlasses
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                ModernQuickActions(
                    onFoodClick = onNavigateToFoodLog,
                    onSymptomClick = onNavigateToSymptomLog,
                    onMealPlanClick = onNavigateToMealPlan,
                    onAIInsightsClick = onNavigateToAIInsights,
                    onDocumentsClick = onNavigateToDocuments,
                    onMediaClick = onNavigateToMedia
                )
            }
        }
    }
}

@Composable
fun ModernHeroSection(pregnancyWeek: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Градиентный фон
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            GradientPurpleStart,
                            GradientPurpleEnd.copy(alpha = 0.7f)
                        )
                    )
                )
        )

        // Декоративные круги на фоне
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-50).dp, y = 50.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            MediumPurple.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-30).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SoftPeach.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        // Контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "Hello! 💜",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Normal
                ),
                color = DarkPurple.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "$pregnancyWeek",
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = DeepPurple
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    Text(
                        "week",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = RichPurple
                    )
                    Text(
                        "of pregnancy",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MediumPurple
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Your baby's size is watermelon! 🍉",
                style = MaterialTheme.typography.bodyLarge,
                color = DarkPurple.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun ModernStatsSection(foodCount: Int, symptomCount: Int, waterGlasses: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            "Today",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GlassStatCard(
                icon = Icons.Default.Restaurant,
                label = "Meals",
                value = foodCount.toString(),
                gradientColors = listOf(GradientPurpleStart, MediumPurple),
                modifier = Modifier.weight(1f)
            )

            GlassStatCard(
                icon = Icons.Default.Favorite,
                label = "Symptoms",
                value = symptomCount.toString(),
                gradientColors = listOf(PeachPink.copy(alpha = 0.3f), LightCoral),
                modifier = Modifier.weight(1f)
            )

            GlassStatCard(
                icon = Icons.Default.WaterDrop,
                label = "Water",
                value = "$waterGlasses/8",
                gradientColors = listOf(MintGreen.copy(alpha = 0.4f), MintGreen),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun GlassStatCard(
    icon: ImageVector,
    label: String,
    value: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(140.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = gradientColors.last().copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.9f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.8f),
                            Color.White.copy(alpha = 0.2f)
                        )
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    brush = Brush.verticalGradient(gradientColors),
                    alpha = 0.15f
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = gradientColors.last(),
                    modifier = Modifier.size(32.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = gradientColors.last()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    label,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ModernQuickActions(
    onFoodClick: () -> Unit,
    onSymptomClick: () -> Unit,
    onMealPlanClick: () -> Unit,
    onAIInsightsClick: () -> Unit,
    onDocumentsClick: () -> Unit,
    onMediaClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            "Quick actions",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Первая строка - большие карточки
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GradientActionCard(
                icon = Icons.Default.Restaurant,
                title = "Nutrition",
                subtitle = "Add a meal",
                gradientColors = listOf(GradientPurpleStart, MediumPurple),
                onClick = onFoodClick,
                modifier = Modifier.weight(1f)
            )

            GradientActionCard(
                icon = Icons.Default.Favorite,
                title = "Symptoms",
                subtitle = "Add how you feel",
                gradientColors = listOf(PeachPink.copy(alpha = 0.6f), LightCoral),
                onClick = onSymptomClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Вторая строка
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GradientActionCard(
                icon = Icons.Default.AutoAwesome,
                title = "AI Menu",
                subtitle = "Personal recipes",
                gradientColors = listOf(MintGreen.copy(alpha = 0.6f), MintGreen),
                onClick = onMealPlanClick,
                modifier = Modifier.weight(1f)
            )

            GradientActionCard(
                icon = Icons.Default.Psychology,
                title = "Analytics",
                subtitle = "Smart recommendations",
                gradientColors = listOf(SoftPeach.copy(alpha = 0.6f), SoftPeach),
                onClick = onAIInsightsClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Третья строка
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            GradientActionCard(
                icon = Icons.Default.Description,
                title = "Documents",
                subtitle = "Tests and medical reports",
                gradientColors = listOf(LightPurple.copy(alpha = 0.5f), MediumPurple),
                onClick = onDocumentsClick,
                modifier = Modifier.weight(1f)
            )

            GradientActionCard(
                icon = Icons.Default.MusicNote,
                title = "Media",
                subtitle = "Music for baby",
                gradientColors = listOf(GradientPeachStart, PeachPink),
                onClick = onMediaClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun GradientActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(120.dp)
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = gradientColors.last().copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(20.dp),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(gradientColors)
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )

                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                }
            }
        }
    }
}