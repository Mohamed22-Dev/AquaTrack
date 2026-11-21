package com.example.watertrack.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.watertrack.MainActivity
import com.example.watertrack.WaterTrackApp
import com.example.watertrack.data.AppPreferences
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val ctx = applicationContext
        val isTest = inputData.getBoolean(KEY_TEST, false)

        if (Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) return Result.success()

        val prefs = AppPreferences(ctx)

        // Plage horaire choisie par l'utilisateur
        val start = prefs.reminderStart.first()
        val end = prefs.reminderEnd.first()
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val inWindow = if (start <= end) hour in start until end else (hour >= start || hour < end)
        if (!isTest && !inWindow) return Result.success()

        val app = ctx as WaterTrackApp
        val userId = prefs.userId.first()
        if (userId == -1L) return Result.success()

        val user = app.repository.observeUser(userId).first() ?: return Result.success()
        if (!isTest && !user.notificationsEnabled) return Result.success()

        val drunk = app.repository.todayTotal(userId).first()
        val remaining = user.dailyGoalMl - drunk
        if (!isTest && remaining <= 0) return Result.success()

        val text = if (remaining > 0) {
            "Il vous reste $remaining ml pour atteindre votre objectif."
        } else {
            "Objectif atteint, bravo !"
        }
        showNotification(ctx, text)
        return Result.success()
    }

    private fun showNotification(ctx: Context, text: String) {
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= 26) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID, "Rappels d'hydratation", NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }
        val intent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pending = PendingIntent.getActivity(
            ctx, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notification = NotificationCompat.Builder(ctx, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("C'est l'heure de boire !")
            .setContentText(text)
            .setContentIntent(pending)
            .setAutoCancel(true)
            .build()
        nm.notify(1001, notification)
    }

    companion object {
        const val CHANNEL_ID = "water_reminders"
        const val KEY_TEST = "is_test"
    }
}

object ReminderScheduler {
    private const val WORK_NAME = "water_reminder"

    fun schedule(context: Context, hours: Int) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(hours.toLong(), TimeUnit.HOURS)
            .setInitialDelay(hours.toLong(), TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME, ExistingPeriodicWorkPolicy.UPDATE, request
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    fun sendTest(context: Context) {
        val request = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(workDataOf(ReminderWorker.KEY_TEST to true))
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}