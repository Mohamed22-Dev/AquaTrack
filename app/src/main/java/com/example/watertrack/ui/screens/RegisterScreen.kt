package com.example.watertrack.ui.screens

import android.net.Uri
import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.watertrack.ui.theme.BlueGradient
import com.example.watertrack.ui.theme.BlueLight
import com.example.watertrack.ui.theme.BluePrimary
import com.example.watertrack.ui.theme.White
import com.example.watertrack.util.ImageUtils
import com.example.watertrack.util.Security
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(vm: AuthViewModel, onRegistered: () -> Unit, onBack: () -> Unit) {
    // 0 = identite, 1 = contact, 2 = photo, 3 = felicitations
    var step by rememberSaveable { mutableIntStateOf(0) }
    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var birth by rememberSaveable { mutableStateOf<Long?>(null) }
    var useEmail by rememberSaveable { mutableStateOf(true) }
    var email by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var photoUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var showPicker by remember { mutableStateOf(false) }
    var localError by remember { mutableStateOf<String?>(null) }

    val dateFormat = remember {
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri -> if (uri != null) photoUri = uri }

    // Retour : revient a l'etape precedente
    BackHandler(enabled = step in 1..2) {
        vm.error = null
        localError = null
        step -= 1
    }

    // Si l'inscription echoue (email deja utilise...), retour a l'etape contact
    LaunchedEffect(vm.error) {
        if (vm.error != null && step == 2) step = 1
    }

    fun validate(): String? {
        val b = birth
        return when (step) {
            0 -> when {
                firstName.isBlank() -> "Prénom requis"
                lastName.isBlank() -> "Nom requis"
                b == null -> "Choisissez votre date d'anniversaire"
                !Security.isAdult(b) -> "Vous devez avoir plus de 18 ans"
                else -> null
            }
            1 -> when {
                useEmail && !Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() ->
                    "Email invalide"
                !useEmail && phone.count { it.isDigit() } < 8 ->
                    "Numéro de téléphone invalide"
                password.length < 6 -> "Mot de passe : 6 caractères minimum"
                else -> null
            }
            else -> null
        }
    }

    fun submit(photo: Uri?) {
        val b = birth ?: return
        vm.register(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            email = if (useEmail) email.trim() else "",
            phone = if (useEmail) "" else phone.trim(),
            password = password,
            birthDate = b,
            photoUri = photo,
            onSuccess = { step = 3 }
        )
    }

    if (showPicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    birth = state.selectedDateMillis
                    showPicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) { Text("Annuler") }
            }
        ) { DatePicker(state = state) }
    }

    if (step == 3) {
        CongratsScreen(name = firstName.trim(), onDone = onRegistered)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Retour + indicateur d'etapes
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {
                    vm.error = null
                    localError = null
                    if (step == 0) onBack() else step -= 1
                }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                }
                Spacer(Modifier.width(8.dp))
                StepBars(current = step, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(16.dp))
            }

            AppLogo(Modifier.size(90.dp))
            Spacer(Modifier.height(8.dp))

            val title = when (step) {
                0 -> "Faisons connaissance"
                1 -> "Vos coordonnées"
                else -> "Ajoutez une photo"
            }
            val subtitle = when (step) {
                0 -> "Comment vous appelez-vous ?"
                1 -> "Email ou numéro de téléphone"
                else -> "Vous pouvez aussi le faire plus tard"
            }
            Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
            Spacer(Modifier.height(24.dp))

            when (step) {
                0 -> {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("Prénom") },
                        leadingIcon = { Icon(Icons.Filled.Person, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Nom") },
                        leadingIcon = { Icon(Icons.Filled.Person, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                    Box(Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = birth?.let { dateFormat.format(it) } ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Anniversaire") },
                            leadingIcon = { Icon(Icons.Filled.CalendarMonth, null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Box(
                            Modifier
                                .matchParentSize()
                                .clickable { showPicker = true }
                        )
                    }
                    Text(
                        "Vous devez avoir plus de 18 ans",
                        fontSize = 12.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 4.dp, top = 4.dp)
                    )
                }

                1 -> {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        FilterChip(
                            selected = useEmail,
                            onClick = { useEmail = true },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Filled.Email, null, Modifier.size(18.dp)) }
                        )
                        FilterChip(
                            selected = !useEmail,
                            onClick = { useEmail = false },
                            label = { Text("Téléphone") },
                            leadingIcon = { Icon(Icons.Filled.Phone, null, Modifier.size(18.dp)) }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    if (useEmail) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            leadingIcon = { Icon(Icons.Filled.Email, null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    } else {
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { v ->
                                phone = v.filter { it.isDigit() || it == '+' }.take(15)
                            },
                            label = { Text("Numéro de téléphone") },
                            leadingIcon = { Icon(Icons.Filled.Phone, null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Mot de passe") },
                        leadingIcon = { Icon(Icons.Filled.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                else -> {
                    PhotoPicker(uri = photoUri) {
                        photoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (photoUri == null) "Touchez le cercle pour choisir une photo"
                        else "Touchez le cercle pour changer la photo",
                        textAlign = TextAlign.Center,
                        fontSize = 14.sp
                    )
                }
            }

            val shownError = localError ?: vm.error
            if (shownError != null) {
                Spacer(Modifier.height(10.dp))
                Text(shownError, color = MaterialTheme.colorScheme.error)
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = {
                    vm.error = null
                    val err = validate()
                    localError = err
                    if (err == null) {
                        if (step < 2) step += 1 else submit(photoUri)
                    }
                },
                enabled = !vm.loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp)
            ) {
                if (vm.loading) {
                    CircularProgressIndicator(
                        Modifier.size(22.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(if (step < 2) "Continuer" else "Terminer", fontSize = 16.sp)
                }
            }

            if (step == 2) {
                TextButton(
                    onClick = {
                        photoUri = null
                        vm.error = null
                        submit(null)
                    },
                    enabled = !vm.loading
                ) { Text("Passer") }
            }
        }
    }
}

@Composable
private fun StepBars(current: Int, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { i ->
            Box(
                Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (i <= current) BluePrimary else BlueLight.copy(alpha = 0.5f))
            )
        }
    }
}

@Composable
private fun PhotoPicker(uri: Uri?, onClick: () -> Unit) {
    val bitmap = rememberUriBitmap(uri)
    Box(
        Modifier
            .size(170.dp)
            .clip(CircleShape)
            .background(BluePrimary.copy(alpha = 0.12f))
            .border(3.dp, BluePrimary, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Icon(
                Icons.Filled.AddAPhoto, null,
                tint = BluePrimary, modifier = Modifier.size(60.dp)
            )
        }
    }
}

@Composable
private fun rememberUriBitmap(uri: Uri?): ImageBitmap? {
    val context = LocalContext.current
    val state = produceState<ImageBitmap?>(initialValue = null, uri) {
        value = if (uri == null) null else withContext(Dispatchers.IO) {
            ImageUtils.decodeSampled(context, uri, 600)?.asImageBitmap()
        }
    }
    return state.value
}

/** Ecran de felicitations : visible 2 secondes puis disparait */
@Composable
private fun CongratsScreen(name: String, onDone: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000L)
        onDone()
    }

    val transition = rememberInfiniteTransition(label = "pulse")
    val scale by transition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Box(
        Modifier
            .fillMaxSize()
            .background(BlueGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text("🎉", fontSize = 96.sp, modifier = Modifier.scale(scale))
            Spacer(Modifier.height(16.dp))
            Text(
                "Félicitations !",
                color = White, fontSize = 32.sp, fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                if (name.isNotEmpty()) "$name, votre compte a été créé avec succès"
                else "Votre compte a été créé avec succès",
                color = White, fontSize = 17.sp, textAlign = TextAlign.Center
            )
        }
    }
}