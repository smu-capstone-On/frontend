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
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.databinding.ActivityProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ActivityProfile : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private lateinit var textCheckNick: TextView
    private lateinit var btnNick: Button
    private lateinit var editNick: EditText
    private lateinit var nick: String
    private lateinit var gender: String
    private lateinit var animal: String
    private var checkNick = false

    //닉네임 중복 체크
    private val checkNickWatcherListener = object : TextWatcher {

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            checkNick = false
            textCheckNick.visibility = View.INVISIBLE
        }

        override fun afterTextChanged(s: Editable?) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        textCheckNick = binding.profileTextCheckNick
        btnNick = binding.profileBtnNickcheck
        editNick = binding.profileEditNick
        val id = intent.getStringExtra("id")
        val pw = intent.getStringExtra("pw")
        val mail = intent.getStringExtra("mail")

        editNick.addTextChangedListener(checkNickWatcherListener)

        btnNick.setOnClickListener {
            nick = editNick.text.toString()
            val call = RetrofitObject.getRetrofitService.checkNick(nick)
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    btnNick.isEnabled = true
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            textCheckNick.visibility = View.VISIBLE
                            if(responseBody.success) {
                                textCheckNick.text = "사용할 수 있는 닉네임입니다."
                                textCheckNick.setTextColor(Color.BLACK)
                                checkNick = true
                            }else{
                                textCheckNick.text = "이미 존재하는 닉네임입니다."
                                textCheckNick.setTextColor(Color.RED)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    btnNick.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        binding.profileBtnSignup.setOnClickListener {
            val groupGender = binding.profileRadioGender.checkedRadioButtonId
            val groupAnimal = binding.profileRadioAnimal.checkedRadioButtonId
            var age = 0
            var userId = 0
            if(!checkNick){
                Toast.makeText(this@ActivityProfile,"닉네임 중복을 확인해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(groupGender != -1){
                gender = findViewById<RadioButton>(groupGender).text.toString()
                gender = when (gender) {
                    "여자" -> "FEMALE"
                    "남자" -> "MALE"
                    else -> "UNKNOWN"
                }
            }else{
                Toast.makeText(this@ActivityProfile,"성별을 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(groupAnimal != -1){
                animal = findViewById<RadioButton>(groupAnimal).text.toString()
                animal = when (animal) {
                    "있음" -> "PETYES"
                    "없음" -> "PETNO"
                    else -> "UNKNOWN"
                }
            }else{
                Toast.makeText(this@ActivityProfile,"반려동물 유무를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val ageString = binding.profileEditAge.text.toString()
            if(ageString.isEmpty()){
                Toast.makeText(this@ActivityProfile,"나이를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                age = ageString.toInt()
            }
            val call = RetrofitObject2.getRetrofitService.signUp(Retrofit.RequestSignUp(id!!,pw!!,mail!!))
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            if(responseBody.success){
                                Log.d("회원가입", "회원가입 성공")
                                val call = RetrofitObject2.getRetrofitService.signIn(Retrofit.RequestSignIn(id, pw))
                                call.enqueue(object : Callback<Retrofit.ResponseSignIn> {
                                    override fun onResponse(call: Call<Retrofit.ResponseSignIn>, response: Response<Retrofit.ResponseSignIn>) {
                                        if (response.isSuccessful) {
                                            val responseBody = response.body()
                                            if(responseBody != null){
                                                if(responseBody.success) {
                                                    Log.d("회원가입", "로그인 성공")
                                                    userId = responseBody.data.id
                                                    val call = RetrofitObject2.getRetrofitService.makeProfile(userId.toString(), Retrofit.RequestProfile(nick, gender, age, animal))
                                                    call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                                                        override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                                                            if (response.isSuccessful) {
                                                                val responseBody = response.body()
                                                                if(responseBody != null){
                                                                    if(responseBody.success) {
                                                                        Log.d("회원가입", "프로필 생성 성공")
                                                                        startActivity(Intent(this@ActivityProfile, ActivitySuccessSignUp::class.java))
                                                                        finish()
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
                                            }
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
                }

                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    btnNick.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }
    }
}