package com.example.team_on

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.ActivityChangeIdBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityChangeId : AppCompatActivity(), DialogAlertInterface {

    private val binding: ActivityChangeIdBinding by lazy { ActivityChangeIdBinding.inflate(layoutInflater) }

    private lateinit var editId: EditText
    private lateinit var btnIdCheck: Button
    private lateinit var btnSave: Button
    private lateinit var textCheckId: TextView
    private lateinit var newId : String
    private lateinit var oldId: String
    private var checkId = false

    //아이디 중복 체크
    private val checkIdWatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            checkId = false
            textCheckId.visibility = View.INVISIBLE
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editId = binding.changeIdEditId
        btnIdCheck = binding.changeIdBtnIdcheck
        btnSave = binding.changeIdBtnSave
        textCheckId = binding.changeIdTextCheckId

        editId.addTextChangedListener(checkIdWatcherListener)

        btnIdCheck.setOnClickListener {
            btnIdCheck.isEnabled = false
            newId = editId.text.toString()
            val call = RetrofitObject.getRetrofitService.checkId(newId)
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    btnIdCheck.isEnabled = true
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            textCheckId.visibility = View.VISIBLE
                            if(responseBody.success) {
                                textCheckId.text = "사용할 수 있는 아이디입니다."
                                textCheckId.setTextColor(Color.BLACK)
                                checkId = true
                            }else{
                                textCheckId.text = "이미 존재하는 아이디입니다."
                                textCheckId.setTextColor(Color.RED)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    btnIdCheck.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        clickViewEvents()
    }
    private fun clickViewEvents() {
        btnSave.setOnClickListener {
            if (checkId) {
                newId = editId.text.toString()
                val call = RetrofitObject.getRetrofitService.changeId(Retrofit.RequestChangeId(oldId, newId))
                call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                    override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            // 아이디 변경 성공 시 팝업
                            if (responseBody != null) {
                                val title = "아이디 변경\n 완료"
                                val dialog = DialogAlert(this@ActivityChangeId, title, null, "확인", -1)
                                dialog.isCancelable = false
                                dialog.show(this@ActivityChangeId.supportFragmentManager, "DialogAlert")
                            }
                        }
                    }
                    // 아이디 변경 실패 시
                    override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                        Toast.makeText(this@ActivityChangeId, "아이디 변경에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this@ActivityChangeId, "완료되지 않은 작업이 있습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onClickOkButton(id: Int) {
        finish()
    }
}