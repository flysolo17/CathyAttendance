package com.ketchupzzz.cathyattendance.models

class Students(val studentID : String? = null,
               val studentStatus : Int? = null) {
    companion object {
        const val TABLE_NAME = "Students"
    }
}