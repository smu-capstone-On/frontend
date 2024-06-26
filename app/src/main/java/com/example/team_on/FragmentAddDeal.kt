package com.example.team_on

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.team_on.databinding.FragmentAddDealBinding

class FragmentAddDeal : Fragment() {

    private var _binding: FragmentAddDealBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnAddDeal: Button
    private lateinit var btnTagDog: Button
    private lateinit var btnTagCat: Button
    private lateinit var btnTagSmall: Button
    private lateinit var btnTagReptile: Button
    private lateinit var btnTagBird: Button
    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var btnAddImage: ImageButton
    private lateinit var toolbar: Toolbar

    private var selectedTags = mutableListOf<String>()

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            btnAddImage.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddDealBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddDeal = binding.addDealBtnEnterPost
        btnTagDog = binding.addDealTagDog
        btnTagCat = binding.addDealTagCat
        btnTagSmall = binding.addDealTagSmall
        btnTagReptile = binding.addDealTagReptile
        btnTagBird = binding.addDealTagBird
        editTextTitle = binding.addDealTitle
        editTextContent = binding.addDealContent
        editTextPrice = binding.addDealEditPrice
        btnAddImage = binding.addDealAddImage
        toolbar = binding.addDealToolbar

        setTagBtn()
        addDeal()
        addImage()

        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setTagBtn() {
        val btns = listOf(btnTagDog, btnTagCat, btnTagSmall, btnTagReptile, btnTagBird)

        btns.forEach { button ->
            button.setOnClickListener {
                button.isSelected = !button.isSelected
                if (button.isSelected) {
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
                    selectedTags.add(button.text.toString())
                } else {
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.hint))
                    selectedTags.remove(button.text.toString())
                }
            }
        }
    }

    // 물품 이미지 추가
    private fun addImage() {
        btnAddImage.setOnClickListener {
            getImage.launch("image/*")
        }
    }

    // 물품 등록하기
    private fun addDeal() {
        btnAddDeal.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()
            val price = editTextPrice.text.toString()
            val tagList = selectedTags
            if (title.isEmpty()) {
                Toast.makeText(activity, "게시글의 제목이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            } else if (content.isEmpty()) {
                Toast.makeText(activity, "게시글의 내용이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            } else {

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? ActivityMain)?.showBottomNaviagtion()
        _binding = null
    }

}