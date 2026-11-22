package com.example.watertrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watertrack.ui.theme.BlueGradient
import com.example.watertrack.ui.theme.White

val AppNavy: Color
    @Composable
    get() = if (MaterialTheme.colorScheme.background == White || MaterialTheme.colorScheme.background == Color(0xFFF4F8FF)) Color(0xFF0B2A5B) else Color(0xFFEAF2FF)

val AppBlueDark = Color(0xFF0B5ED7)
val AppBlueMid = Color(0xFF3B8FF0)

val AppScreenBg: Color
    @Composable
    get() = MaterialTheme.colorScheme.background

val AppTextGray: Color
    @Composable
    get() = if (MaterialTheme.colorScheme.background == White || MaterialTheme.colorScheme.background == Color(0xFFF4F8FF)) Color(0xFF5A6A85) else Color(0xFFA0B2CE)

/** En-tete des sous-pages : fleche retour + titre centre */
@Composable
fun SubScreenHeader(title: String, onBack: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .background(BlueGradient)
            .statusBarsPadding()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = White)
        }
        Text(title, color = White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun EmptyState(emoji: String, title: String, text: String) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(emoji, fontSize = 64.sp)
        Spacer(Modifier.height(12.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppNavy)
        Spacer(Modifier.height(6.dp))
        Text(text, color = AppTextGray, textAlign = TextAlign.Center)
    }
}

@Composable
fun LogoutDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Déconnexion") },
        text = { Text("Voulez-vous vraiment vous déconnecter ?") },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Se déconnecter", color = Color(0xFFE53935)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}
