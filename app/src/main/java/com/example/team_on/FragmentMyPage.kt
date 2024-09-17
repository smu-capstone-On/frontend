package com.example.team_on

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.FragmentTransaction
import com.example.team_on.databinding.FragmentMyPageBinding

class FragmentMyPage : Fragment(), DialogAlertInterface {

    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnEditProfile: ImageButton
    private lateinit var btnViewArticle: ImageButton
    private lateinit var btnViewComment: ImageButton
    private lateinit var btnViewBlacklist: ImageButton
    private lateinit var btnManageAccount: ImageButton
    private lateinit var btnLogout: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnViewArticle = binding.mypageBtnArticle
        btnViewComment = binding.mypageBtnComment
        btnViewBlacklist = binding.mypageBtnBlacklist
        btnManageAccount = binding.mypageBtnManageAccount
        btnLogout = binding.mypageBtnLogout

        btnManageAccount.setOnClickListener {
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frame, FragmentManageAccount())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        btnLogout.setOnClickListener {
            val title = "로그아웃 하시겠습니까?"
            val dialog = DialogAlert(this@FragmentMyPage, title, null, "확인", -1)
            dialog.isCancelable = false
            activity?.let { it.supportFragmentManager.let { fragmentManager -> dialog.show(fragmentManager, "DialogAlert") } }
        }
    }

    override fun onClickOkButton(id: Int) {
        if (id == -1) {
            // 로그아웃 실행
        }
        activity?.finishAffinity()
        startActivity(Intent(context, ActivityLogin()::class.java))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}