package com.example.team_on

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team_on.databinding.ActivitySignupBinding

class ActivitySignup : AppCompatActivity() {
    private val binding: ActivitySignupBinding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        
    }
}