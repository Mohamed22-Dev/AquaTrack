package com.example.watertrack.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

object ImageUtils {

    fun decodeSampled(context: Context, uri: Uri, maxSize: Int): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, bounds)
        }
        var sample = 1
        while (bounds.outWidth / sample > maxSize * 2 || bounds.outHeight / sample > maxSize * 2) {
            sample *= 2
        }
        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        return context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        }
    }

    /** Enregistre la photo avec un nom unique (pour que l'ecran se mette a jour) */
    fun saveProfilePhoto(context: Context, uri: Uri, userId: Long): String? {
        val bitmap = decodeSampled(context, uri, 512) ?: return null
        context.filesDir.listFiles { f -> f.name.startsWith("profile_${userId}_") }
            ?.forEach { it.delete() }
        val file = File(context.filesDir, "profile_${userId}_${System.currentTimeMillis()}.jpg")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 88, it) }
        return file.absolutePath
    }

    fun loadFile(path: String): Bitmap? = BitmapFactory.decodeFile(path)
}