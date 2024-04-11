package com.example.team_on

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team_on.databinding.ActivityLoginBinding

class ActivityLogin : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.loginFindId.setOnClickListener {
            startActivity(Intent(this, ActivityFindID::class.java))
        }
        binding.loginFindPwd.setOnClickListener {
            startActivity(Intent(this, ActivityFindPW::class.java))
        }
        binding.loginSignup.setOnClickListener {
            startActivity(Intent(this, ActivitySignup::class.java))
        }
        binding.loginLoginbtn.setOnClickListener {  }
        binding.loginKakaoLoginbtn.setOnClickListener {  }
    }

}