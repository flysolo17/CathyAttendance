package com.ketchupzzz.cathyattendance.models

class SubjectClass(val classID : String ? = null,
                   val classTeacherID : String ? = null,
                   var classProfile: String? = null,
                   val classTitle: String? = null,
                   val classDesc: String? = null,
                   val students: List<String> = mutableListOf(),
                    val timestamp : Long = System.currentTimeMillis()) {
    companion object {
        const val TABLE_NAME = "SubjectClass"
        const val CLASS_ID = "SubjectClass"
        const val TEACHER_ID = "classTeacherID"

    }
}