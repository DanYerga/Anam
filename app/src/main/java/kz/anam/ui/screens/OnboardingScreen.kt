package kz.anam.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kz.anam.ui.theme.*

@Composable
fun OnboardingScreen(onGetStarted: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWhite)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Premium Illustration Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.3f)
                    .clip(RoundedCornerShape(bottomStart = 48.dp, bottomEnd = 48.dp))
            ) {
                PremiumIllustration()
            }

            Spacer(modifier = Modifier.height(56.dp))

            // Brand
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 32.dp)
            ) {
                Text(
                    "Anam",
                    style = MaterialTheme.typography.displayLarge,
                    color = NightPlum
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    "Your pregnancy —",
                    style = MaterialTheme.typography.headlineMedium,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    "in caring hands",
                    style = MaterialTheme.typography.headlineMedium,
                    color = RoyalViolet,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // CTA Button
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .height(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = RoyalViolet
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp
                )
            ) {
                Text(
                    "Start",
                    style = MaterialTheme.typography.labelLarge,
                    color = PureWhite
                )
            }

            Spacer(modifier = Modifier.height(56.dp))
        }
    }
}

@Composable
fun PremiumIllustration() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = size.width
        val height = size.height

        // Deep purple gradient sky
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    NightPlum,
                    DeepEggplant,
                    RoyalViolet.copy(alpha = 0.8f),
                    ElectricViolet.copy(alpha = 0.6f)
                )
            )
        )

        // Stars - elegant dots
        val starColor = PureWhite.copy(alpha = 0.7f)
        drawCircle(starColor, radius = 5f, center = Offset(width * 0.15f, height * 0.12f))
        drawCircle(starColor, radius = 3f, center = Offset(width * 0.25f, height * 0.08f))
        drawCircle(starColor, radius = 4f, center = Offset(width * 0.7f, height * 0.15f))
        drawCircle(starColor, radius = 6f, center = Offset(width * 0.85f, height * 0.1f))
        drawCircle(starColor, radius = 3f, center = Offset(width * 0.9f, height * 0.2f))

        // Large moon
        drawCircle(
            color = Color(0xFFFFF9E8),
            radius = 72f,
            center = Offset(width * 0.2f, height * 0.15f)
        )
        // Moon glow
        drawCircle(
            color = Color(0xFFFFF9E8).copy(alpha = 0.2f),
            radius = 88f,
            center = Offset(width * 0.2f, height * 0.15f)
        )

        // Distant hills (first layer)
        val hillPath1 = Path().apply {
            moveTo(0f, height * 0.68f)
            quadraticBezierTo(
                width * 0.35f, height * 0.48f,
                width * 0.65f, height * 0.68f
            )
            quadraticBezierTo(
                width * 0.85f, height * 0.78f,
                width, height * 0.72f
            )
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(hillPath1, SoftViolet.copy(alpha = 0.4f))

        // Middle hills
        val hillPath2 = Path().apply {
            moveTo(0f, height * 0.75f)
            quadraticBezierTo(
                width * 0.4f, height * 0.62f,
                width * 0.7f, height * 0.78f
            )
            lineTo(width, height * 0.78f)
            lineTo(width, height)
            lineTo(0f, height)
            close()
        }
        drawPath(hillPath2, DeepEggplant.copy(alpha = 0.7f))

        // Foreground silhouette - elegant woman sitting
        val womanSilhouette = Path().apply {
            // Sitting pose - simplified elegant shape
            moveTo(width * 0.35f, height * 0.88f)

            // Left side body curve
            cubicTo(
                width * 0.38f, height * 0.78f,
                width * 0.43f, height * 0.73f,
                width * 0.47f, height * 0.7f
            )

            // Head (circular)
            moveTo(width * 0.5f, height * 0.66f)

            // Right side body curve
            moveTo(width * 0.5f, height * 0.7f)
            cubicTo(
                width * 0.54f, height * 0.73f,
                width * 0.59f, height * 0.78f,
                width * 0.62f, height * 0.88f
            )

            lineTo(width * 0.62f, height)
            lineTo(width * 0.35f, height)
            close()
        }

        // Draw woman body
        drawPath(
            path = Path().apply {
                moveTo(width * 0.38f, height * 0.88f)
                cubicTo(
                    width * 0.4f, height * 0.78f,
                    width * 0.44f, height * 0.74f,
                    width * 0.48f, height * 0.72f
                )
                cubicTo(
                    width * 0.52f, height * 0.74f,
                    width * 0.56f, height * 0.78f,
                    width * 0.58f, height * 0.88f
                )
                lineTo(width * 0.58f, height)
                lineTo(width * 0.38f, height)
                close()
            },
            color = NightPlum
        )

        // Head
        drawCircle(
            color = NightPlum,
            radius = 28f,
            center = Offset(width * 0.48f, height * 0.68f)
        )

        // Trees (elegant silhouettes)
        val tree1 = Path().apply {
            moveTo(width * 0.75f, height * 0.7f)
            lineTo(width * 0.7f, height * 0.88f)
            lineTo(width * 0.8f, height * 0.88f)
            close()
        }
        drawPath(tree1, NightPlum.copy(alpha = 0.8f))

        val tree2 = Path().apply {
            moveTo(width * 0.85f, height * 0.66f)
            lineTo(width * 0.78f, height * 0.88f)
            lineTo(width * 0.92f, height * 0.88f)
            close()
        }
        drawPath(tree2, NightPlum.copy(alpha = 0.8f))

        // Magical floating elements (glowing particles)
        val glowColor = Color(0xFFE8D5F5).copy(alpha = 0.8f)
        drawCircle(glowColor, radius = 10f, center = Offset(width * 0.55f, height * 0.52f))
        drawCircle(glowColor, radius = 7f, center = Offset(width * 0.62f, height * 0.56f))
        drawCircle(glowColor, radius = 8f, center = Offset(width * 0.5f, height * 0.6f))
        drawCircle(glowColor, radius = 6f, center = Offset(width * 0.58f, height * 0.48f))
        drawCircle(glowColor, radius = 9f, center = Offset(width * 0.53f, height * 0.57f))
    }
}