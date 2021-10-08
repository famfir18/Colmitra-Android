package com.softnesia.colmitra.util

import android.text.TextUtils
import android.text.format.DateUtils
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

object LDate {
    fun changeToIndonesianMonthName(month: Int): String {
        return when (month) {
            1 -> "Januari"
            2 -> "Februari"
            3 -> "Maret"
            4 -> "April"
            5 -> "Mei"
            6 -> "Juni"
            7 -> "Juli"
            8 -> "Agustus"
            9 -> "September"
            10 -> "Oktober"
            11 -> "November"
            12 -> "Desember"
            else -> changeToIndonesianMonthName(
                Calendar.getInstance()[Calendar.MONTH] + 1
            )
        }
    }

    fun changeToMonthName(month: Int): String {
        return when (month) {
            1 -> "January"
            2 -> "February"
            3 -> "March"
            4 -> "April"
            5 -> "May"
            6 -> "June"
            7 -> "July"
            8 -> "August"
            9 -> "September"
            10 -> "October"
            11 -> "November"
            12 -> "December"
            else -> changeToMonthName(
                Calendar.getInstance()[Calendar.MONTH] + 1
            )
        }
    }

    fun changeToDayName(day: Int): String? {
        return when (day) {
            1 -> "Sunday"
            2 -> "Monday"
            3 -> "Tuesday"
            4 -> "Wednesday"
            5 -> "Thursday"
            6 -> "Friday"
            7 -> "Saturday"
            else -> null
        }
    }

    fun currentDate(dateFormat: String): String {
        val cal = Calendar.getInstance()
        return getDateString(cal, dateFormat)
    }

    fun currentDateFromMilliseconds(milliseconds: Long, dateFormat: String): String {
        val cal = Calendar.getInstance()
        cal.timeInMillis = milliseconds
        return getDateString(cal, dateFormat)
    }

    private fun getDateString(cal: Calendar, dateFormat: String): String {
        val format = SimpleDateFormat(dateFormat, Locale.getDefault())
        return format.format(cal.time)
    }

    fun defaultTimeFormat(includeSeconds: Boolean): String {
        return if (includeSeconds) "HH:mm:ss" else "HH:mm"
    }

    fun defaultDateFormat(): String {
        return "yyyy-MM-dd"
    }

    fun defaultDateAndTimeFormat24Hour(): String {
        return "yyyy-MM-dd HH:mm:ss"
    }

    fun defaultDateAndTimeFormat24HourWithDay(): String {
        return "EEEE, yyyy-MM-dd HH:mm:ss"
    }

    fun defaultDateAndTimeFormat(): String {
        return "yyyy-MM-dd hh:mm:ss"
    }

    fun defaultDateAndTimeFormatWithDay(): String {
        return "EEEE, yyyy-MM-dd hh:mm"
    }

    fun dateFormatddMMMMyyyy(): String {
        return "dd MMMM yyyy"
    }

    fun dateFormatddMMMyyyy(): String {
        return "dd MMM yyyy"
    }

    fun dateFormatddMMyyyy(): String {
        return "dd MM yyyy"
    }

    fun dateFormatddMMMyyyyWithDay(): String {
        return "EEEE, dd MMM yyyy"
    }

    fun dateAndTimeFormatddMMMyyyyWithDay(): String {
        return "EEEE, dd MMM yyyy HH:mm"
    }

    fun dateFormatGmt(): String {
        return "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    }

    fun digitFormat(value: Int): String {
        return if (value > 9) value.toString() else "0$value"
    }

    fun getTime(dates: String, dateFormat: String, includeSeconds: Boolean): String {
        val date = getDateObject(dates, dateFormat)
        val timeFormat = SimpleDateFormat(
            defaultTimeFormat(includeSeconds),
            Locale.getDefault()
        )
        return timeFormat.format(date)
    }

    fun getTimeAsLong(dates: String, dateFormat: String): Long {
        val date = getDateObject(dates, dateFormat)
        return date.time
    }

    fun getDateObject(dates: String, dateFormat: String): Date {
        var date = Date()
        val format = SimpleDateFormat(dateFormat, Locale.getDefault())

        try {
            date = format.parse(dates)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return date
    }

    fun changeFormat(date: Date, outputFormat: String): String {
        val outputDateFormat = SimpleDateFormat(outputFormat)
        return outputDateFormat.format(date)
    }

    fun changeFormat(dateString: String, dateFormat: String, outputFormat: String): String? {
        if (TextUtils.isEmpty(dateString)) return null
        val date = getDateObject(dateString, dateFormat)
        val outputDateFormat = SimpleDateFormat(outputFormat)
        return outputDateFormat.format(date)
    }

    fun indonesianDateFormat(
        dateString: String,
        dateFormat: String,
        outputFormat: String
    ): String? {
        if (TextUtils.isEmpty(dateString)) return null
        val date = getDateObject(dateString, dateFormat)
        val indonesianLocale = Locale("in", "ID")
        val outputDateFormat = SimpleDateFormat(outputFormat, indonesianLocale)
        return outputDateFormat.format(date)
    }

    fun changeFormat(dates: String, dateFormat: String, includeDayOfWeek: Boolean): String {
        val date = getDateObject(dates, dateFormat)

        // Refer to http://developer.android.com/reference/java/text/SimpleDateFormat.html
        //String intMonth = (String) DateFormat.format("MM", date); //06
        val month =
            android.text.format.DateFormat.format("MMMM", date) as String //Jan
        val year =
            android.text.format.DateFormat.format("yyyy", date) as String //2013
        val day =
            android.text.format.DateFormat.format("dd", date) as String //20
        val dayOfWeek =
            android.text.format.DateFormat.format("EEEE", date) as String //Thursday

        // Use this to get Indonesian names
        /*
        Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		int dayOfWeekNumber = cal.get(Calendar.DAY_OF_WEEK); // Day of week index (Sunday is 1)
		String dayOfWeek = changeToDayName(dayOfWeekNumber);
		String month = changeToMonthName(Integer.parseInt(intMonth));

		return dayOfWeek + ", " + day + " " + month.substring(0, 3) + " " + year;
		*/
        val detailedDate = "$day $month $year"
        if (dateFormat == defaultDateAndTimeFormat24Hour()) {
            val hour = android.text.format.DateFormat.format("HH", date) as String
            val minute = android.text.format.DateFormat.format("mm", date) as String
            return "$detailedDate $hour:$minute"
        }
        return if (includeDayOfWeek) "$dayOfWeek, $detailedDate" else detailedDate
    }

    fun endTimeIsGreaterThanStartTime(time1: String, time2: String): Boolean {
        val times1 = time1.split(":").toTypedArray()
        val times2 = time2.split(":").toTypedArray()
        val seconds1 = times1[0].toInt() * 3600 + times1[1].toInt() * 60
        val seconds2 = times2[0].toInt() * 3600 + times2[1].toInt() * 60
        return seconds2 > seconds1
    }

    fun endDateisGreaterThanStartDate(
        startDate: String,
        endDate: String,
        dateFormat: String
    ): Boolean {
        val date1 = getDateObject(startDate, dateFormat)
        val date2 = getDateObject(endDate, dateFormat)
        val time1 = date1.time
        val time2 = date2.time
        return time2 > time1
    }

    fun endDateisEqualOrGreaterThanStartDate(
        startDate: String,
        endDate: String,
        dateFormat: String
    ): Boolean {
        val date1 = getDateObject(startDate, dateFormat)
        val date2 = getDateObject(endDate, dateFormat)
        val time1 = date1.time
        val time2 = date2.time
        return time2 >= time1
    }

    // Get time range from today to a future date in day format
    fun timeRangeFromTodayInDay(endDate: String, dateFormat: String): Long {
        return timeRangeInDay(currentDate(dateFormat), endDate, dateFormat)
    }

    fun timeRangeInDay(startDate: String, endDate: String, dateFormat: String): Long {
        val d1 = getDateObject(startDate, dateFormat)
        val d2 = getDateObject(endDate, dateFormat)

        //in milliseconds
        val diff = Math.abs(d2.time - d1.time)
        return diff / (24 * 60 * 60 * 1000) % 7
    }

    fun dateAfterCertainDays(startDate: String, startDateFormat: String, days: Int): String {
        val date = getDateObject(startDate, startDateFormat)
        val cal = Calendar.getInstance()
        cal.time = date
        cal[Calendar.DATE] = cal[Calendar.DATE] + days
        val outputDateFormat = SimpleDateFormat(startDateFormat)
        return outputDateFormat.format(cal.time)
    }

    // Get time span of maximum prior to 7 days
    // If it's more than 7 days, it will display the actual date
    fun timeAgo(date: String, dateFormat: String): String? {
        if (TextUtils.isEmpty(date)) return null
        val sdf = SimpleDateFormat(dateFormat, Locale.getDefault())
        return try {
            val time = sdf.parse(date).time
            val now = System.currentTimeMillis()
            DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS).toString()
        } catch (e: ParseException) {
            e.printStackTrace()
            null
        }
    }

    //https://stackoverflow.com/questions/24780474/need-time-difference-with-string-like-a-min-ago-or-an-hour-ago
    fun timeSpan(timestamp: String?, dateFormat: String?): String {
        val diff: Float
        val format =
            SimpleDateFormat(dateFormat, Locale.US)
        //        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        diff = try {
            val date = format.parse(timestamp)
            System.currentTimeMillis() - date.time.toFloat()
        } catch (e: ParseException) {
            return ""
        }
        return if (diff >= 0) {
            var yearDiff = (diff / (365L * 2592000000f)).roundToInt()
            if (yearDiff < 1) yearDiff = 0

            if (yearDiff > 0) {
                yearDiff.toString() + (if (yearDiff == 1) " year" else " years") + " ago"
            } else {
                var monthDiff = (diff / 2592000000f).roundToInt()
                if (monthDiff < 1) monthDiff = 0

                if (monthDiff > 0) {
                    if (monthDiff > 11) monthDiff = 11
                    monthDiff.toString() + (if (monthDiff == 1) " month" else " months") + " ago"
                } else {
                    var dayDiff = (diff / 86400000f).roundToInt()
                    if (dayDiff < 1) dayDiff = 0

                    if (dayDiff > 0) {
                        if (dayDiff == 30) dayDiff = 29
                        dayDiff.toString() + (if (dayDiff == 1) " day" else " days") + " ago"
                    } else {
                        var hourDiff = (diff / 3600000f).roundToInt()
                        if (hourDiff < 1) hourDiff = 0

                        if (hourDiff > 0) {
                            hourDiff.toString() + (if (hourDiff == 1) " hour" else " hours") + " ago"
                        } else {
                            var minuteDiff = (diff / 60000f).roundToInt()
                            if (minuteDiff < 1) minuteDiff = 0

                            if (minuteDiff > 0) {
                                minuteDiff.toString() + (if (minuteDiff == 1) " minute" else " minutes") + " ago"
                            } else {
                                var secondDiff = (diff / 1000f).roundToInt()
                                if (secondDiff < 1) secondDiff = 0

                                secondDiff.toString() + (if (secondDiff == 1) " second" else " seconds") + " ago"
                            }
                        }
                    }
                }
            }
        } else ""
    }

    fun formatToYesterdayOrToday(date: String, dateFormat: String): String? {
        try {
            val dateTime = SimpleDateFormat(dateFormat).parse(date)
            val calendar = Calendar.getInstance()
            calendar.time = dateTime
            val today = Calendar.getInstance()
            val yesterday = Calendar.getInstance()
            yesterday.add(Calendar.DATE, -1)
            val timeFormatter: DateFormat = SimpleDateFormat("hh:mma")

            return if (calendar[Calendar.YEAR] == today[Calendar.YEAR]
                && calendar[Calendar.DAY_OF_YEAR] == today[Calendar.DAY_OF_YEAR]
            ) {
                "Today " + timeFormatter.format(dateTime)
            } else if (calendar[Calendar.YEAR] == yesterday[Calendar.YEAR]
                && calendar[Calendar.DAY_OF_YEAR] == yesterday[Calendar.DAY_OF_YEAR]
            ) {
                "Yesterday " + timeFormatter.format(dateTime)
            } else {
                changeFormat(date, dateFormat, dateFormatddMMMyyyy())
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return date
    }
}