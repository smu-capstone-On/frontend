package com.example.team_on

import android.graphics.Color
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
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.FragmentChangeNickBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentChangeNick : Fragment() {

    private var _binding: FragmentChangeNickBinding? = null
    private val binding get() = _binding!!

    private lateinit var editNick: EditText
    private lateinit var btnCheckNick: Button
    private lateinit var btnSave: Button
    private lateinit var textCheckNick: TextView
    private lateinit var nick: String
    private lateinit var toolbar: Toolbar
    private var checkNick = false

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
        _binding = FragmentChangeNickBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editNick = binding.editProfileEditNick
        btnCheckNick = binding.editProfileBtnNickcheck
        btnSave = binding.editProfileBtnSave
        textCheckNick = binding.editProfileTextCheckNick
        toolbar = binding.editProfileToolbar

        editNick.addTextChangedListener(checkNickWatcherListener)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
