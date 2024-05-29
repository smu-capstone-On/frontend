package com.example.team_on

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team_on.databinding.ActivityCalendarBinding

class ActivityCalendar : AppCompatActivity() {

    private val binding: ActivityCalendarBinding by lazy { ActivityCalendarBinding.inflate(layoutInflater) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }
}