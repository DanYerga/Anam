package kz.anam.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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

@Composable
fun SettingsScreen() {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(vertical = 24.dp, horizontal = 28.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Profile Section
        item {
            Column {
                Text(
                    text = "ПРОФИЛЬ",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                PremiumSettingsItem(
                    icon = Icons.Default.Person,
                    title = "My profile",
                    onClick = { }
                )
                Spacer(modifier = Modifier.height(12.dp))
                PremiumSettingsItem(
                    icon = Icons.Default.Edit,
                    title = "Edit my profile",
                    onClick = { }
                )
            }
        }

        // App Settings Section
        item {
            Column {
                Text(
                    text = "APP",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                PremiumSettingsSwitchItem(
                    icon = Icons.Default.Notifications,
                    title = "Notifications",
                    checked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
                PremiumSettingsSwitchItem(
                    icon = Icons.Default.DarkMode,
                    title = "Dark theme",
                    checked = darkModeEnabled,
                    onCheckedChange = { darkModeEnabled = it }
                )
            }
        }

        // Support Section
        item {
            Column {
                Text(
                    text = "SUPPORT",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(16.dp))

                PremiumSettingsItem(
                    icon = Icons.Default.Help,
                    title = "Help",
                    onClick = { }
                )
                Spacer(modifier = Modifier.height(12.dp))
                PremiumSettingsItem(
                    icon = Icons.Default.Info,
                    title = "About app",
                    onClick = { }
                )
                Spacer(modifier = Modifier.height(12.dp))
                PremiumSettingsItem(
                    icon = Icons.Default.PrivacyTip,
                    title = "Privacy policy",
                    onClick = { }
                )
            }
        }

        // Version
        item {
            Text(
                text = "Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = TextTertiary,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun PremiumSettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(LightSurface)
            .clickable(onClick = onClick)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PureWhite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = RoyalViolet,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Default.ArrowForward,
                contentDescription = null,
                tint = RoyalViolet,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun PremiumSettingsSwitchItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(LightSurface)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(PureWhite),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = RoyalViolet,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PureWhite,
                    checkedTrackColor = RoyalViolet,
                    uncheckedThumbColor = PureWhite,
                    uncheckedTrackColor = LightBorder
                )
            )
        }
    }
}