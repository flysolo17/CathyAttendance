package com.ketchupzzz.cathyattendance.models


class Attendance(val attendanceID : String? = null,
                 val attendanceNote : String? = "",
                 val accepting : Boolean = true,
                 val attendees : List<Attendees> = mutableListOf(),
                 val timestamp: Long = System.currentTimeMillis()) {

    companion object {
        const val TABLE_NAME = "Attendance"
        const val ACCEPTING = "accepting"
        const val ATTENDEES = "attendees"
    }
}