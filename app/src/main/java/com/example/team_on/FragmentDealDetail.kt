package com.example.team_on

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.example.team_on.databinding.FragmentDealDetailBinding

class FragmentDealDetail : Fragment() {

    private var _binding: FragmentDealDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnchatting: Button
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDealDetailBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val title = arguments?.getString(ARG_TITLE)
        val price = arguments?.getString(ARG_PRICE)
        val date = arguments?.getString(ARG_DATE)

        binding.dealDetailTitle.text = title
        binding.dealDetailPrice.text = price
        binding.dealDetailDate.text = date

        btnchatting = binding.dealDetailBtnChatting
        toolbar = binding.dealDetailToolbar

        sendChat()

        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
    private fun sendChat() {
        btnchatting.setOnClickListener {

        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? ActivityMain)?.showBottomNaviagtion()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_PRICE = "price"
        private const val ARG_DATE = "date"

        fun newInstance(title: String, price: String, date: String) =
            FragmentDealDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_PRICE, price + "원")
                    putString(ARG_DATE, date)
                }
            }
    }
}