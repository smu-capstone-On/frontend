package com.example.team_on

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.team_on.databinding.ActivityChangePwBinding

class ActivityChangePw : AppCompatActivity(), DialogAlertInterface {

    private val binding: ActivityChangePwBinding by lazy { ActivityChangePwBinding.inflate(layoutInflater) }

    private lateinit var editPw: EditText
    private lateinit var editPwCheck: EditText
    private lateinit var btnSave: Button
    private lateinit var textCheckPw: TextView
    private lateinit var pw: String
    private var checkPw = false

    //비밀번호 일치하는지 확인
    private val checkPwWatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val inputText = s.toString()
            if (inputText.isEmpty()) {
                textCheckPw.visibility = View.INVISIBLE
            } else {
                textCheckPw.visibility = View.VISIBLE
                if (inputText == editPw.text.toString()) {
                    textCheckPw.visibility = View.INVISIBLE
                    pw=inputText
                    checkPw = true
                } else {
                    textCheckPw.text = "비밀번호가 일치하지 않습니다."
                    checkPw = false
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editPw = binding.changePwEditPw
        editPwCheck = binding.changePwEditPwcheck
        btnSave = binding.changePwBtnSave
        textCheckPw = binding.changePwTextCheckPw

        editPwCheck.addTextChangedListener(checkPwWatcherListener)

        clickViewEvents()
    }
    private fun clickViewEvents() {
        btnSave.setOnClickListener {
            if (checkPw) {
                pw = editPw.text.toString()

                val title = "비밀번호 변경\n 완료"
                val dialog = DialogAlert(this@ActivityChangePw, title, null, "확인", -1)
                dialog.isCancelable = false
                dialog.show(this.supportFragmentManager, "DialogAlert")
            }
        }
    }

    override fun onClickOkButton(id: Int) {
        finish()
    }
}