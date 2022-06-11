package com.ketchupzzz.cathyattendance.models

data class Users(val userID : String? = null,
                 val idNumber : String? = null,
                 val userProfile : String = "",
                 val firstname : String = "",
                 val middleName : String = "",
                 val lastname : String = "",
                 val userType : String? = null,
                 val email : String ? = null) {
}