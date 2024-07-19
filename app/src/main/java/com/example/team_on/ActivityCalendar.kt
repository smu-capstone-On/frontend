package com.example.team_on

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.team_on.databinding.ActivityCalendarBinding

class ActivityCalendar : AppCompatActivity() {

    private val binding: ActivityCalendarBinding by lazy { ActivityCalendarBinding.inflate(layoutInflater) }

    private lateinit var btnCal: ImageButton
    private lateinit var calConst: ConstraintLayout
    private var tf = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        btnCal = binding.calBtnCal
        calConst = binding.calConst

        btnCal.setOnClickListener {
            if(tf){
                calConst.visibility = View.GONE
                tf = false
            }else{
                calConst.visibility = View.VISIBLE
                tf = true
            }
        }
    }
}