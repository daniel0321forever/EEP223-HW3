package com.example.hw3.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateTimeUtils {

    private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    fun formatUnixTime(unixSeconds: Long): String {
        return timeFormat.format(Date(unixSeconds * 1000L))
    }
}
