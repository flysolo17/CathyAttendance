package com.ketchupzzz.cathyattendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users

class SubjectClassViewModel : ViewModel() {
    val selected = MutableLiveData<SubjectClass>()
    fun setSubjectClass(subjectClass: SubjectClass) {
        selected.value = subjectClass
    }
    fun getSubjectClass() : LiveData<SubjectClass> {
        return selected
    }
}