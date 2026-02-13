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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import kz.anam.ui.theme.*

data class Article(
    val title: String,
    val category: String,
    val readTime: String,
    val icon: ImageVector
)

@Composable
fun ArticlesScreen() {
    val articles = remember {
        listOf(
            Article("Nutrition it the first trimestre", "Nutrition", "5 min", Icons.Default.Restaurant),
            Article("Exercises for pregnant women", "Activity", "7 min", Icons.Default.FitnessCenter),
            Article("How to manage pregnancy nausea", "Health", "6 min", Icons.Default.Favorite),
            Article("Mental Health", "Psychology", "8 min", Icons.Default.Psychology),
            Article("Preparation for giving birth", "Giving birth", "10 min", Icons.Default.ChildCare),
            Article("Vitamins", "Health", "5 min", Icons.Default.Medication),
            Article("Sleep during pregnancy", "Health", "6 min", Icons.Default.Bedtime),
            Article("Choosing a maternity hospital", "Giving birth", "9 min", Icons.Default.LocalHospital)
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(vertical = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier.padding(horizontal = 28.dp)
            ) {
                Text(
                    text = "Helpful",
                    style = MaterialTheme.typography.displaySmall,
                    color = TextPrimary
                )
                Text(
                    text = "articles",
                    style = MaterialTheme.typography.displaySmall,
                    color = RoyalViolet
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Expert advice for every stage",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        items(articles) { article ->
            PremiumArticleCard(article)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PremiumArticleCard(article: Article) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(LightSurface)
            .clickable { /* TODO: Open article */ }
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon container
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(PureWhite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    article.icon,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = RoyalViolet
                )
            }

            // Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = article.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricViolet
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(TextTertiary)
                    )
                    Text(
                        text = article.readTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }
            }

            // Arrow
            Icon(
                Icons.Default.ArrowForward,
                contentDescription = "Read",
                tint = RoyalViolet,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}