package com.example.watertrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.watertrack.data.GoalEntity
import com.example.watertrack.ui.theme.BluePrimary
import com.example.watertrack.ui.theme.GreenAccent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun StatsScreen(vm: MainViewModel, onBack: () -> Unit) {
    val week by vm.weekData.collectAsStateWithLifecycle()
    val totals by vm.allTotals.collectAsStateWithLifecycle()
    val history by vm.goalHistory.collectAsStateWithLifecycle()

    val weekValues = week.map { it.second }
    val weekAvg = if (weekValues.isEmpty()) 0f else weekValues.sum() / weekValues.size
    val bestMl = totals.maxOfOrNull { it.total } ?: 0
    val totalMl = totals.sumOf { it.total }
    val streak = remember(history) { computeStreak(history) }
    val achieved = history.count { it.achieved }

    Column(
        Modifier
            .fillMaxSize()
            .background(AppScreenBg)
    ) {
        SubScreenHeader("Mes statistiques", onBack)

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    Modifier.weight(1f), Icons.Filled.TrendingUp, Color(0xFF8E6CEF),
                    "%.1f L".format(weekAvg), "Moyenne / jour (7 j)"
                )
                StatTile(
                    Modifier.weight(1f), Icons.Filled.EmojiEvents, Color(0xFFF5A623),
                    "%.1f L".format(bestMl / 1000f), "Meilleur jour"
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    Modifier.weight(1f), Icons.Filled.WaterDrop, BluePrimary,
                    "%.1f L".format(totalMl / 1000f), "Total bu"
                )
                StatTile(
                    Modifier.weight(1f), Icons.Filled.Whatshot, Color(0xFFFF6B35),
                    "$streak j", "Série en cours"
                )
            }

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "7 derniers jours (en litres)",
                        fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppNavy
                    )
                    Spacer(Modifier.height(16.dp))
                    WeekBars(week)
                }
            }

            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.CheckCircle, null,
                        tint = GreenAccent, modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Objectifs atteints",
                            fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppNavy
                        )
                        Text(
                            "$achieved jour(s) sur ${history.size}",
                            color = AppTextGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatTile(
    modifier: Modifier,
    icon: ImageVector,
    color: Color,
    value: String,
    label: String
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
    ) {
        Column(Modifier.padding(16.dp)) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.height(10.dp))
            Text(value, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = AppNavy)
            Text(label, fontSize = 13.sp, color = AppTextGray)
        }
    }
}

@Composable
private fun WeekBars(data: List<Pair<String, Float>>) {
    val max = (data.maxOfOrNull { it.second } ?: 0f).coerceAtLeast(0.5f)
    Row(
        Modifier
            .fillMaxWidth()
            .height(200.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEachIndexed { i, pair ->
            val label = pair.first
            val v = pair.second
            Column(
                Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    if (v > 0f) "%.1f".format(v) else "",
                    fontSize = 11.sp, color = AppTextGray
                )
                Spacer(Modifier.height(4.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height((140.dp * (v / max)).coerceAtLeast(4.dp))
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                        .background(if (i % 2 == 0) BluePrimary else GreenAccent)
                )
                Spacer(Modifier.height(6.dp))
                Text(label, fontSize = 12.sp, color = AppNavy)
            }
        }
    }
}

/** Nombre de jours consecutifs ou l'objectif a ete atteint */
private fun computeStreak(history: List<GoalEntity>): Int {
    val achieved = history.filter { it.achieved }.map { it.date }.toSet()
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val cal = Calendar.getInstance()
    if (fmt.format(cal.time) !in achieved) cal.add(Calendar.DAY_OF_YEAR, -1)
    var count = 0
    while (fmt.format(cal.time) in achieved) {
        count++
        cal.add(Calendar.DAY_OF_YEAR, -1)
    }
    return count
}