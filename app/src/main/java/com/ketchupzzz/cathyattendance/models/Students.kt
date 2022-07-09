package com.ketchupzzz.cathyattendance.models

class Students(val studentID : String? = null,
               val studentStatus : Int? = null,

               val timestamp : Long = System.currentTimeMillis()) {

    companion object {
        const val TABLE_NAME = "Students"
    }
}