package com.example.watertrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AboutScreen(onBack: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(AppScreenBg)
    ) {
        SubScreenHeader("À propos", onBack)

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AppLogo(Modifier.size(180.dp))
            Text("WaterTrack", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = AppNavy)
            Text("Version 1.0.0", color = AppTextGray)
            Spacer(Modifier.height(16.dp))
            Text(
                "WaterTrack vous aide à adopter une meilleure routine d'hydratation : " +
                        "enregistrez votre consommation, recevez des rappels personnalisés " +
                        "et suivez vos habitudes grâce à des statistiques détaillées.",
                textAlign = TextAlign.Center, color = AppTextGray, fontSize = 15.sp
            )
            Spacer(Modifier.height(20.dp))

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Technologies",
                        fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppNavy
                    )
                    Spacer(Modifier.height(8.dp))
                    listOf(
                        "Kotlin et Jetpack Compose",
                        "Room (stockage des données)",
                        "WorkManager (rappels automatiques)",
                        "MPAndroidChart (graphiques)"
                    ).forEach {
                        Text("• $it", color = AppTextGray, fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(
                "Projet N°2 - Composant Android (M205)",
                color = AppTextGray, fontSize = 12.sp
            )
        }
    }
}