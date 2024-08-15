package com.example.team_on

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.team_on.databinding.ActivityCalendarBinding
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class ActivityCalendar : AppCompatActivity() {

    private val binding: ActivityCalendarBinding by lazy { ActivityCalendarBinding.inflate(layoutInflater) }

    private lateinit var btnCal: ImageButton
    private lateinit var calConst: ConstraintLayout
    private lateinit var calendar: MaterialCalendarView
    private lateinit var calText: TextView
    private lateinit var btnNext: ImageButton
    private lateinit var btnBack: ImageButton
    private var tf = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        btnCal = binding.calBtnCal
        calConst = binding.calConst
        calendar = binding.calCal
        calText = binding.calTextDate
        btnNext = binding.calBtnGraph
        btnBack = binding.calBtnBack

        btnCal.setOnClickListener {
            if(tf){
                calConst.visibility = View.GONE
                tf = false
            }else{
                calConst.visibility = View.VISIBLE
                tf = true
            }
        }

        calendar.setOnDateChangedListener{ _, date, _ ->
            // 날짜가 선택되었을 때 호출되는 부분
            val selectedDate = date.date.toString()
            val dateText = selectedDate.replace("-",". ")
            calText.text = dateText
            calConst.visibility = View.GONE
        }

        btnNext.setOnClickListener {
            startActivity(Intent(this@ActivityCalendar, ActivityCalendarV2::class.java))
        }

        btnBack.setOnClickListener {
            finish()
        }
    }
}