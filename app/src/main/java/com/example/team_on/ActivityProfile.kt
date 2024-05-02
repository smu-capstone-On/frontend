package com.example.team_on

import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.ActivityProfileBinding
import de.hdodenhof.circleimageview.CircleImageView
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class ActivityProfile : AppCompatActivity() {

    private val binding: ActivityProfileBinding by lazy { ActivityProfileBinding.inflate(layoutInflater) }
    private lateinit var changeProfileImage: ImageButton
    private lateinit var profile: CircleImageView
    private lateinit var mediaType: MediaType
    private lateinit var textCheckNick: TextView
    private lateinit var btnNick: Button
    private lateinit var editNick: EditText
    private lateinit var nick: String
    private lateinit var gender: String
    private lateinit var animal: String
    private var checkNick = false

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val path = getRealPathFromUri(uri)
            val file = File(path)
            mediaType = "image/*".toMediaType()
            changeProfile(uri, file)
        }
    }

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

        changeProfileImage = binding.profileBtnChangeImage
        profile = binding.profileImageProfile
        textCheckNick = binding.profileTextCheckNick
        btnNick = binding.profileBtnNickcheck
        editNick = binding.profileEditNick

        editNick.addTextChangedListener(checkNickWatcherListener)

        changeProfileImage.setOnClickListener{
            when {
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }

                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                -> {
                    showPermissionContextPopup()
                }

                else -> requestPermissions(
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    1000
                )
            }
        }

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
            if(groupGender != -1){
                gender = findViewById<RadioButton>(groupGender).text.toString()
            }else{
                Toast.makeText(this@ActivityProfile,"성별을 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if(groupAnimal != -1){
                animal = findViewById<RadioButton>(groupAnimal).text.toString()
            }else{
                Toast.makeText(this@ActivityProfile,"반려동물 유무를 선택해 주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val ageString = binding.profileEditAge.text.toString()
            val age = ageString.toInt()
            Log.d("profile", gender+animal+ageString)
        }
    }

    //이미지 저장 주소 가져오기
    private fun getRealPathFromUri(uri: Uri): String? {
        val context = applicationContext
        val contentResolver = context.contentResolver
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                return it.getString(columnIndex)
            }
        }
        return null
    }

    //권한 요청
    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("프로필 이미지를 설정하기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            }
            .setNegativeButton("취소하기") { _, _ -> }
            .create()
            .show()
    }

    private fun changeProfile(uri: Uri, file: File){
        Glide.with(this)
            .load(uri)
            .into(profile)
    }
}