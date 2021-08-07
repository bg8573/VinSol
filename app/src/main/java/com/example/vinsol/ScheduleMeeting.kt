package com.example.vinsol

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import java.text.SimpleDateFormat
import java.util.*


class ScheduleMeeting : AppCompatActivity() {


    val sdf = SimpleDateFormat("HH:mm")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule_meeting)
        val date = findViewById<Button>(R.id.date)
        val startTime = findViewById<Button>(R.id.start_time)
        val endTime = findViewById<Button>(R.id.end_time)
        val back = findViewById<TextView>(R.id.back)
        val backIcon = findViewById<ImageView>(R.id.backIcon)
        backIcon.setOnClickListener({super.onBackPressed()})
        back.setOnClickListener({super.onBackPressed()})
        date.setOnClickListener(DatePicker(date))
        startTime.setOnClickListener(TimePicker(startTime))
        endTime.setOnClickListener(TimePicker(endTime))
        val scheduleMeetingBtn = findViewById<Button>(R.id.schedule_meeting)
        scheduleMeetingBtn.setOnClickListener {
            if (startTime.text.toString().isEmpty() || endTime.text.toString().isEmpty() || date.text.toString().isEmpty()) {
                Toast.makeText(this, "Please Enter date and time", Toast.LENGTH_LONG).show()
            } else {
                val st = sdf.parse(startTime.text.toString())
                val et = sdf.parse(endTime.text.toString())
                if (et.before(st)) {
                    Toast.makeText(this, "Please select valid time", Toast.LENGTH_LONG).show()
                } else
                    CheckSlot(date.text.toString(), st, et)
            }
        }
    }

    private fun TimePicker(Time: Button): (v: View) -> Unit =
        {
            val c: Calendar = Calendar.getInstance()
            val mHour = c.get(Calendar.HOUR_OF_DAY)
            val mMinute = c.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(
                this,
                { view, hourOfDay, minute -> Time.setText("$hourOfDay:$minute") },
                mHour,
                mMinute,
                false
            )
            timePickerDialog.show()

        }

    private fun DatePicker(date: Button): (v: View) -> Unit = {
        val c: Calendar = Calendar.getInstance()
        val mYear = c.get(Calendar.YEAR)
        val mMonth = c.get(Calendar.MONTH)
        val mDay = c.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(
            this,
            { view, year, monthOfYear, dayOfMonth -> date.setText(dayOfMonth.toString() + "-" + (monthOfYear + 1) + "-" + year) },
            mYear,
            mMonth,
            mDay
        )
        datePickerDialog.show()
    }

    private fun CheckSlot(date: String, st: Date, et: Date) {

        var check = true
        val url = "https://fathomless-shelf-5846.herokuapp.com/api/schedule?date="+date
        val queue = Volley.newRequestQueue(this)
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->

                for (i in 0 until response.length()) {
                    val jsonObject = response.getJSONObject(i)
                    val startTime = sdf.parse(jsonObject.getString("start_time"))
                    val endTime = sdf.parse(jsonObject.getString("end_time"))
                    if (st.after(startTime) && st.before(endTime))
                    {
                        check = false
                    }
                    else if (st.before(startTime) && et.after(startTime)){
                         check = false
                }
                }
                if (check==true)
                    Toast.makeText(this,"Slot Available",Toast.LENGTH_LONG).show()
                else
                    Toast.makeText(this,"Slot Not Available",Toast.LENGTH_LONG).show()
            },
            { error ->
                Toast.makeText(this,error.message, Toast.LENGTH_LONG).show()

            }
        )
        queue.add(jsonArrayRequest)

    }
}