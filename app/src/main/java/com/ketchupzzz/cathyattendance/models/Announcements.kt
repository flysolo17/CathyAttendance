package com.ketchupzzz.cathyattendance.models

class Announcements(val announcementID : String? = null,
                    val writerID : String? = null,
                    val announcementContent : String? = null,
                    val comments : List<Comments> = mutableListOf(),
                    val timestamp : Long = System.currentTimeMillis()) {
    companion object {
        const val TABLE_NAME = "Announcements"
        const val TIMESTAMP = "timestamp"
    }
}