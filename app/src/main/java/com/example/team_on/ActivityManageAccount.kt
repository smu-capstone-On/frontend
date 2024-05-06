package com.example.team_on

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.team_on.databinding.ActivityManageAccountBinding

class ActivityManageAccount : AppCompatActivity() {

    private val binding: ActivityManageAccountBinding by lazy { ActivityManageAccountBinding.inflate(layoutInflater) }

    private lateinit var btnChangeId: ImageButton
    private lateinit var btnChangePw: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        btnChangeId = binding.accountBtnChangeId
        btnChangePw = binding.accountBtnChangePw

        btnChangeId.setOnClickListener {
            val intent = Intent(this, ActivityChangeId::class.java)
            startActivity(intent)
        }
        btnChangePw.setOnClickListener {
            val intent = Intent(this, ActivityChangePw::class.java)
            startActivity(intent)
        }
    }
}