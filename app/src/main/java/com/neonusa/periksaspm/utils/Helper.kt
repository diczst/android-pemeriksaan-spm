package com.neonusa.periksaspm.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object Helper {
    fun getTanggalToday(): String{
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time
        // Format tanggal menjadi "dd MMMM yyyy"
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        val formattedDate = dateFormat.format(currentDate)
        return formattedDate
    }

    fun getTanggalTodayAngkaVer(): String{
        val calendar = Calendar.getInstance()
        val currentDate = calendar.time

        // Format tanggal menjadi "dd-MM-yyyy"
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale("id", "ID"))
        val formattedDate = dateFormat.format(currentDate)
        return formattedDate
    }

    fun convertDate(inputDateStr: String): String {
        // Formatter untuk input date string
        val inputFormatter = SimpleDateFormat("dd-MM-yyyy", Locale("id", "ID"))
        val date: Date = inputFormatter.parse(inputDateStr)
        // Formatter untuk output date string
        val outputFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        // Format the Date object to the desired string
        return outputFormatter.format(date)
    }

    // memecah tanggal
    fun parseDate(date: String): Triple<Int, Int, Int> {
        val parts = date.split("-")
        val day = parts[0].toInt()
        val month = parts[1].toInt()
        val year = parts[2].toInt()
        return Triple(year, month, day)
    }

}