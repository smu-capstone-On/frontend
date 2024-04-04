package com.example.team_on

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team_on.databinding.ActivityFindPwBinding

class ActivityFindPW : AppCompatActivity() {

    private val binding: ActivityFindPwBinding by lazy { ActivityFindPwBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}