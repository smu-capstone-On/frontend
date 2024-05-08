package com.example.team_on

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.ActivityFindIdBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
            val call = RetrofitObject.getRetrofitService.sendMail(Retrofit.RequestMail(mail))
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        // 인증 번호 전송 성공 시
                        if (responseBody != null) {
                            Toast.makeText(this@ActivityFindID, "인증 번호를 메일로 전송했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                // 인증 번호 전송 실패 시
                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    Toast.makeText(this@ActivityFindID, "인증 번호 전송에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    btnMail.isEnabled = true
                }
            })
        }

        btnAuth.setOnClickListener {
            btnAuth.isEnabled = false
            mail = editMail.text.toString()
            auth = editAuth.text.toString()
            val call = RetrofitObject.getRetrofitService.checkAuth(Retrofit.RequestAuth(mail, auth))
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        // 메일 인증 성공 시
                        if (responseBody != null) {
                            Toast.makeText(this@ActivityFindID, "메일 인증에 성공했습니다.", Toast.LENGTH_SHORT).show()
                            checkAuth = true
                        }
                    }
                }
                // 메일 인증 실패 시
                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    Toast.makeText(this@ActivityFindID, "메일 인증에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    btnAuth.isEnabled = true
                }
            })
        }

        clickViewEvents()
    }

    private fun clickViewEvents() {
        btnEnd.setOnClickListener {
            // 인증 여부 확인
            if (checkAuth) {
                mail = editMail.text.toString()
                val call = RetrofitObject.getRetrofitService.findId(Retrofit.RequestFindId(mail))
                call.enqueue(object : Callback<Retrofit.ResponseFindId> {
                    override fun onResponse(call: Call<Retrofit.ResponseFindId>, response: Response<Retrofit.ResponseFindId>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            // 아이디 찾기 성공 시
                            if (responseBody != null) {
                                val title = "아이디 찾기"
                                val content = responseBody.id

                                val dialog = DialogAlert(this@ActivityFindID, title, content, "로그인 하기", -1)
                                // 배경 클릭 막기
                                dialog.isCancelable = false
                                dialog.show(this@ActivityFindID.supportFragmentManager, "DialogAlert")
                            }
                        }
                    }
                    // 아이디 찾기 실패 시
                    override fun onFailure(call: Call<Retrofit.ResponseFindId>, t: Throwable) {
                        Toast.makeText(this@ActivityFindID, "아이디 찾기에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(this@ActivityFindID, "메일이 인증되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClickOkButton(id: Int) {
        // 화면 이동
        finish()
        startActivity(Intent(this, ActivityLogin::class.java ))
    }
}