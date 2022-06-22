package com.ketchupzzz.cathyattendance.models

import java.sql.Timestamp

class Comments(val commentID : String? = null,
               val writerID : String? = null,
               val timestamp : Long = System.currentTimeMillis()) {
}