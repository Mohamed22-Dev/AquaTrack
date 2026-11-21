package com.example.watertrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun FavoritesScreen(vm: MainViewModel, onBack: () -> Unit) {
    val favorites by vm.favorites.collectAsStateWithLifecycle()
    val tips = allTips.filter { it.id in favorites }

    Column(
        Modifier
            .fillMaxSize()
            .background(AppScreenBg)
    ) {
        SubScreenHeader("Conseils favoris", onBack)

        if (tips.isEmpty()) {
            EmptyState(
                "💙", "Aucun favori",
                "Touchez le cœur d'un conseil dans l'onglet Conseils pour l'enregistrer ici."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tips, key = { it.id }) { tip ->
                    Card(
                        shape = RoundedCornerShape(22.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            Modifier.padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 6.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    tip.title,
                                    fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppNavy
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(tip.detail, fontSize = 14.sp, color = AppTextGray)
                            }
                            IconButton(onClick = { vm.toggleFavorite(tip.id) }) {
                                Icon(
                                    Icons.Filled.Favorite, "Retirer des favoris",
                                    tint = Color(0xFFE53935)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}