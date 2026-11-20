package com.example.watertrack.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.watertrack.ui.theme.BlueGradient
import com.example.watertrack.ui.theme.BlueLight
import com.example.watertrack.ui.theme.BluePrimary
import com.example.watertrack.ui.theme.GreenAccent
import com.example.watertrack.ui.theme.White
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlin.math.PI
import kotlin.math.sin

private data class QuickItem(
    val ml: Int,
    val label: String,
    val icon: ImageVector,
    val highlight: Boolean = false
)

private val quickItems = listOf(
    QuickItem(100, "Petit verre", Icons.Filled.LocalCafe),
    QuickItem(200, "Tasse", Icons.Filled.FreeBreakfast),
    QuickItem(250, "Verre", Icons.Filled.WaterDrop),
    QuickItem(350, "Mug", Icons.Filled.Coffee),
    QuickItem(500, "Bouteille", Icons.Filled.LocalDrink, highlight = true),
    QuickItem(750, "Gourde", Icons.Filled.FitnessCenter, highlight = true)
)

@Composable
fun DashboardScreen(vm: MainViewModel) {
    val today by vm.todayMl.collectAsStateWithLifecycle()
    val goal by vm.goalMl.collectAsStateWithLifecycle()
    val week by vm.weekData.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(false) }

    val progress = if (goal > 0) (today.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val remaining = (goal - today).coerceAtLeast(0)

    val weekValues = week.map { it.second }
    val weekTotal = weekValues.sum()
    val weekAvg = if (weekValues.isEmpty()) 0f else weekTotal / weekValues.size
    val bestDay = weekValues.maxOrNull() ?: 0f

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize()) {
            // En-tete centre
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(BlueGradient)
                    .statusBarsPadding()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Tableau de bord",
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                WaterRing(today = today, goal = goal, progress = progress)

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(BlueLight.copy(alpha = 0.3f))
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.WaterDrop, null,
                        tint = BluePrimary, modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (remaining > 0) "$remaining ml restants" else "Objectif atteint !",
                        color = BluePrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ajout rapide", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    OutlinedButton(
                        onClick = { showDialog = true },
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, Color.LightGray)
                    ) {
                        Icon(Icons.Filled.Add, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Personnalisé")
                    }
                }

                Spacer(Modifier.height(12.dp))

                quickItems.chunked(2).forEach { rowItems ->
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        rowItems.forEach { item ->
                            QuickAddCard(
                                item = item,
                                modifier = Modifier.weight(1f),
                                onClick = { vm.addWater(item.ml) }
                            )
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(8.dp))

                Text(
                    "Vos statistiques",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        Icons.Filled.TrendingUp, Color(0xFF8E6CEF),
                        "%.1f".format(weekAvg), "Moy. hebdo", Modifier.weight(1f)
                    )
                    StatCard(
                        Icons.Filled.EmojiEvents, Color(0xFFF5A623),
                        "%.1f".format(bestDay), "Meilleur jour", Modifier.weight(1f)
                    )
                    StatCard(
                        Icons.Filled.WaterDrop, BluePrimary,
                        "%.1f".format(weekTotal), "Cette semaine", Modifier.weight(1f)
                    )
                }

                Spacer(Modifier.height(24.dp))

                Text(
                    "Consommation de la semaine",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                WeekChart(week)

                Spacer(Modifier.height(90.dp))
            }
        }

    }

    if (showDialog) {
        CustomWaterDialog(
            onDismiss = { showDialog = false },
            onAdd = { ml ->
                vm.addWater(ml)
                showDialog = false
            }
        )
    }
}

/** Anneau de progression avec de l'eau qui monte et ondule */
@Composable
private fun WaterRing(today: Int, goal: Int, progress: Float) {
    val animated by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(900),
        label = "progress"
    )

    val transition = rememberInfiniteTransition(label = "wave")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val strokeW = 14.dp.toPx()
            val radius = (size.minDimension - strokeW) / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val topLeft = Offset(center.x - radius, center.y - radius)
            val arcSize = Size(radius * 2, radius * 2)

            // Eau a l'interieur du cercle
            val innerR = radius - strokeW / 2f
            val circle = Path().apply { addOval(Rect(center, innerR)) }
            val level = center.y + innerR - (innerR * 2f) * animated
            val amp = 6.dp.toPx()
            val wave = Path().apply {
                moveTo(center.x - innerR, center.y + innerR)
                lineTo(center.x - innerR, level)
                var x = center.x - innerR
                val end = center.x + innerR
                while (x <= end) {
                    val y = level + amp * sin(((x - center.x) / innerR) * 3f + phase)
                    lineTo(x, y)
                    x += 4f
                }
                lineTo(end, center.y + innerR)
                close()
            }
            clipPath(circle) {
                drawPath(wave, color = BlueLight.copy(alpha = 0.55f))
            }

            // Piste grise
            drawArc(
                color = Color(0xFFE3E3E3),
                startAngle = 0f, sweepAngle = 360f, useCenter = false,
                topLeft = topLeft, size = arcSize,
                style = Stroke(strokeW)
            )
            // Progression
            drawArc(
                color = BluePrimary,
                startAngle = -90f, sweepAngle = 360f * animated, useCenter = false,
                topLeft = topLeft, size = arcSize,
                style = Stroke(strokeW, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(today.toString(), fontSize = 46.sp, fontWeight = FontWeight.Bold)
                Text(
                    " ml",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Text("sur $goal ml", fontSize = 16.sp, color = Color.DarkGray)
            Text(
                "${(progress * 100).toInt()}%",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = BluePrimary
            )
        }
    }
}

@Composable
private fun QuickAddCard(item: QuickItem, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = if (item.highlight) BlueLight.copy(alpha = 0.3f) else White
        ),
        border = BorderStroke(
            1.dp,
            if (item.highlight) BlueLight else Color(0xFFE6E6E6)
        ),
        modifier = modifier
            .clip(shape)
            .clickable { onClick() }
    ) {
        Row(
            Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (item.highlight) BlueLight.copy(alpha = 0.6f) else Color(0xFFEFEFEF)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.icon, null,
                    tint = if (item.highlight) BluePrimary else Color(0xFF444444),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text("+${item.ml} ml", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(item.label, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    color: Color,
    value: String,
    label: String,
    modifier: Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        border = BorderStroke(1.dp, Color(0xFFE6E6E6)),
        modifier = modifier
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(
                    " L",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 3.dp)
                )
            }
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun WeekChart(data: List<Pair<String, Float>>) {
    val blue = BluePrimary.toArgb()
    val green = GreenAccent.toArgb()

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        factory = { ctx ->
            BarChart(ctx).apply {
                description.isEnabled = false
                legend.isEnabled = false
                axisRight.isEnabled = false
                axisLeft.axisMinimum = 0f
                setTouchEnabled(false)
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f
            }
        },
        update = { chart ->
            if (data.isNotEmpty()) {
                val entries = data.mapIndexed { i, p -> BarEntry(i.toFloat(), p.second) }
                val set = BarDataSet(entries, "").apply {
                    colors = List(entries.size) { if (it % 2 == 0) blue else green }
                    setDrawValues(false)
                }
                chart.xAxis.valueFormatter = IndexAxisValueFormatter(data.map { it.first })
                chart.data = BarData(set).apply { barWidth = 0.6f }
                chart.animateY(500)
                chart.invalidate()
            }
        }
    )
}

@Composable
private fun CustomWaterDialog(onDismiss: () -> Unit, onAdd: (Int) -> Unit) {
    var custom by remember { mutableStateOf("") }
    val value = custom.toIntOrNull()
    val valid = value != null && value in 1..5000

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Quantité personnalisée") },
        text = {
            OutlinedTextField(
                value = custom,
                onValueChange = { custom = it.filter(Char::isDigit).take(4) },
                label = { Text("Quantité (ml)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(
                onClick = { value?.let(onAdd) },
                enabled = valid
            ) { Text("Ajouter") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}