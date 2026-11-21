package com.example.watertrack.ui.screens

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.watertrack.WaterTrackApp

private val Red = Color(0xFFE53935)

@Composable
fun SettingsScreen(vm: MainViewModel, onBack: () -> Unit, onLogout: () -> Unit) {
    val context = LocalContext.current
    val app = context.applicationContext as WaterTrackApp
    val sv: SettingsViewModel = viewModel(factory = SettingsViewModelFactory(app))

    val user by vm.user.collectAsStateWithLifecycle()
    val interval by vm.reminderHours.collectAsStateWithLifecycle()
    val start by sv.reminderStart.collectAsStateWithLifecycle()
    val end by sv.reminderEnd.collectAsStateWithLifecycle()
    val language by sv.language.collectAsStateWithLifecycle()
    val dark by sv.darkMode.collectAsStateWithLifecycle()

    var showHours by remember { mutableStateOf(false) }
    var showLanguage by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    var showPrivacy by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }
    var showAbout by remember { mutableStateOf(false) }
    var showLogout by remember { mutableStateOf(false) }
    var pendingTest by remember { mutableStateOf(false) }

    fun hasPermission(): Boolean =
        Build.VERSION.SDK_INT < 33 ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED

    val permLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            vm.setNotifications(true)
            if (pendingTest) vm.sendTestReminder()
        } else {
            Toast.makeText(
                context, "Autorisez les notifications dans les réglages du téléphone",
                Toast.LENGTH_LONG
            ).show()
        }
        pendingTest = false
    }

    val enabled = user?.notificationsEnabled == true
    val langName = languageName(language)

    Column(
        Modifier
            .fillMaxSize()
            .background(AppScreenBg)
    ) {
        // ---- En-tete ----
        Box(
            Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(Color(0xFFBFE0FF), AppScreenBg)))
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(46.dp)
                        .shadow(3.dp, CircleShape)
                        .background(Color.White, CircleShape)
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.ArrowBack, "Retour", tint = AppBlueDark)
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        "Paramètres",
                        fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = AppNavy
                    )
                    Text(
                        "Personnalisez votre expérience",
                        fontSize = 13.sp, color = AppTextGray
                    )
                }
                Icon(
                    Icons.Filled.Settings, null,
                    tint = AppBlueDark, modifier = Modifier.size(64.dp)
                )
            }
        }

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // ---- Carte application ----
            SettingsCard {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { showAbout = true }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AppBlueDark, AppNavy))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.WaterDrop, null,
                            tint = Color.White, modifier = Modifier.size(34.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "WaterTrack",
                            fontSize = 22.sp, fontWeight = FontWeight.Bold, color = AppNavy
                        )
                        Text("Version 1.0.0", color = AppTextGray)
                    }
                    Icon(Icons.Filled.ChevronRight, null, tint = AppBlueDark)
                }
            }

            // ---- Notifications ----
            SectionTitle(Icons.Filled.Notifications, "Notifications")
            SettingsCard {
                SettingRow(
                    icon = Icons.Filled.Notifications,
                    title = "Rappels d'hydratation",
                    subtitle = "Recevoir des notifications",
                    trailing = {
                        Switch(
                            checked = enabled,
                            onCheckedChange = { on ->
                                if (!on) {
                                    vm.setNotifications(false)
                                } else if (hasPermission()) {
                                    vm.setNotifications(true)
                                } else {
                                    permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                        )
                    }
                )
                RowDivider()
                SettingRow(
                    icon = Icons.Filled.Schedule,
                    title = "Heure des rappels",
                    subtitle = "%02d:00 – %02d:00  (toutes les %d h)".format(start, end, interval),
                    onClick = { showHours = true }
                )
                if (enabled) {
                    RowDivider()
                    SettingRow(
                        icon = Icons.Filled.NotificationsActive,
                        title = "Envoyer un rappel de test",
                        subtitle = "Vérifier que les notifications fonctionnent",
                        onClick = {
                            if (hasPermission()) {
                                vm.sendTestReminder()
                            } else {
                                pendingTest = true
                                permLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    )
                }
            }

            // ---- Langue et region ----
            SectionTitle(Icons.Filled.Language, "Langue et région")
            SettingsCard {
                SettingRow(
                    icon = Icons.Filled.Language,
                    title = "Langue",
                    subtitle = langName,
                    onClick = { showLanguage = true }
                )
                RowDivider()
                SettingRow(
                    icon = Icons.Filled.DarkMode,
                    title = "Mode sombre",
                    subtitle = "Activer le mode sombre",
                    trailing = {
                        Switch(checked = dark, onCheckedChange = { sv.setDarkMode(it) })
                    }
                )
            }

            // ---- Confidentialite et securite ----
            SectionTitle(Icons.Filled.Security, "Confidentialité et sécurité")
            SettingsCard {
                SettingRow(
                    icon = Icons.Filled.Lock,
                    title = "Changer le mot de passe",
                    onClick = { showPassword = true }
                )
                RowDivider()
                SettingRow(
                    icon = Icons.Filled.VerifiedUser,
                    title = "Politique de confidentialité",
                    onClick = { showPrivacy = true }
                )
                RowDivider()
                SettingRow(
                    icon = Icons.Filled.Description,
                    title = "Conditions d'utilisation",
                    onClick = { showTerms = true }
                )
            }

            // ---- A propos ----
            SectionTitle(Icons.Filled.Info, "À propos")
            SettingsCard {
                SettingRow(
                    icon = Icons.Filled.Star,
                    title = "Noter l'application",
                    onClick = { rateApp(context) }
                )
                RowDivider()
                SettingRow(
                    icon = Icons.Filled.Info,
                    title = "À propos de WaterTrack",
                    onClick = { showAbout = true }
                )
            }

            // ---- Deconnexion ----
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(Red.copy(alpha = 0.12f))
                    .clickable { showLogout = true }
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Logout, null, tint = Red)
                Spacer(Modifier.width(10.dp))
                Text("Se déconnecter", color = Red, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // ---- Dialogues ----
    if (showHours) {
        HoursDialog(
            start = start,
            end = end,
            interval = interval,
            onIntervalChange = { vm.setReminderHours(it) },
            onDismiss = { showHours = false },
            onSave = { s, e ->
                sv.setWindow(s, e)
                showHours = false
            }
        )
    }

    if (showLanguage) {
        LanguageDialog(
            selected = language,
            onDismiss = { showLanguage = false },
            onSelect = {
                sv.setLanguage(it)
                showLanguage = false
            }
        )
    }

    if (showPassword) {
        PasswordDialog(sv = sv, onDismiss = { showPassword = false })
    }

    if (showPrivacy) {
        InfoDialog(
            "Politique de confidentialité",
            "Vos données (compte, consommation d'eau, objectifs) sont stockées uniquement " +
                    "sur votre téléphone. Elles ne sont ni envoyées ni partagées avec des tiers.\n\n" +
                    "Votre mot de passe est chiffré et ne peut pas être lu. Vous pouvez " +
                    "supprimer vos données à tout moment en désinstallant l'application.",
            onDismiss = { showPrivacy = false }
        )
    }

    if (showTerms) {
        InfoDialog(
            "Conditions d'utilisation",
            "WaterTrack est destiné aux personnes de 18 ans et plus.\n\n" +
                    "Les conseils proposés sont informatifs et ne remplacent pas l'avis d'un " +
                    "professionnel de santé. Adaptez votre consommation d'eau à votre situation.",
            onDismiss = { showTerms = false }
        )
    }

    if (showAbout) {
        InfoDialog(
            "À propos de WaterTrack",
            "Version 1.0.0\n\nWaterTrack vous aide à adopter une meilleure routine " +
                    "d'hydratation : suivi de votre consommation, objectifs, rappels " +
                    "personnalisés et statistiques détaillées.",
            onDismiss = { showAbout = false }
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

/* ---------------------------------------------------------------- */
/*  Composants                                                      */
/* ---------------------------------------------------------------- */

@Composable
private fun SectionTitle(icon: ImageVector, text: String) {
    Row(
        Modifier.padding(start = 4.dp, top = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = AppNavy, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppNavy)
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
private fun RowDivider() {
    HorizontalDivider(
        color = Color(0xFFE8EEF7),
        modifier = Modifier.padding(horizontal = 14.dp)
    )
}

@Composable
private fun SettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: @Composable () -> Unit = {
        Icon(Icons.Filled.ChevronRight, null, tint = AppBlueDark)
    }
) {
    Row(
        Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AppBlueMid.copy(alpha = 0.14f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = AppBlueDark, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = AppNavy)
            if (subtitle != null) {
                Text(subtitle, fontSize = 13.sp, color = AppTextGray)
            }
        }
        trailing()
    }
}

@Composable
private fun HoursDialog(
    start: Int,
    end: Int,
    interval: Int,
    onIntervalChange: (Int) -> Unit,
    onDismiss: () -> Unit,
    onSave: (Int, Int) -> Unit
) {
    var s by remember { mutableIntStateOf(start) }
    var e by remember { mutableIntStateOf(end) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Heure des rappels") },
        text = {
            Column {
                HourStepper("De", s) { s = it }
                HourStepper("À", e) { e = it }
                Spacer(Modifier.height(12.dp))
                Text("Fréquence", color = AppTextGray, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(1, 2, 3, 4).forEach { h ->
                        val selected = h == interval
                        Box(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) AppBlueDark else Color(0xFFE8EEF7))
                                .clickable { onIntervalChange(h) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                "$h h",
                                color = if (selected) Color.White else AppNavy,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = { onSave(s, e) }) { Text("Enregistrer") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@Composable
private fun HourStepper(label: String, value: Int, onChange: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, modifier = Modifier.weight(1f), fontSize = 16.sp)
        IconButton(onClick = { onChange((value + 23) % 24) }) {
            Icon(Icons.Filled.Remove, "Moins")
        }
        Text("%02d:00".format(value), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        IconButton(onClick = { onChange((value + 1) % 24) }) {
            Icon(Icons.Filled.Add, "Plus")
        }
    }
}

private fun languageName(code: String) = when (code) {
    "en" -> "English"
    "ar" -> "العربية"
    else -> "Français"
}

@Composable
private fun LanguageDialog(selected: String, onDismiss: () -> Unit, onSelect: (String) -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Langue") },
        text = {
            Column {
                listOf("fr", "en", "ar").forEach { code ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(code) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = code == selected, onClick = { onSelect(code) })
                        Spacer(Modifier.width(8.dp))
                        Text(languageName(code), fontSize = 17.sp)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    "Le choix est enregistré. La traduction complète des écrans sera appliquée à l'étape Langues.",
                    fontSize = 12.sp, color = AppTextGray
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Fermer") } }
    )
}

@Composable
private fun PasswordDialog(sv: SettingsViewModel, onDismiss: () -> Unit) {
    val context = LocalContext.current
    var old by remember { mutableStateOf("") }
    var new by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Changer le mot de passe") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = old, onValueChange = { old = it },
                    label = { Text("Ancien mot de passe") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = new, onValueChange = { new = it },
                    label = { Text("Nouveau mot de passe") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = confirm, onValueChange = { confirm = it },
                    label = { Text("Confirmer") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                if (error != null) {
                    Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                when {
                    old.isBlank() -> error = "Entrez l'ancien mot de passe"
                    new.length < 6 -> error = "6 caractères minimum"
                    new != confirm -> error = "Les mots de passe ne correspondent pas"
                    else -> {
                        error = null
                        sv.changePassword(old, new) { ok ->
                            if (ok) {
                                Toast.makeText(
                                    context, "Mot de passe modifié", Toast.LENGTH_SHORT
                                ).show()
                                onDismiss()
                            } else {
                                error = "Ancien mot de passe incorrect"
                            }
                        }
                    }
                }
            }) { Text("Modifier") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@Composable
private fun InfoDialog(title: String, text: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Fermer") } }
    )
}

private fun rateApp(context: Context) {
    val pkg = context.packageName
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg")))
    } catch (e: Exception) {
        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$pkg")
                )
            )
        } catch (e2: Exception) {
            Toast.makeText(context, "Le Play Store n'est pas disponible", Toast.LENGTH_SHORT).show()
        }
    }
}