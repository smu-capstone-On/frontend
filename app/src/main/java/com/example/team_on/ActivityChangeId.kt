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
import androidx.appcompat.app.AppCompatActivity
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.ActivityChangeIdBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityChangeId : AppCompatActivity() {

    private val binding: ActivityChangeIdBinding by lazy { ActivityChangeIdBinding.inflate(layoutInflater) }

    private lateinit var editId: EditText
    private lateinit var btnIdCheck: Button
    private lateinit var btnSave: Button
    private lateinit var textCheckId: TextView
    private lateinit var id : String
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
            id = editId.text.toString()
            val call = RetrofitObject.getRetrofitService.checkId(id)
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

        btnSave.setOnClickListener {
            if (checkId) {

            }
        }

    }
}