package com.pollvote.utils

import android.annotation.SuppressLint
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class TimeUtil private constructor() {

    companion object {
//        fun utcToLocal(datesToConvert: String): String {
//            var ourDate: String
//            try {
//                val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
//                formatter.timeZone = TimeZone.getTimeZone("UTC")
//                val value = formatter.parse(datesToConvert)
//                val dateFormatter = SimpleDateFormat(
//                    "yyyy-MM-dd HH:mm:ss",
//                    Locale.getDefault()
//                ) //this format changeable
//                dateFormatter.timeZone = TimeZone.getDefault()
//                if (value != null)
//                    ourDate = dateFormatter.format(value)
//                //Log.d("ourDate", ourDate);
//            } catch (e: ParseException) {
//                ourDate = "0000-00-00"
//            }
//            return ourDate
//        }

        fun formatServerDateToLocal(datesToConvert: String): String {
            var ourDate =""
            try {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                formatter.timeZone = TimeZone.getTimeZone("UTC")
                val value = formatter.parse(datesToConvert)
                val dateFormatter =
                    SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) //this format changeable
                dateFormatter.timeZone = TimeZone.getDefault()
                if (value != null)
                    ourDate = dateFormatter.format(value)
                //Log.d("ourDate", ourDate);
            } catch (e: ParseException) {
                ourDate = "0000-00-00"
            }
            return ourDate
        }


        @SuppressLint("SimpleDateFormat")
        fun timeDifference(startTimeStr: String, endTimeStr: String, type: Int): String {
            val today = Date()
            val formatter = SimpleDateFormat("h:mm:ss a")
            val current = formatter.format(today)
            val startTime: Date = formatter.parse(startTimeStr)
            val currentTime: Date = formatter.parse(current)
            val endTime: Date = formatter.parse(endTimeStr)

            var mills: Long = startTime.time - currentTime.time

            //Todo add annotation
            //start time
            if (type == 1)
                mills = startTime.time - currentTime.time

            //end time
            if (type == 2)
                mills = endTime.time - currentTime.time

            //difference time
            if (type == 3)
                mills = currentTime.time - startTime.time

            val hours = (mills / (1000 * 60 * 60)).toInt()
            val mins = (mills / (1000 * 60) % 60).toInt()
            val Secs = (mills / 1000).toInt() % 60.toLong()

            var hourString = "$hours"
            if (hours < 10)
                hourString = "0$hours"

            var minsString = "$mins"
            if (mins < 10)
                minsString = "0$mins"

            var secsString = "$Secs"
            if (Secs < 10)
                secsString = "0$Secs"


//        val diff = "$hourString:$minsString:$secsString" // updated value every1 secon
            return "$hourString:$minsString:$secsString"
        }

        @SuppressLint("SimpleDateFormat")
        fun msConvert(milliSecond: Long): String {
            val millis: Long = milliSecond
            val date = Date(millis)
            val formatter = SimpleDateFormat("hh:mm:ss a")
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val formatted = formatter.format(date)
//            println("Result: $formatted")
            return formatted
        }

        fun printDifference(startDate: Date, endDate: Date): Long {
            //milliseconds
            var different = endDate.time - startDate.time
            val secondsInMilli: Long = 1000
            val minutesInMilli = secondsInMilli * 60
            val hoursInMilli = minutesInMilli * 60
            val daysInMilli = hoursInMilli * 24
            val elapsedDays = different / daysInMilli
            different %= daysInMilli

            return elapsedDays
        }
    }


}