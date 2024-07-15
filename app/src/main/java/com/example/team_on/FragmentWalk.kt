package com.example.team_on

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import com.example.team_on.databinding.FragmentWalkBinding

class FragmentWalk : Fragment() {

    private lateinit var binding: FragmentWalkBinding
    private lateinit var btnAttribute: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalkBinding.inflate(layoutInflater)

        btnAttribute = binding.fwalkBtnAttribute

        btnAttribute.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityAttribute::class.java))
        }
        return binding.root
    }
}