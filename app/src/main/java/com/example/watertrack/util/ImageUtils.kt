package com.example.watertrack.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.File

object ImageUtils {

    /** Charge une image en reduisant sa taille pour economiser la memoire */
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

    /** Copie la photo choisie dans le stockage prive de l'application */
    fun saveProfilePhoto(context: Context, uri: Uri, userId: Long): String? {
        val bitmap = decodeSampled(context, uri, 512) ?: return null
        val file = File(context.filesDir, "profile_$userId.jpg")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 88, it) }
        return file.absolutePath
    }

    fun loadFile(path: String): Bitmap? = BitmapFactory.decodeFile(path)
}