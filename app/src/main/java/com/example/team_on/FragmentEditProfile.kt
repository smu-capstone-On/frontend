package com.example.team_on

import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.FragmentEditProfileBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentEditProfile : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var changeProfileImage: ImageButton
    private lateinit var profile: CircleImageView
    private lateinit var editNick: EditText
    private lateinit var btnCheckNick: Button
    private lateinit var btnSave: Button
    private lateinit var textCheckNick: TextView
    private lateinit var nick: String
    private lateinit var toolbar: Toolbar
    private var checkNick = false

    // 이미지 선택을 위한 ActivityResultLauncher
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            changeProfile(it)
        }
    }

    // 권한 요청을 위한 ActivityResultLauncher
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            getImage.launch("image/*")
        } else {
            Toast.makeText(activity, "프로필 이미지를 설정하려면 권한이 필요합니다. 설정에서 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        changeProfileImage = binding.editProfileBtnChangeImage
        profile = binding.editProfileImageProfile
        editNick = binding.editProfileEditNick
        btnCheckNick = binding.editProfileBtnNickcheck
        btnSave = binding.editProfileBtnSave
        textCheckNick = binding.editProfileTextCheckNick
        toolbar = binding.editProfileToolbar

        editNick.addTextChangedListener(checkNickWatcherListener)

        changeProfileImage.setOnClickListener {
            buildVersion()
        }

        btnCheckNick.setOnClickListener {
            nick = editNick.text.toString()
            val call = RetrofitObject.getRetrofitService.checkNick(nick)
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    btnCheckNick.isEnabled = true
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null) {
                            textCheckNick.visibility = View.VISIBLE
                            if (responseBody.success) {
                                textCheckNick.text = "사용할 수 있는 닉네임입니다."
                                textCheckNick.setTextColor(Color.BLACK)
                                checkNick = true
                            } else {
                                textCheckNick.text = "이미 존재하는 닉네임입니다."
                                textCheckNick.setTextColor(Color.RED)
                            }
                        }
                    }
                }
                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    btnCheckNick.isEnabled = true
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        btnSave.setOnClickListener {
            if (checkNick) {
                // 닉네임 중복 확인
            }
        }

        toolbar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
    // 버전 확인
    private fun buildVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermission(android.Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun requestPermission(permission: String) {
        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                getImage.launch("image/*")
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showPermission(permission)
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }
    // 접근 권한이 필요한 경우 알림
    private fun showPermission(permission: String) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("권한이 필요합니다.")
            .setMessage("프로필 이미지를 설정하기 위해서는 갤러리 접근 권한이 필요합니다.")
            .setPositiveButton("동의하기") { _, _ ->
                requestPermissionLauncher.launch(permission)
            }
            .setNegativeButton("취소하기") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
    // 이미지 변경
    private fun changeProfile(uri: Uri) {
        Glide.with(this)
            .load(uri)
            .into(profile)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
