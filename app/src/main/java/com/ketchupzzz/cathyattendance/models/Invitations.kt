package com.ketchupzzz.cathyattendance.models

class Invitations(val classID : String ? = null,
                  val studentID : String? = null,
                  val isAccepted: Boolean = false,
                  val timestamp : Long = System.currentTimeMillis()) {
    companion object {
        const val TABLE_NAME = "Invitations"
    }

}