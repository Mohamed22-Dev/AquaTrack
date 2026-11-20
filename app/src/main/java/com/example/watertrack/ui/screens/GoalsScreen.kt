package com.example.watertrack.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.watertrack.data.GoalEntity
import com.example.watertrack.ui.theme.BlueGradient
import com.example.watertrack.ui.theme.BluePrimary
import com.example.watertrack.ui.theme.GreenAccent
import com.example.watertrack.ui.theme.RedError
import com.example.watertrack.ui.theme.TextDark
import com.example.watertrack.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun GoalsScreen(vm: MainViewModel) {
    val today by vm.todayMl.collectAsStateWithLifecycle()
    val goal by vm.goalMl.collectAsStateWithLifecycle()
    val cup by vm.cupMl.collectAsStateWithLifecycle()
    val history by vm.goalHistory.collectAsStateWithLifecycle()

    var showGoalDialog by remember { mutableStateOf(false) }
    var showCupDialog by remember { mutableStateOf(false) }

    val progress = if (goal > 0) (today.toFloat() / goal).coerceIn(0f, 1f) else 0f
    val cups = if (cup > 0) today.toFloat() / cup else 0f
    val cupsText = if (cups % 1f == 0f) cups.toInt().toString() else "%.1f".format(cups)

    val dateText = remember {
        SimpleDateFormat("EEEE d MMMM", Locale.FRENCH).format(Date())
            .replaceFirstChar { it.uppercase() }
    }
    val todayKey = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }

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
            Text("Objectifs", color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Date
            item {
                Text(dateText, fontSize = 18.sp, color = Color.DarkGray)
            }

            // Grand cercle d'eau
            item {
                WaterCircle(
                    today = today,
                    goal = goal,
                    progress = progress,
                    cupsText = cupsText
                )
            }

            // Bouton ajouter de l'eau
            item {
                Button(
                    onClick = { vm.addCup() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text(
                        "+ Ajouter de l'eau ($cup ml)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Taille du verre
            item {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { showCupDialog = true }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.LocalDrink, null, tint = BluePrimary)
                    Spacer(Modifier.width(8.dp))
                    Text("$cup ml par verre", fontSize = 16.sp)
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        Icons.Filled.Edit, null,
                        tint = Color.Gray, modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Annuler / Modifier l'objectif
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    ElevatedButton(
                        onClick = { vm.undoLast() },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = White, contentColor = TextDark
                        )
                    ) {
                        Icon(
                            Icons.Filled.Undo, null,
                            tint = BluePrimary, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Annuler le dernier",
                            fontSize = 13.sp,
                            lineHeight = 16.sp,
                            maxLines = 2,
                            textAlign = TextAlign.Center
                        )
                    }
                    ElevatedButton(
                        onClick = { showGoalDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = White, contentColor = TextDark
                        )
                    ) {
                        Icon(
                            Icons.Filled.Flag, null,
                            tint = BluePrimary, modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Modifier l'objectif",
                            fontSize = 13.sp,
                            lineHeight = 16.sp,
                            maxLines = 2,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Historique
            item {
                Spacer(Modifier.height(6.dp))
                Text(
                    "Historique des objectifs",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (history.isEmpty()) {
                item {
                    Text("Aucun historique pour le moment.", modifier = Modifier.fillMaxWidth())
                }
            } else {
                items(history) { g -> GoalRow(g, isToday = g.date == todayKey) }
            }
        }
    }

    if (showGoalDialog) {
        NumberDialog(
            title = "Objectif quotidien",
            label = "Objectif (ml)",
            initial = goal,
            min = 500,
            max = 10000,
            onDismiss = { showGoalDialog = false },
            onSave = {
                vm.setDailyGoal(it)
                showGoalDialog = false
            }
        )
    }

    if (showCupDialog) {
        NumberDialog(
            title = "Taille du verre",
            label = "Quantité (ml)",
            initial = cup,
            min = 50,
            max = 2000,
            onDismiss = { showCupDialog = false },
            onSave = {
                vm.setCupMl(it)
                showCupDialog = false
            }
        )
    }
}

/** Cercle avec de l'eau qui monte et ondule */
@Composable
private fun WaterCircle(today: Int, goal: Int, progress: Float, cupsText: String) {
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
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val textColor = if (animated > 0.55f) White else TextDark

    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(280.dp)) {
        Canvas(Modifier.fillMaxSize()) {
            val r = size.minDimension / 2f
            val center = Offset(size.width / 2f, size.height / 2f)
            val circle = Path().apply { addOval(Rect(center, r)) }
            val level = center.y + r - (2f * r) * animated
            val amp = 8.dp.toPx()

            drawCircle(color = Color(0xFFF2F2F2), radius = r, center = center)

            clipPath(circle) {
                drawPath(
                    wavePath(size.width, size.height, level, amp, phase + 1.5f),
                    color = BluePrimary.copy(alpha = 0.45f)
                )
                drawPath(
                    wavePath(size.width, size.height, level + 4.dp.toPx(), amp, phase),
                    color = BluePrimary
                )
            }

            drawCircle(
                color = Color(0xFFD9D9D9),
                radius = r - 1.dp.toPx(),
                center = center,
                style = Stroke(2.dp.toPx())
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${(progress * 100).toInt()}%",
                fontSize = 54.sp,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text("$today ml / $goal ml", fontSize = 18.sp, color = textColor)
            Text("$cupsText verres", fontSize = 15.sp, color = textColor)
        }
    }
}

private fun wavePath(
    width: Float,
    height: Float,
    level: Float,
    amplitude: Float,
    phase: Float
): Path = Path().apply {
    moveTo(0f, height)
    lineTo(0f, level)
    var x = 0f
    while (x <= width) {
        val y = level + amplitude * sin((x / width) * 2f * PI.toFloat() * 1.2f + phase)
        lineTo(x, y)
        x += 4f
    }
    lineTo(width, height)
    close()
}

@Composable
private fun GoalRow(g: GoalEntity, isToday: Boolean) {
    val label = if (isToday) "Aujourd'hui" else g.date
    val statusText = when {
        g.achieved -> "Atteint"
        isToday -> "En cours"
        else -> "Non atteint"
    }
    Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (g.achieved) Icons.Filled.CheckCircle else Icons.Filled.Cancel,
                contentDescription = null,
                tint = if (g.achieved) GreenAccent else if (isToday) BluePrimary else RedError
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "%.1f L - %s".format(g.targetMl / 1000f, statusText),
                modifier = Modifier.weight(1f)
            )
            Text(label, fontSize = 13.sp)
        }
    }
}

@Composable
private fun NumberDialog(
    title: String,
    label: String,
    initial: Int,
    min: Int,
    max: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var text by remember { mutableStateOf(initial.toString()) }
    val value = text.toIntOrNull()
    val valid = value != null && value in min..max

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it.filter(Char::isDigit).take(5) },
                    label = { Text(label) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    isError = !valid
                )
                if (!valid) {
                    Text(
                        "Entre $min et $max ml",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { value?.let(onSave) }, enabled = valid) {
                Text("Enregistrer")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}