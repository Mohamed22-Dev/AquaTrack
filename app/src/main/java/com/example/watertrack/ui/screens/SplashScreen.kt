package com.example.watertrack.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.dp
import com.example.watertrack.ui.theme.BlueGradient
import com.example.watertrack.ui.theme.White
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2800L)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlueGradient)
    ) {
        // Logo au centre
        AppLogo(
            Modifier
                .align(Alignment.Center)
                .size(300.dp)
        )

        // Goutte d'eau en bas de la page
        WaterDropLoader(
            Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(bottom = 48.dp)
                .size(width = 56.dp, height = 76.dp)
        )
    }
}

@Composable
fun WaterDropLoader(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "drop")

    val fill by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fill"
    )

    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave"
    )

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val drop = Path().apply {
            moveTo(w / 2, 0f)
            cubicTo(w * 0.90f, h * 0.38f, w * 1.00f, h * 0.98f, w / 2, h * 0.98f)
            cubicTo(w * 0.00f, h * 0.98f, w * 0.10f, h * 0.38f, w / 2, 0f)
            close()
        }

        val level = h * (1f - fill)
        val amplitude = h * 0.035f
        val wave = Path().apply {
            moveTo(0f, h)
            lineTo(0f, level)
            var x = 0f
            while (x <= w) {
                val y = level + amplitude * sin((x / w) * 2f * PI.toFloat() * 1.5f + phase)
                lineTo(x, y)
                x += 3f
            }
            lineTo(w, h)
            close()
        }

        clipPath(drop) {
            drawPath(wave, color = White.copy(alpha = 0.9f))
        }

        drawPath(drop, color = White, style = Stroke(width = 3.dp.toPx()))
    }
}