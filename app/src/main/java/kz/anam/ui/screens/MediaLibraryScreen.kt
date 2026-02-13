package kz.anam.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kz.anam.data.models.MediaContent
import kz.anam.data.models.MediaType
import kz.anam.ui.theme.*
import kz.anam.viewmodels.MediaLibraryViewModel

/**
 * Экран медиа библиотеки (музыка, видео, подкасты)
 * Путь: app/src/main/java/kz/anam/ui/screens/MediaLibraryScreen.kt
 */

@Composable
fun MediaLibraryScreen(viewModel: MediaLibraryViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(ElectricViolet, RoyalViolet)
                    )
                )
                .padding(horizontal = 28.dp, vertical = 32.dp)
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🎵", style = MaterialTheme.typography.displayMedium)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            "Media",
                            style = MaterialTheme.typography.displaySmall,
                            color = PureWhite
                        )
                        Text(
                            "For you and your baby",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PureWhite.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Filter chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 28.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.selectedType == null,
                    onClick = { viewModel.filterByType(null) },
                    label = { Text("Все") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalViolet,
                        selectedLabelColor = PureWhite
                    )
                )
            }
            item {
                FilterChip(
                    selected = uiState.selectedType == MediaType.MUSIC,
                    onClick = { viewModel.filterByType(MediaType.MUSIC) },
                    label = { Text("🎵 Music") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalViolet,
                        selectedLabelColor = PureWhite
                    )
                )
            }
            item {
                FilterChip(
                    selected = uiState.selectedType == MediaType.MEDITATION,
                    onClick = { viewModel.filterByType(MediaType.MEDITATION) },
                    label = { Text("🧘 Meditation") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalViolet,
                        selectedLabelColor = PureWhite
                    )
                )
            }
            item {
                FilterChip(
                    selected = uiState.selectedType == MediaType.VIDEO,
                    onClick = { viewModel.filterByType(MediaType.VIDEO) },
                    label = { Text("📹 Video") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalViolet,
                        selectedLabelColor = PureWhite
                    )
                )
            }
            item {
                FilterChip(
                    selected = uiState.selectedType == MediaType.PODCAST,
                    onClick = { viewModel.filterByType(MediaType.PODCAST) },
                    label = { Text("🎙️ Podcasts") },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = RoyalViolet,
                        selectedLabelColor = PureWhite
                    )
                )
            }
        }

        // Content list
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 28.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(uiState.filteredContent) { content ->
                MediaContentCard(
                    content = content,
                    onClick = {
                        // Открываем в YouTube/браузере
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(content.contentUrl))
                        context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun MediaContentCard(
    content: MediaContent,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(LightSurface)
            .clickable(onClick = onClick)
    ) {
        Column {
            // Thumbnail
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    .background(DeepEggplant.copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = content.thumbnailUrl,
                    contentDescription = content.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Play button overlay
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = RoundedCornerShape(32.dp),
                        color = RoyalViolet.copy(alpha = 0.9f)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = PureWhite,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Duration badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(12.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(TextPrimary.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        content.duration,
                        style = MaterialTheme.typography.labelSmall,
                        color = PureWhite
                    )
                }
            }

            // Content info
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        when (content.type) {
                            MediaType.MUSIC -> "🎵"
                            MediaType.MEDITATION -> "🧘"
                            MediaType.VIDEO -> "📹"
                            MediaType.PODCAST -> "🎙️"
                        },
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    content.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    content.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    maxLines = 2
                )

                // Tags
                if (content.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(content.tags) { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElectricViolet.copy(alpha = 0.1f))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    tag,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = ElectricViolet
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}