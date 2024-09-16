package com.example.team_on

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.team_on.databinding.ActivitySuccessSignUpBinding

class ActivitySuccessSignUp : AppCompatActivity() {

    private val binding: ActivitySuccessSignUpBinding by lazy { ActivitySuccessSignUpBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.successSignupBtn.setOnClickListener {
            startActivity(Intent(this@ActivitySuccessSignUp, ActivityLogin::class.java))
            finish()
        }
    }
}