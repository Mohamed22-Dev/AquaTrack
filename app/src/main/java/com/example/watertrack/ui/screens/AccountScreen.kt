package com.example.watertrack.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.watertrack.R
import com.example.watertrack.util.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AccountScreen(
    vm: MainViewModel,
    onStats: () -> Unit,
    onGoals: () -> Unit,
    onHistory: () -> Unit,
    onFavorites: () -> Unit,
    onAbout: () -> Unit,
    onSettings: () -> Unit,
    onLogout: () -> Unit
) {
    val user by vm.user.collectAsStateWithLifecycle()
    val today by vm.todayMl.collectAsStateWithLifecycle()
    val goal by vm.goalMl.collectAsStateWithLifecycle()
    val totals by vm.allTotals.collectAsStateWithLifecycle()
    val favorites by vm.favorites.collectAsStateWithLifecycle()

    var showPremium by remember { mutableStateOf(false) }
    var showLogout by remember { mutableStateOf(false) }

    val photo = rememberFileBitmap(user?.photoPath)
    val progress = if (goal > 0) ((today * 100f) / goal).toInt().coerceIn(0, 100) else 0
    val displayName = user?.let { u ->
        "${u.firstName} ${u.lastName}".trim().ifEmpty { u.username }
    } ?: ""
    val hasEmail = user?.email?.isNotEmpty() == true
    val contact = user?.let { if (it.email.isNotEmpty()) it.email else it.phone } ?: ""

    Column(
        Modifier
            .fillMaxSize()
            .background(AppScreenBg)
            .verticalScroll(rememberScrollState())
    ) {
        // ---- الهيدر المائي مع صورة Profil ----
        WaterWaveHeader(
            drawableRes = R.drawable.profil,
            height = 235.dp,
            showSettingsButton = true,
            onSettingsClick = onSettings
        ) {
            // Photo (عرض الصورة دون قابليتها للنقر)
            Box(
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
                    .size(138.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(3.dp, AppBlueMid, CircleShape)
                        .padding(6.dp)
                        .clip(CircleShape)
                        .background(AppBlueMid.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (photo != null) {
                        Image(
                            bitmap = photo,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            Icons.Filled.Person, null,
                            tint = AppBlueMid, modifier = Modifier.size(70.dp)
                        )
                    }
                }
            }
        }

        // ---- Nom + contact ----
        Column(
            Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                displayName,
                fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = AppNavy,
                textAlign = TextAlign.Center
            )
            if (contact.isNotEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (hasEmail) Icons.Filled.Email else Icons.Filled.Phone, null,
                        tint = AppBlueMid, modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(contact, fontSize = 16.sp, color = AppTextGray)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Statistiques rapides ----
        Card(
            shape = RoundedCornerShape(26.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QuickStat(
                    Modifier.weight(1f), Icons.Filled.WaterDrop, AppBlueMid,
                    "%.1f L".format(today / 1000f), "Aujourd'hui"
                )
                Divider1()
                QuickStat(
                    Modifier.weight(1f), Icons.Filled.EmojiEvents, Color(0xFFF5A623),
                    "$progress%", "Objectif"
                )
                Divider1()
                QuickStat(
                    Modifier.weight(1f), Icons.Filled.Star, Color(0xFFFFC107),
                    "${totals.size}", "Jours au total"
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Premium ----
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(
                    Brush.horizontalGradient(listOf(Color(0xFF0B4DB8), Color(0xFF3B8FF0)))
                )
                .clickable { showPremium = true }
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("👑", fontSize = 38.sp)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    "Passer à Premium",
                    color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    "Débloquez plus de fonctionnalités",
                    color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp
                )
            }
            Text("✨", fontSize = 28.sp)
        }

        Spacer(Modifier.height(16.dp))

        // ---- Menu ----
        Column(
            Modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MenuCard(
                Icons.Filled.BarChart, "Mes statistiques",
                "Consultez vos statistiques détaillées", onStats
            )
            MenuCard(
                Icons.Filled.TrackChanges, "Mes objectifs",
                "Gérez votre objectif quotidien", onGoals
            )
            MenuCard(
                Icons.Filled.History, "Historique d'hydratation",
                "Consultez vos relevés passés", onHistory
            )
            MenuCard(
                Icons.Filled.Favorite, "Conseils favoris",
                if (favorites.isEmpty()) "Enregistrez vos conseils préférés"
                else "${favorites.size} conseil(s) enregistré(s)",
                onFavorites
            )
            MenuCard(
                Icons.Filled.Info, "À propos de WaterTrack",
                "Version 1.0.0", onAbout
            )
            MenuCard(
                Icons.Filled.Logout, "Déconnexion",
                "Quitter votre compte", { showLogout = true },
                tint = Color(0xFFE53935)
            )
        }

        Spacer(Modifier.height(16.dp))
    }

    if (showPremium) {
        AlertDialog(
            onDismissRequest = { showPremium = false },
            title = { Text("👑 Premium") },
            text = { Text("Cette fonctionnalité sera bientôt disponible.") },
            confirmButton = { TextButton(onClick = { showPremium = false }) { Text("OK") } }
        )
    }

    if (showLogout) {
        LogoutDialog(
            onConfirm = {
                showLogout = false
                vm.logout(onLogout)
            },
            onDismiss = { showLogout = false }
        )
    }
}

@Composable
private fun Divider1() {
    Box(
        Modifier
            .width(1.dp)
            .height(52.dp)
            .background(Color(0xFFDDE8F7))
    )
}

@Composable
private fun QuickStat(
    modifier: Modifier,
    icon: ImageVector,
    iconColor: Color,
    value: String,
    label: String
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, null, tint = iconColor, modifier = Modifier.size(30.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = AppNavy)
        Text(label, fontSize = 13.sp, color = AppTextGray)
    }
}

@Composable
private fun MenuCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    tint: Color = AppBlueDark
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(tint.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = tint, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppNavy)
                Text(subtitle, fontSize = 13.sp, color = AppTextGray)
            }
            Icon(Icons.Filled.ChevronRight, null, tint = AppBlueDark)
        }
    }
}

@Composable
private fun rememberFileBitmap(path: String?): ImageBitmap? {
    val state = produceState<ImageBitmap?>(initialValue = null, path) {
        value = if (path == null) null else withContext(Dispatchers.IO) {
            ImageUtils.loadFile(path)?.asImageBitmap()
        }
    }
    return state.value
}