package com.example.watertrack.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class TipCategory(val label: String, val icon: ImageVector) {
    ALL("Tous", Icons.Filled.Star),
    HEALTH("Santé", Icons.Filled.Favorite),
    LIFESTYLE("Lifestyle", Icons.Filled.DirectionsRun),
    DAILY("Quotidien", Icons.Filled.WbSunny)
}

data class Tip(
    val id: String,
    val category: TipCategory,
    val icon: ImageVector,
    val title: String,
    val short: String,
    val detail: String
)

val allTips = listOf(
    Tip(
        "t1", TipCategory.HEALTH, Icons.Filled.Favorite,
        "Restez en bonne santé",
        "Boire de l'eau renforce votre système immunitaire.",
        "Une bonne hydratation aide le corps à éliminer les toxines, protège les reins et soutient les défenses naturelles."
    ),
    Tip(
        "t2", TipCategory.LIFESTYLE, Icons.Filled.DirectionsRun,
        "Pendant le sport",
        "Hydratez-vous avant, pendant et après l'effort.",
        "Buvez un verre d'eau avant l'effort, quelques gorgées toutes les 15 à 20 minutes pendant, et réhydratez-vous après."
    ),
    Tip(
        "t3", TipCategory.DAILY, Icons.Filled.WbSunny,
        "Par temps chaud",
        "Augmentez votre consommation d'eau quand il fait chaud.",
        "La transpiration augmente avec la chaleur. Pensez à boire même sans soif, surtout en été."
    ),
    Tip(
        "t4", TipCategory.DAILY, Icons.Filled.NightsStay,
        "Avant de dormir",
        "Un verre d'eau peut prévenir la déshydratation nocturne.",
        "Un petit verre avant le coucher aide, mais évitez d'en boire trop pour ne pas perturber votre sommeil."
    ),
    Tip(
        "t5", TipCategory.HEALTH, Icons.Filled.Restaurant,
        "Aliments riches en eau",
        "Fruits et légumes contribuent à votre hydratation.",
        "Concombre, pastèque, orange et tomate contiennent plus de 85 % d'eau."
    ),
    Tip(
        "t6", TipCategory.HEALTH, Icons.Filled.Block,
        "Évitez les boissons sucrées",
        "Préférez l'eau aux sodas et jus industriels.",
        "Les boissons sucrées apportent des calories inutiles. L'eau reste la meilleure boisson au quotidien."
    ),
    Tip(
        "t7", TipCategory.LIFESTYLE, Icons.Filled.LocalDrink,
        "Gardez une bouteille près de vous",
        "Avoir de l'eau à portée de main aide à boire plus souvent.",
        "Placez une bouteille sur votre bureau ou dans votre sac pour y penser tout au long de la journée."
    ),
    Tip(
        "t8", TipCategory.DAILY, Icons.Filled.Alarm,
        "Au réveil",
        "Commencez la journée par un grand verre d'eau.",
        "Après la nuit, le corps est légèrement déshydraté. Un verre d'eau au réveil relance le métabolisme."
    )
)