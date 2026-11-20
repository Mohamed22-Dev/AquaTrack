package com.example.watertrack.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.watertrack.WaterTrackApp
import com.example.watertrack.data.AppPreferences
import com.example.watertrack.data.GoalEntity
import com.example.watertrack.data.Repository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val repo: Repository,
    private val prefs: AppPreferences
) : ViewModel() {

    private val userId = prefs.userId

    val todayMl: StateFlow<Int> = userId
        .flatMapLatest { repo.todayTotal(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val goalMl: StateFlow<Int> = userId
        .flatMapLatest { repo.observeUser(it) }
        .map { it?.dailyGoalMl ?: 2000 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 2000)

    val cupMl: StateFlow<Int> = prefs.cupMl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 250)

    val goalHistory: StateFlow<List<GoalEntity>> = userId
        .flatMapLatest { repo.goalHistory(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val weekData: StateFlow<List<Pair<String, Float>>> = userId
        .flatMapLatest { repo.weekTotals(it) }
        .map { totals ->
            val byDay = totals.associate { it.day to it.total }
            val dayFmt = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val labelFmt = SimpleDateFormat("EEE", Locale.FRENCH)
            val cal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -6) }
            (0..6).map {
                val d = cal.time
                val item = labelFmt.format(d).replaceFirstChar { c -> c.uppercase() } to
                        ((byDay[dayFmt.format(d)] ?: 0) / 1000f)
                cal.add(Calendar.DAY_OF_YEAR, 1)
                item
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            combine(todayMl, goalMl) { drunk, goal -> drunk to goal }
                .collect { (drunk, goal) ->
                    val id = userId.first()
                    if (id != -1L) repo.syncTodayGoal(id, goal, drunk)
                }
        }
    }

    fun addWater(ml: Int) {
        viewModelScope.launch {
            val id = userId.first()
            if (id != -1L && ml > 0) repo.addWater(id, ml)
        }
    }

    fun addCup() = addWater(cupMl.value)

    fun undoLast() {
        viewModelScope.launch {
            val id = userId.first()
            if (id != -1L) repo.undoLast(id)
        }
    }

    fun setCupMl(ml: Int) {
        viewModelScope.launch {
            if (ml in 50..2000) prefs.setCupMl(ml)
        }
    }

    fun setDailyGoal(ml: Int) {
        viewModelScope.launch {
            val id = userId.first()
            if (id != -1L && ml in 500..10000) repo.updateDailyGoal(id, ml)
        }
    }
}

class MainViewModelFactory(private val app: WaterTrackApp) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        MainViewModel(app.repository, AppPreferences(app)) as T
}