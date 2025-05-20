package com.universal.fiestamas.presentation.utils.extensions

import android.annotation.SuppressLint
import com.google.firebase.Timestamp
import com.universal.fiestamas.domain.models.LocalTimestamp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone

fun timestampToLocalDateTime(timestamp: Timestamp?): LocalDateTime {
    if (timestamp == null) return LocalDateTime.now()
    val timeZone = TimeZone.getTimeZone("UTC")
    val instant = Instant.ofEpochSecond(timestamp.seconds, timestamp.nanoseconds.toLong())
    return LocalDateTime.ofInstant(instant, timeZone.toZoneId()) // ZoneId.systemDefault())
}

fun timestampToLocalDate(timestamp: Timestamp?): LocalDate {
    if (timestamp == null) return LocalDate.now()
    val instant = Instant.ofEpochSecond(timestamp.seconds, timestamp.nanoseconds.toLong())
    return instant.atZone(ZoneId.systemDefault()).toLocalDate()
}

@Suppress("unused")
fun stringToTimestamp(dateString: String?): Timestamp {
    if (dateString.isNullOrEmpty()) {
        return Timestamp.now()
    }
    try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateString)
        if (date != null) {
            return Timestamp(date)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return Timestamp.now()
}


@SuppressLint("SimpleDateFormat")
fun stringToISO8601(inputDate: String): String {
    val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm")
    val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    val dateTime: LocalDateTime = LocalDateTime.parse(inputDate, inputFormat)
    val timeZone = ZoneId.systemDefault()
    val zonedDateTime = dateTime.atZone(timeZone)
    return outputFormat.format(zonedDateTime)
}

fun daysUntilDate(targetTimestamp: Timestamp?): Int {
    if (targetTimestamp == null) return 0
    val currentDate = LocalDate.now()
    val targetDate = targetTimestamp.toDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val differenceInDays = ChronoUnit.DAYS.between(currentDate, targetDate)
    return differenceInDays.toInt()
}

fun convertTimestampToDate(timestamp: Timestamp?): Pair<String, String> {
    if (timestamp == null) return Pair("", "")
    val date = timestamp.toDate()
    val dateFormat = SimpleDateFormat("dd-MM-yy", Locale.getDefault())
    val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
    val dateString = dateFormat.format(date)
    val weekDayString = dayOfWeekFormat.format(date)
    return Pair(dateString, weekDayString)
}

fun convertTimestampToDateYYYYmmDD(timestamp: Timestamp?): Pair<String, String> {
    if (timestamp == null) return Pair("", "")
    val date = timestamp.toDate()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
    val dateString = dateFormat.format(date)
    val weekDayString = dayOfWeekFormat.format(date)
    return Pair(dateString, weekDayString)
}

fun convertTimestampToDateYYYYmmDD(timestamp: LocalTimestamp?): Pair<String, String> {
    if (timestamp == null) return Pair("", "")
    val date = timestamp.toDate()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale("es", "ES"))
    val dateString = dateFormat.format(date)
    val weekDayString = dayOfWeekFormat.format(date)
    return Pair(dateString, weekDayString)
}


fun convertTimestampToDateAndHour(timestamp: Timestamp?): Pair<String, String> {
    if (timestamp == null) return Pair("", "")
    val timeZone = TimeZone.getDefault()
    val date = timestamp.toDate()
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm 'hrs'", Locale.getDefault())
    dateFormatter.timeZone = timeZone
    timeFormatter.timeZone = timeZone
    val formattedDate = dateFormatter.format(date)
    val formattedTime = timeFormatter.format(date)
    return Pair(formattedDate, formattedTime)
}

fun convertTimestampToDateAndHourUTC(timestamp: Timestamp?): Pair<String, String> {
    if (timestamp == null) return Pair("", "")
    val timeZone = TimeZone.getTimeZone("UTC")
    val date = timestamp.toDate()
    val dateFormatter = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val timeFormatter = SimpleDateFormat("HH:mm 'hrs'", Locale.getDefault())
    dateFormatter.timeZone = timeZone
    timeFormatter.timeZone = timeZone
    val formattedDate = dateFormatter.format(date)
    val formattedTime = timeFormatter.format(date)
    return Pair(formattedDate, formattedTime)
}

fun Timestamp.toMilliSeconds(): Long {
    return this.seconds * 1000 + this.nanoseconds / 1_000_000
}


fun isDateEndPriorDateStart(dateStart: String, dateEnd: String): Boolean {
    if (dateStart.isBlank() || dateEnd.isBlank()) return false

    val timeStampStart = stringToTimestamp(dateStart)
    val timeStampEnd = stringToTimestamp(dateEnd)

    return timeStampEnd < timeStampStart
}

fun formatStringAsV2ForPromos(date: String): String {
    val formattedDate = date
        .replace("-", "/")
        .replace("2024", "24")
        .replace("2025", "25")
    return formattedDate
}

fun formatStringAsDateV2ForPromos(date: String): String {
    val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val parsedDate = LocalDate.parse(date, inputFormatter)
    val formattedDate = parsedDate.format(outputFormatter)

    return formattedDate
}

fun splitStringDate(dateString: String): Triple<Int, Int, Int>? {
    // validate correct format 2024-07-23
    val regex = Regex("""^(\d{4})-(\d{2})-(\d{2})$""")
    val matchResult = regex.matchEntire(dateString)

    return if (matchResult != null) {
        val (year, month, day) = matchResult.destructured
        Triple(year.toInt(), month.toInt()-1, day.toInt())
    } else {
        null // null if invalid format
    }
}

fun formatStringToDDMMYYYY(date: String): String {
    // receives: "2024-12-23"
    return try {
        val parts = date.split("-")
        if (parts.size == 3) {
            // returns: "23-12-2024"
            "${parts[2]}-${parts[1]}-${parts[0]}"
        } else {
            throw IllegalArgumentException("Invalid date format")
        }
    } catch (e: Exception) {
        "Invalid date"
    }
}