package com.example.team_on

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.FragmentChangePwBinding
import retrofit2.Callback
import retrofit2.Response

class FragmentChangePw : Fragment(), DialogAlertInterface {

    private var _binding: FragmentChangePwBinding? = null
    private val binding get() = _binding!!

    private lateinit var editPw: EditText
    private lateinit var editPwCheck: EditText
    private lateinit var btnSave: Button
    private lateinit var textCheckPw: TextView
    private lateinit var id: String
    private lateinit var newPw: String
    private lateinit var oldPw: String
    private lateinit var toolbar: Toolbar
    private var checkPw = false

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
                    newPw=inputText
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChangePwBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editPw = binding.changePwEditPw
        editPwCheck = binding.changePwEditPwcheck
        btnSave = binding.changePwBtnSave
        textCheckPw = binding.changePwTextCheckPw
        toolbar = binding.changePwToolbar

        editPwCheck.addTextChangedListener(checkPwWatcherListener)

        toolbar.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        clickViewEvents()
    }
    private fun clickViewEvents() {
        btnSave.setOnClickListener {
            if (checkPw) {
                newPw = editPw.text.toString()
                val call = RetrofitObject.getRetrofitService.changePw(Retrofit.RequestChangePw(id, oldPw, newPw))
                call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                    override fun onResponse(call: retrofit2.Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                        if (response.isSuccessful) {
                            val responseBody = response.body()
                            // 비밀번호 변경 성공 시 팝업
                            if (responseBody != null) {
                                val title = "비밀번호 변경\n 완료"
                                val dialog = DialogAlert(this@FragmentChangePw, title, null, "확인")
                                dialog.isCancelable = false
                                activity?.let { dialog.show(it.supportFragmentManager, "DialogAlert") }
                            }
                        }
                    }
                    // 비밀번호 변경 실패 시
                    override fun onFailure(call: retrofit2.Call<Retrofit.ResponseSuccess>, t: Throwable) {
                        Toast.makeText(context, "비밀번호 변경에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                })
            } else {
                Toast.makeText(context, "완료되지 않은 작업이 있습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onClickOkButton(id: Int) {
        onDestroyView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}