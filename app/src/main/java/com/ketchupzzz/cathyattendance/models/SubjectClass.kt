package com.ketchupzzz.cathyattendance.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

import java.util.*
@Parcelize
class SubjectClass(val classID : String ? = null,
                   val classTeacherID : String? = null,
                   var classProfile: String? = null,
                   val classTitle: String? = null,
                   val classDesc: String? = null,
                   val open : Boolean? = null,
                   val classCode: String = UUID.randomUUID().toString(),
                   val timestamp : Long = System.currentTimeMillis())  : Parcelable {
    companion object {
        const val TABLE_NAME = "SubjectClass"
        const val CLASS_ID = "classID"
        const val TEACHER_ID = "classTeacherID"
        const val CLASS_TITLE = "classTitle"
        const val IS_OPEN = "open"
    }
}