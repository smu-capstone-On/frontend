package com.example.team_on

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.ActivityProfileBinding
import com.example.team_on.databinding.ActivitySignupBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivitySignup : AppCompatActivity() {

    private val binding: ActivitySignupBinding by lazy { ActivitySignupBinding.inflate(layoutInflater) }
    private lateinit var textCheckId : TextView
    private lateinit var textCheckPw : TextView
    private lateinit var textAuth : TextView
    private lateinit var btnCheckId : Button
    private lateinit var btnMail : Button
    private lateinit var btnAuth : Button
    private lateinit var btnNext : Button
    private lateinit var editId : EditText
    private lateinit var editPw : EditText
    private lateinit var editPwCheck : EditText
    private lateinit var editMail : EditText
    private lateinit var editAuth : EditText
    private lateinit var id : String
    private lateinit var pw : String
    private lateinit var mail : String
    private var checkId = false
    private var checkPw = false
    private var checkAuth = false

    //아이디 중복 체크
    private val checkIdWatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            checkId = false
            textCheckId.visibility = View.INVISIBLE
        }

        override fun afterTextChanged(s: Editable?) {}
    }

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

        textCheckId = binding.signupTextCheckId
        textCheckPw = binding.signupTextPwCheck
        textAuth = binding.signupTextAuth
        btnCheckId = binding.signupBtnCheckId
        btnMail = binding.signupBtnMail
        btnAuth = binding.signupBtnAuth
        btnNext = binding.signupBtnNext
        editId = binding.signupEditId
        editPw = binding.signupEditPwd
        editPwCheck = binding.signupEditPwdCheck
        editMail = binding.signupEditMail
        editAuth = binding.signupEditAuth

        editId.addTextChangedListener(checkIdWatcherListener)
        editPwCheck.addTextChangedListener(checkPwWatcherListener)

        btnCheckId.setOnClickListener {
            btnCheckId.isEnabled = false
            id = editId.text.toString()
            val call = RetrofitObject.getRetrofitService.checkId(id)
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    btnCheckId.isEnabled = true
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
                    btnCheckId.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }
        btnMail.setOnClickListener {
            btnMail.isEnabled = false
            mail = editMail.text.toString()
            val call = RetrofitObject.getRetrofitService.sendMail(Retrofit.RequestMail(mail))
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    btnMail.isEnabled = true
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            textCheckId.visibility = View.VISIBLE
                            if(responseBody.success) {
                                Toast.makeText(this@ActivitySignup,"메일이 발송되었습니다.",Toast.LENGTH_SHORT).show()
                                btnAuth.isEnabled = true
                                btnAuth.alpha = 1f
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    btnCheckId.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        btnAuth.setOnClickListener {
            btnAuth.isEnabled = false
            val auth = editAuth.text.toString()
            val call = RetrofitObject.getRetrofitService.checkAuth(Retrofit.RequestAuth(mail, auth))
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            textCheckId.visibility = View.VISIBLE
                            if(responseBody.success) {
                                btnMail.visibility = View.GONE
                                btnAuth.visibility = View.GONE
                                editAuth.visibility = View.GONE
                                textAuth.visibility = View.VISIBLE
                                checkAuth = true
                            }else{
                                btnAuth.isEnabled = true
                                Toast.makeText(this@ActivitySignup,"인증 번호가 일치하지 않습니다.",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        btnNext.setOnClickListener {
            if(checkId && checkPw && checkAuth){
                val intent = Intent(this, ActivityProfile::class.java)
                intent.putExtra("id", id)
                intent.putExtra("pw", pw)
                intent.putExtra("mail", mail)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this@ActivitySignup,"양식을 다시 확인해 주세요.",Toast.LENGTH_SHORT).show()
            }
        }
    }
}