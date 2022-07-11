package com.ketchupzzz.cathyattendance.models

class Students(val studentID : String? = null,
               val gradeList: List<Grade> = listOf(Grade("First Grading"),Grade("Second Grading"),Grade("Third Grading")),
               val timestamp : Long = System.currentTimeMillis()) {

    companion object {
        const val TABLE_NAME = "Students"
        const val GRADE  ="gradeList"
    }
}