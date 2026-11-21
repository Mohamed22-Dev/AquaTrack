package com.example.watertrack.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.watertrack.WaterTrackApp
import com.example.watertrack.navigation.Tab
import com.example.watertrack.ui.theme.White

private val BarTop = Color(0xFF7FD0FB)
private val BarBottom = Color(0xFF5FB4F8)
private val AddButtonColor = Color(0xFF0B4DA8)

/** Sous-pages du compte (le bouton "Compte" reste allume) */
object AccountRoutes {
    const val STATS = "stats"
    const val HISTORY = "history"
    const val FAVORITES = "favorites"
    const val ABOUT = "about"
    const val SETTINGS = "settings"

    val all = setOf(STATS, HISTORY, FAVORITES, ABOUT, SETTINGS)
}

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val app = LocalContext.current.applicationContext as WaterTrackApp
    val vm: MainViewModel = viewModel(factory = MainViewModelFactory(app))

    val tabsNav = rememberNavController()
    val backStack by tabsNav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val highlightRoute =
        if (currentRoute in AccountRoutes.all) Tab.Account.route else currentRoute

    val cup by vm.cupMl.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    fun goTab(tab: Tab) {
        tabsNav.navigate(tab.route) {
            popUpTo(tabsNav.graph.findStartDestination().id)
            launchSingleTop = true
        }
    }

    Scaffold(
        bottomBar = {
            FancyBottomBar(
                currentRoute = highlightRoute,
                onTab = { tab -> goTab(tab) },
                onAdd = { showAdd = true }
            )
        }
    ) { padding ->
        NavHost(
            navController = tabsNav,
            startDestination = Tab.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Tab.Dashboard.route) { DashboardScreen(vm) }
            composable(Tab.Goals.route) { GoalsScreen(vm) }
            composable(Tab.Tips.route) { TipsScreen(vm) }
            composable(Tab.Account.route) {
                AccountScreen(
                    vm = vm,
                    onStats = { tabsNav.navigate(AccountRoutes.STATS) },
                    onGoals = { goTab(Tab.Goals) },
                    onHistory = { tabsNav.navigate(AccountRoutes.HISTORY) },
                    onFavorites = { tabsNav.navigate(AccountRoutes.FAVORITES) },
                    onAbout = { tabsNav.navigate(AccountRoutes.ABOUT) },
                    onSettings = { tabsNav.navigate(AccountRoutes.SETTINGS) },
                    onLogout = onLogout
                )
            }
            composable(AccountRoutes.STATS) {
                StatsScreen(vm) { tabsNav.popBackStack() }
            }
            composable(AccountRoutes.HISTORY) {
                HistoryScreen(vm) { tabsNav.popBackStack() }
            }
            composable(AccountRoutes.FAVORITES) {
                FavoritesScreen(vm) { tabsNav.popBackStack() }
            }
            composable(AccountRoutes.ABOUT) {
                AboutScreen { tabsNav.popBackStack() }
            }
            composable(AccountRoutes.SETTINGS) {
                SettingsScreen(vm, onBack = { tabsNav.popBackStack() }, onLogout = onLogout)
            }
        }
    }

    if (showAdd) {
        AddWaterDialog(
            cup = cup,
            onDismiss = { showAdd = false },
            onAdd = { ml ->
                vm.addWater(ml)
                showAdd = false
            }
        )
    }
}

/** Forme de la barre : coins arrondis + creux au centre */
private class NotchedBarShape(
    private val corner: Dp,
    private val notchHalfWidth: Dp,
    private val notchDepth: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val r = with(density) { corner.toPx() }
        val nw = with(density) { notchHalfWidth.toPx() }
        val nd = with(density) { notchDepth.toPx() }
        val cx = size.width / 2f

        val path = Path().apply {
            moveTo(0f, r)
            quadraticBezierTo(0f, 0f, r, 0f)
            lineTo(cx - nw * 1.6f, 0f)
            cubicTo(cx - nw * 0.9f, 0f, cx - nw * 0.9f, nd, cx, nd)
            cubicTo(cx + nw * 0.9f, nd, cx + nw * 0.9f, 0f, cx + nw * 1.6f, 0f)
            lineTo(size.width - r, 0f)
            quadraticBezierTo(size.width, 0f, size.width, r)
            lineTo(size.width, size.height - r)
            quadraticBezierTo(size.width, size.height, size.width - r, size.height)
            lineTo(r, size.height)
            quadraticBezierTo(0f, size.height, 0f, size.height - r)
            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
private fun FancyBottomBar(
    currentRoute: String?,
    onTab: (Tab) -> Unit,
    onAdd: () -> Unit
) {
    val barShape = remember {
        NotchedBarShape(corner = 26.dp, notchHalfWidth = 42.dp, notchDepth = 38.dp)
    }

    Box(
        Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp)
            .height(102.dp)
    ) {
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(68.dp)
                .background(Brush.verticalGradient(listOf(BarTop, BarBottom)), barShape),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BarItem(Tab.Dashboard, currentRoute == Tab.Dashboard.route, { onTab(Tab.Dashboard) }, Modifier.weight(1f))
            BarItem(Tab.Goals, currentRoute == Tab.Goals.route, { onTab(Tab.Goals) }, Modifier.weight(1f))
            Spacer(Modifier.weight(1f))
            BarItem(Tab.Tips, currentRoute == Tab.Tips.route, { onTab(Tab.Tips) }, Modifier.weight(1f))
            BarItem(Tab.Account, currentRoute == Tab.Account.route, { onTab(Tab.Account) }, Modifier.weight(1f))
        }

        Box(
            Modifier
                .align(Alignment.TopCenter)
                .size(68.dp)
                .shadow(10.dp, CircleShape)
                .background(White, CircleShape)
                .padding(5.dp)
                .clip(CircleShape)
                .background(AddButtonColor)
                .clickable(onClick = onAdd),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.WaterDrop,
                contentDescription = "Ajouter de l'eau",
                tint = White,
                modifier = Modifier.size(30.dp)
            )
            Icon(
                Icons.Filled.Add,
                contentDescription = null,
                tint = White,
                modifier = Modifier
                    .size(15.dp)
                    .offset(x = 13.dp, y = 11.dp)
            )
        }
    }
}

@Composable
private fun BarItem(
    tab: Tab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            tab.icon,
            contentDescription = stringResource(tab.label),
            tint = White.copy(alpha = if (selected) 1f else 0.65f),
            modifier = Modifier.size(if (selected) 30.dp else 26.dp)
        )
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .size(5.dp)
                .clip(CircleShape)
                .background(if (selected) White else Color.Transparent)
        )
    }
}

@Composable
private fun AddWaterDialog(cup: Int, onDismiss: () -> Unit, onAdd: (Int) -> Unit) {
    var custom by remember { mutableStateOf("") }
    val value = custom.toIntOrNull()
    val valid = value != null && value in 1..5000
    val quick = remember(cup) { listOf(cup, 100, 500).distinct().sorted() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter de l'eau") },
        text = {
            Column {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    quick.forEach { ml ->
                        FilledTonalButton(
                            onClick = { onAdd(ml) },
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) { Text("+$ml ml", fontSize = 13.sp) }
                    }
                }
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = custom,
                    onValueChange = { custom = it.filter(Char::isDigit).take(4) },
                    label = { Text("Autre quantité (ml)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { value?.let(onAdd) }, enabled = valid) { Text("Ajouter") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}