package com.ketchupzzz.cathyattendance.dialogs
import android.app.Activity
import android.view.View
import android.widget.TextView


import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ketchupzzz.cathyattendance.R

class ProgressDialog(private val activity: Activity) {
    private lateinit var alertDialog: androidx.appcompat.app.AlertDialog
     fun loading(title : String){
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(activity)
        val layoutInflater = activity.layoutInflater
        val view : View = layoutInflater.inflate(R.layout.progress_dialog,null)
        val textTitle  : TextView = view.findViewById(R.id.textLoadingTitle)
         textTitle.text = title
        materialAlertDialogBuilder.setView(view)
        materialAlertDialogBuilder.setCancelable(false)
        alertDialog = materialAlertDialogBuilder.create()
        alertDialog.show()
    }
     fun stopLoading(){
        alertDialog.dismiss()
    }
}