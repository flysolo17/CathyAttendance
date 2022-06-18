package com.ketchupzzz.cathyattendance.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue
import java.util.*
@Parcelize
class SubjectClass(val classID : String ? = null,
                   val classTeacherID : String? = null,
                   var classProfile: String? = null,
                   val classTitle: String? = null,
                   val classDesc: String? = null,
                   val classCode: String = UUID.randomUUID().toString(), //code to join the class
                   val isOpen : Int = 0,   //0 for close 1 for open (for enrollment)
                   val schedules: @RawValue MutableList<Schedules> = mutableListOf(),
                   val timestamp : Long = System.currentTimeMillis())  : Parcelable {
    companion object {
        const val TABLE_NAME = "SubjectClass"
        const val CLASS_ID = "classID"
        const val TEACHER_ID = "classTeacherID"

    }
}