package com.example.team_on

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import com.example.team_on.databinding.FragmentHomeBinding

class FragmentHome : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var btnGoWalk: Button
    private lateinit var btnGoCalendar: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        btnGoWalk = binding.homeBtnGoWalk
        btnGoCalendar = binding.homeBtnGoCalendar

        btnGoWalk.setOnClickListener{
            val intent = Intent(requireContext(), ActivityWalk::class.java)
            startActivity(intent)
        }

        btnGoCalendar.setOnClickListener{
            val intent = Intent(requireContext(), ActivityCalendar::class.java)
            startActivity(intent)
        }
        return binding.root
    }
}