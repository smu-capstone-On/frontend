package com.example.team_on

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team_on.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.mainSignupbtn.setOnClickListener {
            startActivity(Intent(this, ActivitySignup::class.java))
        }
        binding.mainLoginbtn.setOnClickListener {
            startActivity(Intent(this, ActivityLogin::class.java))
        }
        binding.mainBtnGoprofile.setOnClickListener {
            val intent = Intent(this, ActivityProfile::class.java)
            startActivity(intent)
        }
    }
}