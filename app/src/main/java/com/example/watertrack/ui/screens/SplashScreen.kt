package com.example.watertrack.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

private val SplashNavy = Color(0xFF0B2A5B)
private val SplashBlue = Color(0xFF1E88E5)
private val SplashLightBlue = Color(0xFF7CC4FF)

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2600, easing = FastOutSlowInEasing)
        )
        delay(300L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFFF7FBFF), Color(0xFFD9EDFF), Color(0xFFA4D7E1))
                )
            )
    ) {
        // Logo au centre
        AppLogo(
            Modifier
                .align(Alignment.Center)
                .offset(y = (-24).dp)
                .size(320.dp)
        )

        // Barre de progression "eau" en bas de la page
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 56.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            WaterProgressBar(
                progress = progress.value,
                modifier = Modifier
                    .width(230.dp)
                    .height(18.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = "${(progress.value * 100).toInt()}%",
                color = SplashNavy,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

/** Barre de progression remplie d'eau qui ondule */
@Composable
private fun WaterProgressBar(progress: Float, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "wave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pill = Path().apply {
            addRoundRect(RoundRect(0f, 0f, w, h, CornerRadius(h / 2f)))
        }

        // Fond du reservoir
        drawPath(pill, Color.White.copy(alpha = 0.8f))

        clipPath(pill) {
            val front = w * progress
            val amp = h * 0.35f

            fun waterPath(extraPhase: Float, shift: Float): Path = Path().apply {
                moveTo(0f, 0f)
                var y = 0f
                while (y <= h) {
                    val x = front + shift + amp * sin(y / h * 2f * PI.toFloat() * 1.5f + phase + extraPhase)
                    lineTo(x.coerceAtLeast(0f), y)
                    y += 1f
                }
                lineTo(0f, h)
                close()
            }

            // Vague claire derriere
            drawPath(
                waterPath(1.8f, h * 0.35f),
                Brush.horizontalGradient(
                    listOf(SplashLightBlue.copy(alpha = 0.6f), SplashBlue.copy(alpha = 0.6f)),
                    startX = 0f,
                    endX = w
                )
            )
            // Vague principale
            drawPath(
                waterPath(0f, 0f),
                Brush.horizontalGradient(
                    listOf(SplashLightBlue, SplashBlue),
                    startX = 0f,
                    endX = w
                )
            )
            // Reflet
            drawLine(
                color = Color.White.copy(alpha = 0.45f),
                start = Offset(h / 2f, h * 0.28f),
                end = Offset((front - h * 0.4f).coerceAtLeast(h / 2f), h * 0.28f),
                strokeWidth = h * 0.16f
            )
        }

        // Contour
        drawPath(pill, SplashBlue.copy(alpha = 0.45f), style = Stroke(width = 1.5.dp.toPx()))
    }
}