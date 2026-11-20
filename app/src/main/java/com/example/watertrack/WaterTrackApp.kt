package com.example.watertrack

import android.app.Application
import com.example.watertrack.data.AppDatabase
import com.example.watertrack.data.Repository

class WaterTrackApp : Application() {
    val repository by lazy { Repository(AppDatabase.get(this)) }
}