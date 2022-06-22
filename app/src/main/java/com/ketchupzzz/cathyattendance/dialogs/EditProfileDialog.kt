package com.ketchupzzz.cathyattendance.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.databinding.FragmentEditProfileDialogBinding

private const val ARG_USER_ID = "userID"
private const val ARG_USER_PROFILE= "userProfile"
private const val ARG_FIRST_NAME = "userFirstname"
private const val ARG_MIDDLE_NAME = "userMiddleName"
private const val ARG_LAST_NAME = "userLastname"
private const val ARG_ID_NUMBER = "userIDNumber"
class EditProfileDialog : DialogFragment() {
    private var userID: String? = null
    private var userProfile: String? = null
    private var userFirstname: String? = null
    private var userMiddleName: String? = null
    private var userLastname: String? = null
    private var userIDNumber: String? = null
    private lateinit var binding : FragmentEditProfileDialogBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)

        arguments?.let {
            userID = it.getString(ARG_USER_ID)
            userProfile = it.getString(ARG_USER_ID)
            userFirstname = it.getString(ARG_USER_ID)
            userMiddleName = it.getString(ARG_USER_ID)
            userLastname = it.getString(ARG_USER_ID)
            userIDNumber = it.getString(ARG_USER_ID)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEditProfileDialogBinding.inflate(inflater,container,false)
        return binding.root
    }
    companion object {
        @JvmStatic
        fun newInstance(userID: String,userProfile: String,userFirstname: String,userMiddleName: String,userLastname: String,userIDNumber: String) =
            EditProfileDialog().apply {
                arguments = Bundle().apply {
                    putString(ARG_USER_ID, userID)
                    putString(ARG_USER_PROFILE, userProfile)
                    putString(ARG_FIRST_NAME, userFirstname)
                    putString(ARG_MIDDLE_NAME, userMiddleName)
                    putString(ARG_LAST_NAME, userLastname)
                    putString(ARG_LAST_NAME, userIDNumber)

                }
            }
    }

}