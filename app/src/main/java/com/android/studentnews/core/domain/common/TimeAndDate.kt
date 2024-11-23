package com.android.studentnews.core.domain.common

import android.icu.util.Calendar
import androidx.compose.runtime.Composable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


// Date Related

fun formatDateToString(dateMillis: Long): String {
    val date = Date(dateMillis)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

fun formatDateToDay(dateMillis: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateMillis
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
    val year = calendar.get(Calendar.YEAR)
    return day
}

fun formatDateToMonthName(dateMillis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateMillis
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
    return monthName
}

fun formatDateToYear(dateMillis: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateMillis
    val year = calendar.get(Calendar.YEAR)
    return year
}


//  TIme Related

fun formatTimeToString(hour: Int, minutes: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minutes)
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return timeFormat.format(calendar.time)
}

fun formatTimeToHour(hour: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    val hours = SimpleDateFormat("hh", Locale.getDefault()).format(calendar.time)
    return hours
}

fun formatTimeToMinutes(minutes: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MINUTE, minutes)
    val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(calendar.time)
    return minutes
}