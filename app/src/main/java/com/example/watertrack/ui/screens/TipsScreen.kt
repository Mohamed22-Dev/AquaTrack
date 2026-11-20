package com.example.watertrack.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

private val Navy = Color(0xFF0B2A5B)
private val BlueDark = Color(0xFF1558C8)
private val BlueMid = Color(0xFF3B8FF0)
private val BlueSoft = Color(0xFFE3F0FF)
private val ScreenBg = Color(0xFFF4F8FF)

private enum class TipCategory(val label: String, val icon: ImageVector) {
    ALL("Tous", Icons.Filled.Star),
    HEALTH("Santé", Icons.Filled.Favorite),
    LIFESTYLE("Lifestyle", Icons.Filled.DirectionsRun),
    DAILY("Quotidien", Icons.Filled.WbSunny)
}

private data class Tip(
    val category: TipCategory,
    val icon: ImageVector,
    val title: String,
    val short: String,
    val detail: String
)

private val tips = listOf(
    Tip(
        TipCategory.HEALTH, Icons.Filled.Favorite,
        "Restez en bonne santé",
        "Boire de l'eau renforce votre système immunitaire.",
        "Une bonne hydratation aide le corps à éliminer les toxines, protège les reins et soutient les défenses naturelles."
    ),
    Tip(
        TipCategory.LIFESTYLE, Icons.Filled.DirectionsRun,
        "Pendant le sport",
        "Hydratez-vous avant, pendant et après l'effort.",
        "Buvez un verre d'eau avant l'effort, quelques gorgées toutes les 15 à 20 minutes pendant, et rehydratez-vous après."
    ),
    Tip(
        TipCategory.DAILY, Icons.Filled.WbSunny,
        "Par temps chaud",
        "Augmentez votre consommation d'eau quand il fait chaud.",
        "La transpiration augmente avec la chaleur. Pensez à boire même sans soif, surtout en été."
    ),
    Tip(
        TipCategory.DAILY, Icons.Filled.NightsStay,
        "Avant de dormir",
        "Un verre d'eau peut prévenir la déshydratation nocturne.",
        "Un petit verre avant le coucher aide, mais évitez d'en boire trop pour ne pas perturber votre sommeil."
    ),
    Tip(
        TipCategory.HEALTH, Icons.Filled.Restaurant,
        "Aliments riches en eau",
        "Fruits et légumes contribuent à votre hydratation.",
        "Concombre, pastèque, orange et tomate contiennent plus de 85 % d'eau."
    ),
    Tip(
        TipCategory.HEALTH, Icons.Filled.Block,
        "Évitez les boissons sucrées",
        "Préférez l'eau aux sodas et jus industriels.",
        "Les boissons sucrées apportent des calories inutiles. L'eau reste la meilleure boisson au quotidien."
    ),
    Tip(
        TipCategory.LIFESTYLE, Icons.Filled.LocalDrink,
        "Gardez une bouteille près de vous",
        "Avoir de l'eau à portée de main aide à boire plus souvent.",
        "Placez une bouteille sur votre bureau ou dans votre sac pour y penser tout au long de la journée."
    ),
    Tip(
        TipCategory.DAILY, Icons.Filled.Alarm,
        "Au réveil",
        "Commencez la journée par un grand verre d'eau.",
        "Après la nuit, le corps est légèrement déshydraté. Un verre d'eau au réveil relance le métabolisme."
    )
)

private val dailyTips = listOf(
    "Buvez un verre d'eau avant chaque repas !" to "Cela aide à la digestion et à mieux absorber les nutriments.",
    "Commencez la journée par un verre d'eau !" to "Cela réhydrate le corps après la nuit.",
    "Gardez une bouteille près de vous !" to "Vous boirez plus souvent sans y penser.",
    "Mangez des fruits riches en eau !" to "Pastèque, orange et concombre aident à rester hydraté."
)

@Composable
fun TipsScreen() {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(TipCategory.ALL) }
    var openedTip by remember { mutableStateOf<Tip?>(null) }

    val dayOfYear = remember { Calendar.getInstance().get(Calendar.DAY_OF_YEAR) }
    val daily = dailyTips[dayOfYear % dailyTips.size]

    val filtered = if (selected == TipCategory.ALL) tips else tips.filter { it.category == selected }

    Column(
        Modifier
            .fillMaxSize()
            .background(ScreenBg)
            .verticalScroll(rememberScrollState())
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
    ) {
        // En-tete
        Row(
            Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.WaterDrop, null,
                tint = BlueMid, modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    "Conseils d'hydratation",
                    fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Navy
                )
                Text(
                    "Petites habitudes, grande différence !",
                    fontSize = 13.sp, color = Color.Gray
                )
            }
        }

        // Astuce du jour
        DailyTipCard(title = daily.first, text = daily.second)

        Spacer(Modifier.height(16.dp))

        // Filtres
        Row(
            Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            TipCategory.values().forEach { cat ->
                CategoryChip(cat, cat == selected) { selected = cat }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Liste des conseils
        filtered.forEach { tip ->
            TipCard(tip) { openedTip = tip }
            Spacer(Modifier.height(10.dp))
        }

        Spacer(Modifier.height(8.dp))

        // Ressources utiles
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Ressources utiles", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Navy)
            Row(
                Modifier.clickable {
                    openUrl(context, "https://www.youtube.com/results?search_query=importance+hydratation+eau")
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Voir plus", color = BlueDark, fontWeight = FontWeight.SemiBold)
                Icon(Icons.Filled.ChevronRight, null, tint = BlueDark)
            }
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ResourceCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.PlayArrow,
                tag = "Vidéo",
                title = "L'importance de l'eau",
                duration = "2 min",
                onClick = {
                    openUrl(context, "https://www.youtube.com/results?search_query=importance+de+l%27eau")
                }
            )
            ResourceCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.Article,
                tag = "Article",
                title = "Les bienfaits de l'hydratation",
                duration = "5 min",
                onClick = {
                    openUrl(context, "https://www.who.int/fr/news-room/fact-sheets/detail/healthy-diet")
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        // Partager
        Row(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(Brush.horizontalGradient(listOf(BlueDark, BlueMid)))
                .clickable {
                    shareText(
                        context,
                        "Conseils d'hydratation WaterTrack :\n" +
                                tips.joinToString("\n") { "- ${it.title} : ${it.short}" }
                    )
                }
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Share, null, tint = Color.White, modifier = Modifier.size(28.dp))
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "Partager ces conseils",
                    color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold
                )
                Text(
                    "Partagez ces astuces avec vos amis !",
                    color = Color.White.copy(alpha = 0.9f), fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    openedTip?.let { tip ->
        AlertDialog(
            onDismissRequest = { openedTip = null },
            title = { Text(tip.title, color = Navy, fontWeight = FontWeight.Bold) },
            text = { Text(tip.detail) },
            confirmButton = {
                TextButton(onClick = {
                    shareText(context, "${tip.title} : ${tip.detail}")
                    openedTip = null
                }) { Text("Partager") }
            },
            dismissButton = { TextButton(onClick = { openedTip = null }) { Text("Fermer") } }
        )
    }
}

@Composable
private fun DailyTipCard(title: String, text: String) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1558C8), Color(0xFF4AA8FF))))
    ) {
        // Cercles decoratifs
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .offset(x = 30.dp)
                .size(170.dp)
                .background(Color.White.copy(alpha = 0.10f), CircleShape)
        )
        Icon(
            Icons.Filled.LocalDrink, null,
            tint = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 20.dp)
                .size(110.dp)
        )

        Column(Modifier.padding(20.dp)) {
            Row(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Star, null, tint = Color(0xFFF5A623), modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text("Astuce du jour", color = BlueDark, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(14.dp))
            Text(
                title,
                color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold,
                lineHeight = 30.sp,
                modifier = Modifier.fillMaxWidth(0.62f)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text,
                color = Color.White.copy(alpha = 0.95f), fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth(0.62f)
            )
        }
    }
}

@Composable
private fun CategoryChip(cat: TipCategory, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(50)
    Row(
        Modifier
            .clip(shape)
            .background(if (selected) BlueDark else Color(0xFFE8EEF7))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            cat.icon, null,
            tint = if (selected) Color.White else BlueDark,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            cat.label,
            color = if (selected) Color.White else Navy,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun TipCard(tip: Tip, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(BlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(tip.icon, null, tint = BlueDark, modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(tip.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Navy)
                Spacer(Modifier.height(2.dp))
                Text(tip.short, fontSize = 14.sp, color = Color.Gray)
            }
            Spacer(Modifier.width(8.dp))
            Box(
                Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(BlueSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.ChevronRight, null, tint = BlueDark)
            }
        }
    }
}

@Composable
private fun ResourceCard(
    modifier: Modifier,
    icon: ImageVector,
    tag: String,
    title: String,
    duration: String,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
    ) {
        Row(
            Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .size(62.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(BlueDark, BlueMid))),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(10.dp))
            Column {
                Text(tag, color = BlueMid, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text(
                    title,
                    fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Navy,
                    maxLines = 2, lineHeight = 17.sp
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Filled.Schedule, null,
                        tint = Color.Gray, modifier = Modifier.size(13.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(duration, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

private fun shareText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Partager"))
}

private fun openUrl(context: Context, url: String) {
    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
}