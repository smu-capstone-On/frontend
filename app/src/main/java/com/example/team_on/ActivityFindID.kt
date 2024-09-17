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
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.databinding.ActivityFindIdBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityFindID : AppCompatActivity(), DialogAlertInterface {

    private val binding: ActivityFindIdBinding by lazy { ActivityFindIdBinding.inflate(layoutInflater) }

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

        editMail = binding.findidEditMail
        editAuth = binding.findidEditAuth
        textAuth = binding.findidTextAuth
        btnMail = binding.findidBtnMail
        btnAuth = binding.findidBtnAuth
        btnEnd = binding.findidBtnEnd

        btnMail.setOnClickListener {
            btnMail.isEnabled = false
            mail = editMail.text.toString()
            val call = RetrofitObject.getRetrofitService.sendMail(mail)
            call.enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            Toast.makeText(this@ActivityFindID,"메일이 발송되었습니다.",Toast.LENGTH_SHORT).show()
                            btnAuth.isEnabled = true
                            btnAuth.alpha = 1f
                        }
                    }else{
                        Toast.makeText(this@ActivityFindID,"메일 양식을 확인해주세요.",Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this@ActivityFindID,"인증 번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
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
                // 메일 인증 실패 시
                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
            btnAuth.isEnabled = true
        }
        clickViewEvents()
    }

    private fun clickViewEvents() {
        btnEnd.setOnClickListener {
            // 인증 여부 확인
            if (checkAuth) {
                mail = editMail.text.toString()
                val call = RetrofitObject2.getRetrofitService.findId(mail)
                call.enqueue(object : Callback<Retrofit.ResponseFindId> {
                    override fun onResponse(call: Call<Retrofit.ResponseFindId>, response: Response<Retrofit.ResponseFindId>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            // 아이디 찾기 성공 시
                            if (responseBody != null) {
                                if(responseBody.success){
                                    val title = "아이디 찾기"
                                    val id = responseBody.data.loginId

                                    val dialog = DialogAlert(this@ActivityFindID, title, id, "로그인 하기", null)
                                    // 배경 클릭 막기
                                    dialog.isCancelable = false
                                    dialog.show(this@ActivityFindID.supportFragmentManager, "DialogAlert")
                                }else{
                                    Toast.makeText(this@ActivityFindID, "입력하신 이메일로 가입된 계정이 없습니다.", Toast.LENGTH_SHORT).show()                                }
                            }
                        }
                    }
                    // 아이디 찾기 실패 시
                    override fun onFailure(call: Call<Retrofit.ResponseFindId>, t: Throwable) {
                        val errorMessage = "Call Failed: ${t.message}"
                        Log.d("Retrofit", errorMessage)
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