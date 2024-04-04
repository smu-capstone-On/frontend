package com.example.team_on

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.team_on.databinding.ActivityLoginBinding

class ActivityLogin : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)


    }
}