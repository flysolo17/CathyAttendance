package com.ketchupzzz.cathyattendance.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ketchupzzz.cathyattendance.models.Users

class UserViewModel : ViewModel() {
    val selected = MutableLiveData<Users>()

    fun setUser(users: Users) {
        selected.value = users
    }
    fun getUser() : LiveData<Users> {
        return selected
    }
}