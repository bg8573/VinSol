package com.example.vinsol

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    var MeetingList = ArrayList<Meeting>()
    lateinit var recyclerView: RecyclerView
    lateinit var displayDate:TextView
    lateinit var adapter:MeetingListAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    private val date = Date()
    private val sdf = SimpleDateFormat("dd/MM/yyyy")
    private val currentDate = sdf.format(date.time)
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recyclerView)
        val scheduleMeeting = findViewById<Button>(R.id.schedule_meeting_btn)
        scheduleMeeting.setOnClickListener({
            startActivity(Intent(this,ScheduleMeeting::class.java))
        })
        val next = findViewById<TextView>(R.id.next)
        val nextIcon = findViewById<ImageView>(R.id.nextIcon)
        nextIcon.setOnClickListener({
            calendar.add(Calendar.DAY_OF_YEAR,1)
            loadData(sdf.format(calendar.time))
        })
        val prevIcon = findViewById<ImageView>(R.id.prevIcon)
        prevIcon.setOnClickListener({
            calendar.add(Calendar.DAY_OF_YEAR,-1)
            loadData(sdf.format(calendar.time))
        })
        calendar.time = date
        next.setOnClickListener({
            calendar.add(Calendar.DAY_OF_YEAR,1)
            loadData(sdf.format(calendar.time))
        })
        val prev = findViewById<TextView>(R.id.prev)
        prev.setOnClickListener({
            calendar.add(Calendar.DAY_OF_YEAR,-1)
            loadData(sdf.format(calendar.time))
        })
        displayDate = findViewById(R.id.display_date)
        recyclerView.setHasFixedSize(true)
        loadData(currentDate)

    }

    private fun loadData(date:String) {

        displayDate.setText(date)
        MeetingList.clear()
        val url = "https://fathomless-shelf-5846.herokuapp.com/api/schedule?date="+date
        val queue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->

                    for (i in 0 until response.length()) {
                        val jsonObject = response.getJSONObject(i)
                        val startTime = jsonObject.getString("start_time")
                        val endTime = jsonObject.getString("end_time")
                        val description = jsonObject.getString("description")
                        MeetingList.add(Meeting(startTime, endTime, description))
                    }
                if (MeetingList.isEmpty()){
                    Toast.makeText(this, "No Meetings Scheduled for Today", Toast.LENGTH_LONG).show()
                    adapter.notifyDataSetChanged()
                }
                 else {
                    layoutManager = LinearLayoutManager(this)
                    recyclerView.setLayoutManager(layoutManager)
                    adapter = MeetingListAdapter(this, MeetingList)
                    recyclerView.adapter = adapter
                    adapter.notifyDataSetChanged()
                    recyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
                }

            },
            { error ->
                Toast.makeText(this,error.message, Toast.LENGTH_LONG).show()
                }
        )
        queue.add(jsonArrayRequest)

    }
}