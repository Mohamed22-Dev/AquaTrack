package com.example.watertrack.util

import java.security.MessageDigest
import java.util.Calendar

object Security {
    fun sha256(input: String): String =
        MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }

    /** true إذا كان العمر 18 سنة أو أكثر */
    fun isAdult(birthMillis: Long): Boolean {
        val limit = Calendar.getInstance().apply { add(Calendar.YEAR, -18) }.timeInMillis
        return birthMillis <= limit
    }
}