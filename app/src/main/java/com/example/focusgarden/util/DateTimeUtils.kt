package com.example.focusgarden.util

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateTimeUtils {
    fun startOfTodayMillis(): Long = LocalDate.now()
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

    fun formatSeconds(seconds: Long): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        return if (hours > 0) "${hours}ч ${minutes}м" else "${minutes}м"
    }

    fun formatTimer(seconds: Int): String {
        val safe = seconds.coerceAtLeast(0)
        val minutes = safe / 60
        val secs = safe % 60
        return "%02d:%02d".format(minutes, secs)
    }

    fun formatDateTime(millis: Long): String {
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale("ru"))
        return Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).format(formatter)
    }
}
