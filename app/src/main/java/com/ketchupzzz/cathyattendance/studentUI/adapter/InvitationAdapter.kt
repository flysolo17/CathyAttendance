package com.ketchupzzz.cathyattendance.studentUI.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.models.Invitations
import com.ketchupzzz.cathyattendance.models.SubjectClass
import com.ketchupzzz.cathyattendance.models.Users
import com.squareup.picasso.Picasso


class InvitationAdapter(val context: Context,val invitationsList : List<Invitations>,val invitationClicks: InvitationClicks) : RecyclerView.Adapter<InvitationAdapter.InvitationViewHolder>() {

    interface InvitationClicks{
        fun accept(position: Int)
        fun reject(position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvitationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.row_invitations,parent,false)
        return InvitationViewHolder(view)
    }

    override fun onBindViewHolder(holder: InvitationViewHolder, position: Int) {
        holder.bindInvitationContent(invitationsList[position].classID!!)
        holder.buttonReject.setOnClickListener {
            invitationClicks.reject(position)
        }
        holder.buttonAccept.setOnClickListener {
            invitationClicks.accept(position)
        }
    }

    override fun getItemCount(): Int {
        return invitationsList.size
    }
    class InvitationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageTeacherProfile : ImageView= itemView.findViewById(R.id.imageTeacherProfile)
        val textTeachersName : TextView = itemView.findViewById(R.id.textTeachersName)
        val textInvitationContent : TextView = itemView.findViewById(R.id.textInvitationContent)
        val buttonReject : Button = itemView.findViewById(R.id.buttonReject)
        val buttonAccept : Button = itemView.findViewById(R.id.buttonAccept)
        val firestore = FirebaseFirestore.getInstance()
        fun bindInvitationContent(classID : String) {
            firestore.collection(SubjectClass.TABLE_NAME)
                .document(classID)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        error.printStackTrace()
                    } else {
                        if (value != null && value.exists()) {
                            val subjectClass = value.toObject(SubjectClass::class.java)
                            bindTeacherInfo(subjectClass?.classTeacherID!!)
                            textInvitationContent.text = "Invited you to join class ${subjectClass!!.classTitle}"
                        }
                    }
                }
        }
        fun bindTeacherInfo(teacherID : String) {
            firestore.collection(Users.TABLE_NAME)
                .document(teacherID)
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        error.printStackTrace()
                    } else {
                        if (value != null && value.exists()) {
                            val user = value.toObject(Users::class.java)
                            textTeachersName.text = "${user?.firstname} ${user?.middleName} ${user?.lastname}"
                            if (user?.userProfile!!.isNotEmpty()) {
                                Picasso.get().load(user.userProfile).into(imageTeacherProfile)
                            }
                        }
                    }
                }
        }
    }
}
