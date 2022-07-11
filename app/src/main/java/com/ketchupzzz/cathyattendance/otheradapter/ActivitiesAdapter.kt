package com.ketchupzzz.cathyattendance.otheradapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ketchupzzz.cathyattendance.R
import com.ketchupzzz.cathyattendance.models.Activities
import java.text.SimpleDateFormat
import java.util.*

class ActivitiesAdapter(val context : Context, private val activitiesList : List<Activities>) : RecyclerView.Adapter<ActivitiesAdapter.ActivitiesViewHolder>()  {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesViewHolder {
        val  view : View = LayoutInflater.from(context).inflate(R.layout.row_activities,parent,false)
        return ActivitiesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivitiesViewHolder, position: Int) {
        val  activities = activitiesList[position]
        holder.textActivityName.text = activities.activityName
        holder.textActivityType.text = activities.activityType
        holder.textActivityDate.text = timestampToDate(activities.activityTimestamp!!)
        holder.textScore.text =activities.score.toString()
        holder.textMaxScore.text = activities.maxScore.toString()
        holder.textPercentage.text = "${getPercentage(activities.score!!, activities.maxScore!!)}%"
    }

    override fun getItemCount(): Int {
        return activitiesList.size
    }
    class ActivitiesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textActivityName : TextView = itemView.findViewById(R.id.textActivityName)
        val textActivityType : TextView = itemView.findViewById(R.id.textActivityType)
        val textActivityDate : TextView = itemView.findViewById(R.id.textActivityDate)
        val textScore : TextView = itemView.findViewById(R.id.textScore)
        val textMaxScore : TextView = itemView.findViewById(R.id.textMaxScore)
        val textPercentage : TextView = itemView.findViewById(R.id.textPercentage)

    }
    private fun timestampToDate(timestamp : Long)  : String{
        val  date = Date(timestamp)
        val format = SimpleDateFormat("MM/dd/yy",Locale.US)
        return format.format(date)
    }
    private fun getPercentage(score: Int, maxScore: Int): String {
        return String.format("%.1f",(score.toDouble() / maxScore) * 100)
    }
}