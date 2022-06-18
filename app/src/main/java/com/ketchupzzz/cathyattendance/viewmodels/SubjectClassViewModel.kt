package com.ketchupzzz.cathyattendance.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ketchupzzz.cathyattendance.models.SubjectClass

class SubjectClassViewModel : ViewModel() {
    val selected = MutableLiveData<SubjectClass>()

}