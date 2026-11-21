package com.example.watertrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.watertrack.data.WaterLogEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(vm: MainViewModel, onBack: () -> Unit) {
    val logs by vm.allLogs.collectAsStateWithLifecycle()
    var toDelete by remember { mutableStateOf<WaterLogEntity?>(null) }

    val dayFmt = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US) }
    val timeFmt = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val grouped = remember(logs) { logs.groupBy { dayFmt.format(Date(it.timestamp)) } }

    Column(
        Modifier
            .fillMaxSize()
            .background(AppScreenBg)
    ) {
        SubScreenHeader("Historique d'hydratation", onBack)

        if (logs.isEmpty()) {
            EmptyState(
                "💧", "Aucun historique",
                "Ajoutez de l'eau depuis l'accueil pour voir vos relevés ici."
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                grouped.forEach { entry ->
                    val day = entry.key
                    val dayLogs = entry.value
                    item(key = "h_$day") {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                dayLabel(day),
                                fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppNavy
                            )
                            Text(
                                "%.2f L".format(dayLogs.sumOf { it.amountMl } / 1000f),
                                fontSize = 15.sp, fontWeight = FontWeight.SemiBold,
                                color = AppBlueDark
                            )
                        }
                    }
                    items(dayLogs, key = { "l_${it.id}" }) { log ->
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                Modifier
                                    .padding(start = 16.dp, end = 4.dp, top = 4.dp, bottom = 4.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.WaterDrop, null,
                                    tint = AppBlueMid, modifier = Modifier.size(24.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    "+${log.amountMl} ml",
                                    fontSize = 16.sp, fontWeight = FontWeight.SemiBold,
                                    color = AppNavy, modifier = Modifier.weight(1f)
                                )
                                Text(
                                    timeFmt.format(Date(log.timestamp)),
                                    color = AppTextGray, fontSize = 14.sp
                                )
                                IconButton(onClick = { toDelete = log }) {
                                    Icon(
                                        Icons.Filled.Delete, "Supprimer",
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

    toDelete?.let { log ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Supprimer cet ajout ?") },
            text = { Text("+${log.amountMl} ml sera retiré de votre historique.") },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteLog(log.id)
                    toDelete = null
                }) { Text("Supprimer", color = Color(0xFFE53935)) }
            },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text("Annuler") } }
        )
    }
}

private fun dayLabel(dayKey: String): String {
    val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val today = fmt.format(Date())
    val yesterday = fmt.format(Date(System.currentTimeMillis() - 86_400_000L))
    return when (dayKey) {
        today -> "Aujourd'hui"
        yesterday -> "Hier"
        else -> {
            val date = fmt.parse(dayKey) ?: return dayKey
            SimpleDateFormat("EEEE d MMMM", Locale.FRENCH).format(date)
                .replaceFirstChar { it.uppercase() }
        }
    }
}