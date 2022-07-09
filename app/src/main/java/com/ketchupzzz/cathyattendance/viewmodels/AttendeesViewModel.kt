package com.ketchupzzz.cathyattendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ketchupzzz.cathyattendance.models.Attendance
import com.ketchupzzz.cathyattendance.models.Attendees

class AttendeesViewModel : ViewModel() {
    val selected = MutableLiveData<Attendance>()

    fun setAttendance(attendance: Attendance) {
        selected.value = attendance
    }
    fun getAttendance()  : LiveData<Attendance> {
        return selected
    }
}