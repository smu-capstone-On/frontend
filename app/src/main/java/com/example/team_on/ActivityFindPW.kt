package com.example.team_on

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.databinding.ActivityFindPwBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityFindPW : AppCompatActivity(), DialogAlertInterface {

    private val binding: ActivityFindPwBinding by lazy { ActivityFindPwBinding.inflate(layoutInflater) }

    private lateinit var editId: EditText
    private lateinit var editMail: EditText
    private lateinit var editAuth: EditText
    private lateinit var textAuth: TextView
    private lateinit var btnMail: Button
    private lateinit var btnAuth: Button
    private lateinit var btnEnd: Button
    private lateinit var mail: String
    private lateinit var auth: String
    private var checkAuth = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        editId = binding.findpwEditId
        editMail = binding.findpwEditMail
        textAuth = binding.findpwTextAuth
        editAuth = binding.findpwEditAuth
        btnMail = binding.findpwBtnMail
        btnAuth = binding.findpwBtnAuth
        btnEnd = binding.findpwBtnEnd

        btnMail.setOnClickListener {
            btnMail.isEnabled = false
            mail = editMail.text.toString()
            val call = RetrofitObject.getRetrofitService.sendMail(mail)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            Toast.makeText(this@ActivityFindPW,"메일이 발송되었습니다.",Toast.LENGTH_SHORT).show()
                            btnAuth.isEnabled = true
                            btnAuth.alpha = 1f
                        }
                    }else{
                        Toast.makeText(this@ActivityFindPW,"메일 양식을 확인해주세요.",Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
            btnMail.isEnabled = true
        }

        btnAuth.setOnClickListener {
            btnAuth.isEnabled = false
            mail = editMail.text.toString()
            auth = editAuth.text.toString()
            if(auth.isEmpty()){
                btnAuth.isEnabled = true
                Toast.makeText(this@ActivityFindPW,"인증 번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val call = RetrofitObject.getRetrofitService.checkAuth(mail, auth.toInt())
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        // 메일 인증 성공 시
                        if (responseBody != null) {
                            btnMail.visibility = View.GONE
                            btnAuth.visibility = View.GONE
                            editAuth.visibility = View.GONE
                            textAuth.visibility = View.VISIBLE
                            checkAuth = true
                        }
                    }
                }
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
            btnAuth.isEnabled = true
        }

        btnEnd.setOnClickListener {
            if(checkAuth && editId.text.isNotEmpty()){
                val call = RetrofitObject2.getRetrofitService.findPw(mail)
                call.enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ActivityFindPW,"임시 비밀번호가 메일로 발송되었습니다.", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@ActivityFindPW, ActivityLogin::class.java))
                        }
                    }
                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        val errorMessage = "Call Failed: ${t.message}"
                        Log.d("Retrofit", errorMessage)
                    }
                })
            }
        }
    }

    override fun onClickOkButton(id: Int) {
        // 화면 이동
        finish()
        startActivity(Intent(this, ActivityLogin::class.java ))
    }
}