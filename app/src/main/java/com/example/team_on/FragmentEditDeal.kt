package com.example.team_on

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.team_on.databinding.FragmentEditDealBinding

class FragmentEditDeal : Fragment() {

    private var _binding: FragmentEditDealBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnSaveEditDeal: Button
    private lateinit var btnPreorder: Button
    private lateinit var btnSold: Button
    private lateinit var btnTagDog: Button
    private lateinit var btnTagCat: Button
    private lateinit var btnTagSmall: Button
    private lateinit var btnTagReptile: Button
    private lateinit var btnTagBird: Button
    private lateinit var btnEditImage: ImageButton
    private lateinit var toolbar: Toolbar

    private var selectedTags = mutableListOf<String>()
    private var isSelectedPreorder: Boolean = false
    private var isSelectedSold: Boolean = false

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            btnEditImage.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditDealBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSaveEditDeal = binding.editDealBtnSave
        btnPreorder = binding.editDealBtnPreorder
        btnSold = binding.editDealBtnSold
        btnTagDog = binding.editDealTagDog
        btnTagCat = binding.editDealTagCat
        btnTagSmall = binding.editDealTagSmall
        btnTagReptile = binding.editDealTagReptile
        btnTagBird = binding.editDealTagBird
        btnEditImage = binding.editDealAddImage
        toolbar = binding.editDealToolbar

        setTagBtn()
        setBtn(btnPreorder)
        setBtn(btnSold)
        saveEditDeal()

        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setBtn(btn: Button) {
        btn.setOnClickListener {
            btn.isSelected = !btn.isSelected
            if (btn.isSelected) {
                btn.setTextColor(ContextCompat.getColor(btn.context, R.color.white))
                when (btn) {
                    btnPreorder -> isSelectedPreorder = true
                    btnSold -> isSelectedSold = true
                }
            } else {
                btn.setTextColor(ContextCompat.getColor(btn.context, R.color.hint))
            }
        }
    }

    // 태그 버튼 클릭 시
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
        btnEditImage.setOnClickListener {
            getImage.launch("image/*")
        }
    }

    // 등록 버튼 클릭 시
    private fun saveEditDeal() {
        btnSaveEditDeal.setOnClickListener {
            val title = binding.editDealTitle.text.toString()
            val content = binding.editDealContent.text.toString()
            val price = binding.editDealEditPrice.text.toString()
            val checkPreorder = btnPreorder.isSelected
            val checkSold = btnSold.isSelected
            val tagList = selectedTags
            if (title.isEmpty()) {
                Toast.makeText(activity, "제목이 입력되지 않았습니다.",Toast.LENGTH_SHORT).show()
            } else if (content.isEmpty()) {
                Toast.makeText(activity, "내용이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}