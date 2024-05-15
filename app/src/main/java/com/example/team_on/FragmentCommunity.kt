package com.example.team_on

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.FragmentCommunityBinding

class FragmentCommunity: Fragment() {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var btnTagDog: Button
    private lateinit var btnTagCat: Button
    private lateinit var btnTagSmall: Button
    private lateinit var btnTagReptile: Button
    private lateinit var btnTagBird: Button
    private lateinit var btnTagQuestion: Button
    private lateinit var btnSearch: ImageButton
    private lateinit var btnSearchCancel: ImageButton
    private lateinit var btnAddPost: ImageButton
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_community, container, false)

        btnTagDog = binding.communityTagDog
        btnTagCat = binding.communityTagCat
        btnTagSmall = binding.communityTagSmall
        btnTagReptile = binding.communityTagReptile
        btnTagBird = binding.communityTagBird
        btnTagQuestion = binding.communityTagQuestion
        btnSearch = binding.communityBtnSearch
        btnSearchCancel = binding.communityBtnSearchCancel
        btnAddPost = binding.communityBtnPost
        editText = binding.communityEditSearch
        recyclerView = binding.communityRecyclerview

        setSearchFun()
        stopSearchFun()
        setTagBtn()
        addPost()

        return binding.root
    }

    private fun setSearchFun() {
        btnSearch.setOnClickListener {
            btnSearch.isEnabled = false
            btnSearchCancel.visibility = View.VISIBLE
            editText.visibility = View.VISIBLE
            editText.requestFocus()
        }
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    private fun stopSearchFun() {
        btnSearchCancel.setOnClickListener {
            btnSearch.isEnabled = true
            btnSearchCancel.visibility = View.INVISIBLE
            editText.visibility = View.INVISIBLE
            editText.text.clear()
            editText.clearFocus()
        }
    }

    private fun filter(text: String) {

    }

    private fun loadItems() {

    }

    private fun setTagBtn() {
        val btns = listOf(btnTagDog, btnTagCat, btnTagSmall, btnTagReptile, btnTagBird, btnTagQuestion)

        btns.forEach { button ->
            button.setOnClickListener {
                button.isSelected = !button.isSelected
                if (button.isSelected) {
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
                } else {
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.hint))
                }
            }
        }
    }
    private fun addPost() {
        btnAddPost.setOnClickListener {
            // startActivity(Intent(requireContext(), ActivityAddPost::class.java))
        }
    }
}