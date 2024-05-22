package com.example.team_on

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.example.team_on.databinding.FragmentAddPostBinding

class FragmentAddPost : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var boardType: String
    private lateinit var toolbar: Toolbar

    companion object {
        private const val ARG_BOARD_TYPE = "board_type"

        // 게시판 타입에 따른 새 인스턴스 생성
        fun newInstance(boardType: String): FragmentAddPost {
            val fragment = FragmentAddPost()
            val args = Bundle()
            args.putString(ARG_BOARD_TYPE, boardType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            boardType = it.getString(ARG_BOARD_TYPE) ?: "community"
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar = binding.addPostToolbar

        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setUI(){
        when (boardType) {
            "community" -> { }
            "deal" -> { }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}