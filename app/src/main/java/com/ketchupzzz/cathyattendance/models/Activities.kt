package com.ketchupzzz.cathyattendance.models

import java.sql.Timestamp

class Activities(val activityID : String? = null,
              val classID : String? = null,
              val studentID : String? = null,
              val activityName : String? = null,
              val activityType : String? = null,
              val score : Int? = 0,
              val maxScore : Int? = 0,
              val activityTimestamp: Long? = null) {
    companion object {
        const val TABLE_NAME = "Activities"
        const val TIMESTAMP = "activityTimestamp"
    }
}