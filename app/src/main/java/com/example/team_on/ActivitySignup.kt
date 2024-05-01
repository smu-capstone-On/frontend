package com.example.team_on

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team_on.databinding.ActivityProfileBinding
import com.example.team_on.databinding.ActivitySignupBinding

class ActivitySignup : AppCompatActivity() {
    private val binding: ActivitySignupBinding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.signupBtnCheckId.setOnClickListener {  }
        binding.signupBtnSendEmail.setOnClickListener {  }
        binding.signupBtnCheckEmail.setOnClickListener {  }
        binding.signupSignupbtn.setOnClickListener {
            startActivity(Intent(this, ActivityProfile::class.java))
        }
    }
}