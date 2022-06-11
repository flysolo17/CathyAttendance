package com.ketchupzzz.cathyattendance.loginsystem

import android.widget.EditText


class Validation {
    private var emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    fun validateCard(inputEmail: EditText) : Boolean {
        val email = inputEmail.text.toString()
        return if (email.isEmpty()){
            inputEmail.error = "enter email"
            false
        } else if (!email.trim { it <= ' ' }.matches(emailPattern.toRegex())){
            inputEmail.error = "Invalid Email"
            false
        }
        else {
            true
        }
    }
    fun validatePassword(inputPassword: EditText) : Boolean {
        val password =inputPassword.text.toString()
        return when {
            password.isEmpty() -> {
                inputPassword.error = "Invalid Password"
                false
            }
            password.length < 7 -> {
                inputPassword.error = "Password too short"
                false
            }
            else -> {

                true
            }
        }
    }
}