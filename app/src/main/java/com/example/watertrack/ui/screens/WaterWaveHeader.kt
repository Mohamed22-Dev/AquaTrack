package com.example.watertrack.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val HeaderNavyTitle = Color(0xFF0A2540)
private val HeaderSubtitleColor = Color(0xFF5A78A0)
private val IconBlue = Color(0xFF0B5ED7)

@Composable
fun WaterWaveHeader(
    @DrawableRes drawableRes: Int,
    modifier: Modifier = Modifier,
    height: Dp = 160.dp,
    title: String? = null,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    showSettingsButton: Boolean = false,
    onSettingsClick: (() -> Unit)? = null,
    showNotificationButton: Boolean = false,
    onNotificationClick: (() -> Unit)? = null,
    content: (@Composable BoxScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        // 1. Image background
        Image(
            painter = painterResource(id = drawableRes),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 2. Smooth White Gradient Fade at Bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFFF4F8FF).copy(alpha = 0.85f),
                            Color(0xFFF4F8FF)
                        )
                    )
                )
        )

        // 3. Header Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showBackButton) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .shadow(3.dp, CircleShape)
                            .background(Color.White, CircleShape)
                            .clickable { onBackClick?.invoke() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ChevronLeft,
                            contentDescription = "Retour",
                            tint = IconBlue,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                }

                if (title != null) {
                    Column {
                        Text(
                            text = title,
                            color = HeaderNavyTitle,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                color = HeaderSubtitleColor,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                }
            }

            if (showSettingsButton) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(44.dp)
                        .shadow(3.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .clickable { onSettingsClick?.invoke() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Settings,
                        contentDescription = "Paramètres",
                        tint = IconBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // زر المنبه والتذكيرات الشغالة بـ WorkManager
            if (showNotificationButton) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(44.dp)
                        .shadow(3.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .clickable { onNotificationClick?.invoke() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.NotificationsActive,
                        contentDescription = "Rappels",
                        tint = IconBlue,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            content?.invoke(this)
        }
    }
}