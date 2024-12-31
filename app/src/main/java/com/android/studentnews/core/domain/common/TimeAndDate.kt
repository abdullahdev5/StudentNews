package com.android.studentnews.core.domain.common

import android.icu.util.Calendar
import android.text.format.DateUtils
import androidx.compose.runtime.Composable
import java.sql.Time
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit


// Date Related
inline fun formatDateToString(dateMillis: Long): String {
    val date = Date(dateMillis)
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    return dateFormat.format(date)
}

inline fun formatDateToDay(dateMillis: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateMillis
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    return day
}

inline fun formatDateToMonthName(dateMillis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateMillis
    val monthName = SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
    return monthName
}

inline fun formatDateToMonthInt(dateMillis: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateMillis
    val monthInt = calendar.get(Calendar.MONTH)
    return monthInt
}

inline fun formatDateToYear(dateMillis: Long): Int {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = dateMillis
    val year = calendar.get(Calendar.YEAR)
    return year
}


//  Time Related

inline fun formatTimeToStringFromTimeMillis(timeMillis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeMillis
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return timeFormat.format(calendar.time)
}

inline fun formatTimeToString(hour: Int, minutes: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    calendar.set(Calendar.MINUTE, minutes)
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return timeFormat.format(calendar.time)
}

inline fun formatTimeToHour(hour: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.HOUR_OF_DAY, hour)
    val hours = SimpleDateFormat("hh", Locale.getDefault()).format(calendar.time)
    return hours
}

inline fun formatTimeToMinutes(minutes: Int): String {
    val calendar = Calendar.getInstance()
    calendar.set(Calendar.MINUTE, minutes)
    val minutes = SimpleDateFormat("mm", Locale.getDefault()).format(calendar.time)
    return minutes
}


// Both

inline fun formatDateOrTimeToAgo(
    date: Date,
): CharSequence {
    val now = System.currentTimeMillis()
    val currentCalendar = Calendar.getInstance()
    currentCalendar.timeInMillis = now

//    var convTime: String? = null;
//
//    val prefix = "";
//    val suffix = "ago";
//
//    try {
//        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        var pasTime = dateFormat.parse(date.toString());
//
//
//        val dateDiff = time - pasTime.time;
//
//        var second = TimeUnit.MILLISECONDS.toSeconds(dateDiff);
//        var minute = TimeUnit.MILLISECONDS.toMinutes(dateDiff);
//        var hour   = TimeUnit.MILLISECONDS.toHours(dateDiff);
//        var day  = TimeUnit.MILLISECONDS.toDays(dateDiff);
//
//        if (second < 60) {
//            convTime = "$second seconds $suffix"
//        } else if (minute < 60) {
//            convTime = "$minute minutes $suffix"
//        } else if (hour < 24) {
//            convTime = "$hour hours $suffix"
//        } else if (day >= 7) {
//            if (day > 360) {
//                convTime = "${(day / 360)} years $suffix"
//            } else if (day > 30) {
//                convTime = "${(day / 30)} months $suffix"
//            } else {
//                convTime = "${(day / 7)} week $suffix"
//            }
//        } else if (day < 7) {
//            convTime = "$day days $suffix"
//        }
//
//    } catch (e: ParseException) {
//        e.printStackTrace();
//    }
//
//
//    return convTime.toString()

    val dateChar = DateUtils.getRelativeTimeSpanString(
        date.time,

        now,

        if (date.month == currentCalendar.time.month)
            DateUtils.MINUTE_IN_MILLIS
        else if (date.month != currentCalendar.time.month)
            DateUtils.WEEK_IN_MILLIS
        else if (
            date.year == currentCalendar.time.year
            && date.month == currentCalendar.time.month
        )
            DateUtils.YEAR_IN_MILLIS
        else if (date.day == currentCalendar.time.day)
            DateUtils.MINUTE_IN_MILLIS
        else DateUtils.DAY_IN_MILLIS
    )

    return dateChar ?: ""
}