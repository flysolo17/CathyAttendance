package com.ketchupzzz.cathyattendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ketchupzzz.cathyattendance.models.Students
import com.ketchupzzz.cathyattendance.models.SubjectClass

class StudentsViewModel : ViewModel() {
    val selected = MutableLiveData<Students>()
    fun setStudent(students: Students) {
        selected.value = students
    }
    fun getStudent() : LiveData<Students> {
        return selected
    }
}