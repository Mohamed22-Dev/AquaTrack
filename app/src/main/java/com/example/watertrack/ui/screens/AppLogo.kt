package com.example.watertrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.watertrack.R

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.logo_aquatrack),
        contentDescription = "AquaTrack Logo",
        modifier = modifier,
        contentScale = ContentScale.Fit
    )
}