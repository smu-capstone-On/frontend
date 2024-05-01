package com.example.team_on

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityLogin : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var editId: EditText
    private lateinit var editPw: EditText
    private lateinit var buttonSignIn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
                val call = RetrofitObject.getRetrofitService.signIn(Retrofit.RequestSignIn(id, pw))
                call.enqueue(object : Callback<Retrofit.ResponseSignIn> {
                    override fun onResponse(call: Call<Retrofit.ResponseSignIn>, response: Response<Retrofit.ResponseSignIn>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            if(responseBody != null){
                                if(responseBody.success) {
                                    startActivity(Intent(this@ActivityLogin, ActivityMain::class.java))
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
        binding.loginBtnKakao.setOnClickListener {  }
    }

}