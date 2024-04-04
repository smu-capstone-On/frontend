package com.example.team_on

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team_on.databinding.ActivityFindIdBinding

class ActivityFindID : AppCompatActivity() {

    private val binding: ActivityFindIdBinding by lazy { ActivityFindIdBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}