package com.example.team_on

import android.os.Bundle
import android.telecom.Call
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.ActivityChangePwBinding
import retrofit2.Callback
import retrofit2.Response

class ActivityChangePw : AppCompatActivity(), DialogAlertInterface {

    private val binding: ActivityChangePwBinding by lazy { ActivityChangePwBinding.inflate(layoutInflater) }

    private lateinit var editPw: EditText
    private lateinit var editPwCheck: EditText
    private lateinit var btnSave: Button
    private lateinit var textCheckPw: TextView
    private lateinit var id: String
    private lateinit var newPw: String
    private lateinit var oldPw: String
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
                    newPw=inputText
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
                newPw = editPw.text.toString()
                val call = RetrofitObject.getRetrofitService.changePw(Retrofit.RequestChangePw(id, oldPw, newPw))
                call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                    override fun onResponse(call: retrofit2.Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            // 비밀번호 변경 성공 시 팝업
                            if (responseBody != null) {
                                val title = "비밀번호 변경\n 완료"
                                val dialog = DialogAlert(this@ActivityChangePw, title, null, "확인", -1)
                                dialog.isCancelable = false
                                dialog.show(this@ActivityChangePw.supportFragmentManager, "DialogAlert")
                            }
                        }
                    }
                    // 비밀번호 변경 실패 시
                    override fun onFailure(call: retrofit2.Call<Retrofit.ResponseSuccess>, t: Throwable) {
                        Toast.makeText(this@ActivityChangePw, "비밀번호 변경에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this@ActivityChangePw, "완료되지 않은 작업이 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClickOkButton(id: Int) {
        finish()
    }
}