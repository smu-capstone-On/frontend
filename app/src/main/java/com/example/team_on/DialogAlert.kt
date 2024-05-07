package com.example.team_on

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.team_on.databinding.DialogAlertBinding

interface DialogAlertInterface {
    fun onClickOkButton(id: Int)
}

class DialogAlert(
    dialogAlertInterface: DialogAlertInterface,
    title: String, content: String?, buttonText: String, id: Int
) :  DialogFragment() {

    private var _binding: DialogAlertBinding? = null
    private val binding get() = _binding!!

    private var dialogAlertInterface: DialogAlertInterface? = null

    private lateinit var btnEnd: Button
    private lateinit var textTitle: TextView
    private lateinit var textResponse: TextView
    private var title: String? = null
    private var content: String? = null
    private var buttonText: String? = null
    private var id: Int? = null

    init {
        textTitle.text = title
        textResponse.text = content
        btnEnd.text = buttonText
        this.id = id
        this.dialogAlertInterface = dialogAlertInterface
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAlertBinding.inflate(inflater, container, false)
        val view = binding.root

        btnEnd = binding.dialogBtnEnd
        textTitle = binding.dialogTvTitle
        textResponse = binding.dialogTvResponse

        // 레이아웃 배경을 투명하게
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 제목
        textTitle.text = title
        // 버튼 텍스트
        btnEnd.text = buttonText
        // 내용(반환값)
        if (content == null) {
            textResponse.visibility = View.GONE
        } else {
            textResponse.text = content
        }

        // 다른 버튼 유무
        if (id == -1) { }

        // 확인 버튼 클릭
        btnEnd.setOnClickListener {
            this.dialogAlertInterface?.onClickOkButton(id!!)
            dismiss()
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}