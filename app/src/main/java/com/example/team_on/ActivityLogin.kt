package com.example.team_on

import DatabaseWalk
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.ClientError
import com.kakao.sdk.common.model.ClientErrorCause
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.kakao.sdk.user.UserApiClient

class ActivityLogin : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var editId: EditText
    private lateinit var editPw: EditText
    private val sharedPreference = KakaoSDK.user
    private val editor = sharedPreference.edit()

//    private val databaseWalk: DatabaseWalk by lazy{ DatabaseWalk.getInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

//        databaseWalk.insertData("2024.08.25", "3615", "3215", null)
//        databaseWalk.insertData("2024.08.26", "2512", "2153", null)
//        databaseWalk.insertData("2024.08.27", "3521", "3215", null)
//        databaseWalk.insertData("2024.08.28", "2112", "1254", null)
//        databaseWalk.insertData("2024.08.29", "4525", "5312", null)


        editId = binding.loginEditId
        editPw = binding.loginEditPwd

        binding.loginFindId.setOnClickListener {
            startActivity(Intent(this, ActivityFindID::class.java))
        }
        binding.loginFindPwd.setOnClickListener {
            startActivity(Intent(this, ActivityFindPW::class.java))
        }
        binding.loginSignup.setOnClickListener {
            startActivity(Intent(this, ActivitySignup::class.java))
        }
        binding.loginBtnSignin.setOnClickListener {
            val id = editId.text.toString()
            val pw = editPw.text.toString()
            if(id.isEmpty()){
                Toast.makeText(this@ActivityLogin,"아이디를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }else if(pw.isEmpty()){
                Toast.makeText(this@ActivityLogin,"비밀번호를 입력해 주세요.", Toast.LENGTH_SHORT).show()
            }else{
                val call = RetrofitObject2.getRetrofitService.signIn(Retrofit.RequestSignIn(id, pw))
                call.enqueue(object : Callback<Retrofit.ResponseSignIn> {
                    override fun onResponse(call: Call<Retrofit.ResponseSignIn>, response: Response<Retrofit.ResponseSignIn>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if(responseBody != null){
                                if(responseBody.profile == null){
                                    val intent = Intent(this@ActivityLogin, ActivityProfile::class.java)
                                    intent.putExtra("userId", responseBody.id)
                                    startActivity(intent)
                                    finish()
                                }else{
                                    editor.putString("userId", responseBody.id.toString())
                                    editor.putString("email", responseBody.email)
                                    editor.putString("nick", responseBody.profile.nickName)
                                    editor.putInt("age", responseBody.profile.age)
                                    editor.putString("sex", responseBody.profile.sex)
                                    editor.putString("id", id)
                                    editor.putBoolean("kakao", false)
                                    editor.apply()
                                    startActivity(Intent(this@ActivityLogin, ActivityMain::class.java))
                                    finish()
                                }
                            }
                        }
                        else{
                            Toast.makeText(this@ActivityLogin,"입력하신 내용을 다시 확인해 주세요.",Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Retrofit.ResponseSignIn>, t: Throwable) {
                        val errorMessage = "Call Failed: ${t.message}"
                        Log.d("Retrofit", errorMessage)
                    }
                })
            }
        }

        binding.loginBtnKakao.setOnClickListener {
            val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
                if (error != null) {
                    Log.e("Kakao", "카카오계정으로 로그인 실패", error)
                } else if (token != null) {
                    Log.e("Kakao", "카카오계정으로 로그인 성공")
                }
            }

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        // 사용자가 카카오톡 설치 후 디바이스 권한 요청 화면에서 로그인을 취소한 경우,
                        // 의도적인 로그인 취소로 보고 카카오계정으로 로그인 시도 없이 로그인 취소로 처리 (예: 뒤로 가기)
                        if (error is ClientError && error.reason == ClientErrorCause.Cancelled) {
                            return@loginWithKakaoTalk
                        }

                        // 카카오톡에 연결된 카카오계정이 없는 경우, 카카오계정으로 로그인 시도
                        UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
                    } else if (token != null) {
                        Log.e("Kakao", "카카오계정으로 로그인 성공")
                        UserApiClient.instance.me { user, error ->
                            if (error != null) {
                                Log.e("Kakao", "사용자 정보 요청 실패", error)
                            }
                            else if (user != null) {
                                Log.i("Kakao", "사용자 정보 요청 성공" +
                                        "\n닉네임: ${user.id}")
                            }
                            val kakaoId = user!!.id.toString()
                            val call = RetrofitObject2.getRetrofitService.signIn(Retrofit.RequestSignIn(kakaoId,kakaoId))
                            call.enqueue(object : Callback<Retrofit.ResponseSignIn> {
                                override fun onResponse(call: Call<Retrofit.ResponseSignIn>, response: Response<Retrofit.ResponseSignIn>) {
                                    if (response.isSuccessful) {
                                        val responseBody = response.body()
                                        if(responseBody != null){
                                            if(responseBody.profile == null){
                                                val intent = Intent(this@ActivityLogin, ActivityProfile::class.java)
                                                intent.putExtra("userId", responseBody.id)
                                                startActivity(intent)
                                                finish()
                                            }else{
                                                editor.putString("userId", responseBody.id.toString())
                                                editor.putString("email", responseBody.email)
                                                editor.putString("nick", responseBody.profile.nickName)
                                                editor.putInt("age", responseBody.profile.age)
                                                editor.putString("sex", responseBody.profile.sex)
                                                editor.putBoolean("kakao", true)
                                                editor.apply()
                                                startActivity(Intent(this@ActivityLogin, ActivityMain::class.java))
                                                finish()
                                            }
                                        }
                                    }else{
                                        val call2 = RetrofitObject2.getRetrofitService.signUp(Retrofit.RequestSignUp(kakaoId,kakaoId,kakaoId))
                                        call2.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                                            override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                                                if (response.isSuccessful) {
                                                    val responseBody = response.body()
                                                    if(responseBody!!.success){
                                                        val intent = Intent(this@ActivityLogin, ActivitySuccessSignUp::class.java)
                                                        startActivity(intent)
                                                        finish()
                                                    }
                                                }
                                            }
                                            override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                                                val errorMessage = "Call Failed: ${t.message}"
                                                Log.d("Retrofit", errorMessage)
                                            }
                                        })
                                    }
                                }

                                override fun onFailure(call: Call<Retrofit.ResponseSignIn>, t: Throwable) {
                                    val errorMessage = "Call Failed: ${t.message}"
                                    Log.d("Retrofit", errorMessage)
                                }
                            })
                        }
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }
    }
}