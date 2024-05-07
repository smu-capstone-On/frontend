package com.example.team_on

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.example.team_on.databinding.ActivityFindPwBinding

class ActivityFindPW : AppCompatActivity(), DialogAlertInterface {

    private val binding: ActivityFindPwBinding by lazy { ActivityFindPwBinding.inflate(layoutInflater) }

    private lateinit var editId: EditText
    private lateinit var editMail: EditText
    private lateinit var editAuth: EditText
    private lateinit var btnMail: Button
    private lateinit var btnAuth: Button
    private lateinit var btnEnd: Button
    private lateinit var id: String
    private lateinit var mail: String
    private lateinit var auth: String
    private var authCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editId = binding.findpwEditId
        editMail = binding.findpwEditMail
        editAuth = binding.findpwEditAuth
        btnMail = binding.findpwBtnMail
        btnAuth = binding.findpwBtnAuth
        btnEnd = binding.findpwBtnEnd

        btnMail.setOnClickListener {
            btnMail.isEnabled = false
            id = editId.text.toString()
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
            id = editId.text.toString()
            mail = editMail.text.toString()

            val title = "비밀번호 찾기"
            val content = ""

            val dialog = DialogAlert(this@ActivityFindPW, title, content, "로그인 하기", -1)
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