package com.example.team_on

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.team_on.databinding.ActivityFindIdBinding

class ActivityFindID : AppCompatActivity(), DialogAlertInterface {

    private val binding: ActivityFindIdBinding by lazy { ActivityFindIdBinding.inflate(layoutInflater) }

    private lateinit var editMail: EditText
    private lateinit var editAuth: EditText
    private lateinit var btnMail: Button
    private lateinit var btnAuth: Button
    private lateinit var btnEnd: Button
    private lateinit var mail: String
    private lateinit var auth: String
    private var checkAuth = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editMail = binding.findidEditMail
        editAuth = binding.findidEditAuth
        btnMail = binding.findidBtnMail
        btnAuth = binding.findidBtnAuth
        btnEnd = binding.findidBtnEnd

        btnMail.setOnClickListener {
            btnMail.isEnabled = false
            mail = editMail.text.toString()

        }

        btnAuth.setOnClickListener {
            btnAuth.isEnabled = false
            auth = editAuth.text.toString()
        }

        clickViewEvents()
    }

    private fun clickViewEvents() {
        btnEnd.setOnClickListener {
            mail = editMail.text.toString()

            val title = "아이디 찾기"
            val content = ""

            val dialog = DialogAlert(this@ActivityFindID, title, content, "로그인 하기", -1)
            // 배경 클릭 막기
            dialog.isCancelable = false
            dialog.show(this.supportFragmentManager, "DialogAlert")
        }
    }

    override fun onClickOkButton(id: Int) {
        // 화면 이동
        finish()
        startActivity(Intent(this, ActivityLogin::class.java ))
    }
}