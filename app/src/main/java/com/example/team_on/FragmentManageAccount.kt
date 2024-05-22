package com.example.team_on

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentTransaction
import com.example.team_on.databinding.FragmentManageAccountBinding

class FragmentManageAccount : Fragment() {

    private var _binding: FragmentManageAccountBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnChangeId: ImageButton
    private lateinit var btnChangePw: ImageButton
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentManageAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnChangeId = binding.accountBtnChangeId
        btnChangePw = binding.accountBtnChangePw
        toolbar = binding.accountToolbar

        btnChangeId.setOnClickListener {
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frame, FragmentChangeId())
            transaction.addToBackStack(null)
            transaction.commit()
        }

        btnChangePw.setOnClickListener {
            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frame, FragmentChangePw())
            transaction.addToBackStack(null)
            transaction.commit()
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