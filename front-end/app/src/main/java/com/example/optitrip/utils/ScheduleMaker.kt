package com.example.optitrip.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.optitrip.entities.trip.Point
import com.example.optitrip.entities.trip.Trip
import java.io.File
import java.io.IOException
import java.io.OutputStreamWriter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID

/**
 * Class generation an ics file from the data from [ScheduleFragment]
 *
 * @property trip the trip of the schedule
 * @property startTime the start of the schedule
 * @property durations the time passed at each point
 * @property types the type of stop at each point
 * @property context the context of the activity
 * @property text [StringBuilder] to build string top write
 */
class ScheduleMaker(private val trip : Trip, private var startTime : LocalDateTime, private val durations : Array<Int>, private val types : Array<String>, private val context: Context) {

    private val text : StringBuilder = StringBuilder("")

    /**
     * Build intro string
     *
     */
    private fun intro() {
        text.append("BEGIN:VCALENDAR").append(END_OF_LINE)
        text.append("VERSION:2.0").append(END_OF_LINE)
        text.append("CALSCALE:GREGORIAN").append(END_OF_LINE)
        text.append("PRODID:-//Romain//FormationDevAndroid//").append(END_OF_LINE)
        text.append("METHOD:PUBLISH").append(END_OF_LINE)
    }

    /**
     * Build timezone string
     *
     */
    private fun timezone() {

        val now = LocalDate.now()
        val nowString = "%04d%02d%02d".format(now.year,now.monthValue,now.dayOfMonth)

        val tz = TimeZone.getDefault()
        var offsetST = ""
        var offsetDL = ""
        var transitionMonthtoDL = 0
        var transitionMonthtoST = 0

        val rules = tz.toZoneId().rules
        val iNow = Instant.now()
        val transition = rules.nextTransition(iNow)
        if(transition != null) {

            val transitionAfter = tz.toZoneId().rules.nextTransition(transition.instant.plusSeconds(400000))

            if (tz.observesDaylightTime()) {
                offsetDL = transition.offsetBefore.toString()
                offsetST = transition.offsetAfter.toString()
                transitionMonthtoST = transition.dateTimeAfter.monthValue
                transitionMonthtoDL = transitionAfter.dateTimeAfter.monthValue
            } else {
                offsetST = transition.offsetBefore.toString()
                offsetDL = transition.offsetAfter.toString()
                transitionMonthtoDL = transition.dateTimeAfter.monthValue
                transitionMonthtoST = transitionAfter.dateTimeAfter.monthValue
            }

            offsetDL = offsetDL.replace(":", "")
            offsetST = offsetST.replace(":", "")

            text.append("BEGIN:VTIMEZONE").append(END_OF_LINE)
            text.append("TZID:${tz.id}").append(END_OF_LINE)
            text.append("LAST-MODIFIED:${nowString}T100000Z").append(END_OF_LINE)
            text.append("TZURL:http://tzurl.org/zoneinfo-outlook/${tz.id}").append(END_OF_LINE)
            //daylight
            text.append("BEGIN:DAYLIGHT").append(END_OF_LINE)
            text.append("TZNAME:${tz.getDisplayName(true, TimeZone.LONG)}").append(END_OF_LINE)
            text.append("TZOFFSETFROM:${offsetST}").append(END_OF_LINE)
            text.append("TZOFFSETTO:${offsetDL}").append(END_OF_LINE)
            text.append("RRULE:FREQ=YEARLY;BYMONTH=${transitionMonthtoDL};BYDAY=-1SU")
                .append(END_OF_LINE)
            text.append("END:DAYLIGHT").append(END_OF_LINE)

            text.append("BEGIN:STANDARD").append(END_OF_LINE)
            text.append("TZNAME:${tz.getDisplayName(false, TimeZone.LONG)}").append(END_OF_LINE)
            text.append("TZOFFSETFROM:${offsetDL}").append(END_OF_LINE)
            text.append("TZOFFSETTO:${offsetST}").append(END_OF_LINE)
            text.append("RRULE:FREQ=YEARLY;BYMONTH=${transitionMonthtoST};BYDAY=-1SU")
                .append(END_OF_LINE)
            text.append("END:STANDARD").append(END_OF_LINE)
            text.append("END:VTIMEZONE").append(END_OF_LINE)

        }else {
            text.append("BEGIN:VTIMEZONE").append(END_OF_LINE)
            text.append("TZID:${tz.id}").append(END_OF_LINE)
            text.append("LAST-MODIFIED:${nowString}T000000Z").append(END_OF_LINE)
            text.append("TZURL:http://tzurl.org/zoneinfo-outlook/${tz.id}").append(END_OF_LINE)
            text.append("END:VTIMEZONE").append(END_OF_LINE)
        }
    }

    /**
     * Build one event string
     *
     * @param i the index of the event
     * @param point the point of the event's location
     */
    private fun event(i : Int, point : Point) {
        val formatTime = {time : LocalDateTime -> "%02d%02d%02d".format(time.hour,time.minute,time.second)}
        val date = "%04d%02d%02d".format(startTime.year,startTime.monthValue,startTime.dayOfMonth)
        val now = LocalDate.now()
        val nowString = "%04d%02d%02d".format(now.year,now.monthValue,now.dayOfMonth)
        val tz = TimeZone.getDefault()

        val eventName = "${types[i]}  ${point.pointName}"
        val eventDuration = durations[i]
        val eventAddress = point.address!!.replace("\n","")

        text.append("BEGIN:VEVENT").append(END_OF_LINE)
            .append("DTSTAMP:${nowString}T100000Z").append(END_OF_LINE)
            .append("UID:${UUID.randomUUID()}").append(END_OF_LINE)
            .append("DTSTART;TZID=${tz.id}:${date}T${formatTime(startTime)}Z").append(END_OF_LINE)

        startTime = startTime.plusMinutes(eventDuration.toLong())

        text.append("DTEND;TZID=${tz.id}:${date}T${formatTime(startTime)}Z").append(END_OF_LINE)
            .append("SUMMARY:$eventName").append(END_OF_LINE)
            .append("DESCRIPTION:${types[i]} at ${point.pointName} during my trip ${trip.tripName}").append(END_OF_LINE)
            .append("LOCATION:${eventAddress}").append(END_OF_LINE)
            .append("STATUS:CONFIRMED").append(END_OF_LINE)
            .append("TRANSP:OPAQUE").append(END_OF_LINE)

        if(i < trip.steps.size) {
            startTime = startTime.plusSeconds(trip.steps[i].stepTime!!.toLong())
        }


        text.append("BEGIN:VALARM").append(END_OF_LINE)
        text.append("ACTION:DISPLAY").append(END_OF_LINE)
        text.append("DESCRIPTION:$eventName").append(END_OF_LINE)
        text.append("TRIGGER:-PT30M").append(END_OF_LINE)
        text.append("END:VALARM").append(END_OF_LINE)
        text.append("END:VEVENT").append(END_OF_LINE)
    }

    /**
     * Write string to a file
     *
     * @param path path of the file
     */
    private fun writeFile(path: String) {
        try {
            val streamWriter = OutputStreamWriter(context.openFileOutput(path,Context.MODE_PRIVATE))
            streamWriter.write(text.toString())
            streamWriter.close()
        }catch (e : IOException) {
            Log.e("Exception ","cannot write schedule to file")
        }
    }

    /**
     * Generate the file by build the string [text] then write it
     *
     * @param path
     */
    fun generateICS(path : String) {
        this.intro()
        this.timezone()
        for((i,point) in trip.points.withIndex()) {
            this.event(i,point)
        }
        text.append("END:VCALENDAR")
        this.writeFile(path)
    }

    fun intentOpenICS(path : String) : Intent {
        //getfile dir
        val myMime = MimeTypeMap.getSingleton();
        val mimetype = myMime.getMimeTypeFromExtension("a.ics")

        val intent  = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(File(path)),mimetype)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        return intent
    }

    fun intentEvent() : Intent{
         val intent = Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI).apply {
             val beginTime: android.icu.util.Calendar? = android.icu.util.Calendar.getInstance().apply {
                 set(2022,0,45,7,30)
             }

             val endTime: android.icu.util.Calendar? = android.icu.util.Calendar.getInstance().apply {
                 set(2022,0,45,8,30)
             }

             putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime!!.timeInMillis)
             putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime!!.timeInMillis)
             putExtra(Events.TITLE,"trip")
             putExtra(Events.EVENT_LOCATION, "auderghem belgique")

         }
        return intent
    }

    companion object {
        const val END_OF_LINE ="\r\n"
    }


}