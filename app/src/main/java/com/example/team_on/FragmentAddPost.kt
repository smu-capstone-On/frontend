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
import com.example.team_on.databinding.FragmentAddPostBinding

class FragmentAddPost : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnAddPost: Button
    private lateinit var btnTagDog: Button
    private lateinit var btnTagCat: Button
    private lateinit var btnTagSmall: Button
    private lateinit var btnTagReptile: Button
    private lateinit var btnTagBird: Button
    private lateinit var btnTagQuestion: Button
    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var btnAddImage: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var toolbar: Toolbar

    private var selectedTags = mutableListOf<String>()

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        btnAddPost = binding.addPostEnterPost
        btnTagDog = binding.addPostTagDog
        btnTagCat = binding.addPostTagCat
        btnTagSmall = binding.addPostTagSmall
        btnTagReptile = binding.addPostTagReptile
        btnTagBird = binding.addPostTagBird
        btnTagQuestion = binding.addPostTagQuestion
        editTextTitle = binding.addPostTitle
        editTextContent = binding.addPostContent
        btnAddImage = binding.addPostAddImage
        imageView = binding.addPostImageView
        toolbar = binding.addPostToolbar

        setTagBtn()
        addImage()
        addPost()

        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    // 태그 버튼 클릭 시
    private fun setTagBtn() {
        val btns = listOf(btnTagDog, btnTagCat, btnTagSmall, btnTagReptile, btnTagBird, btnTagQuestion)

        btns.forEach { button ->
            button.setOnClickListener {
                if (selectedTags.size == 3 && !button.isSelected) {
                    Toast.makeText(activity, "태그는 최대 3개까지 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
                else {
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
    }

    // 이미지 추가하기
    private fun addImage() {
        btnAddImage.setOnClickListener {
            getImage.launch("image/*")
            imageView.visibility = View.VISIBLE
        }
    }

    // 게시글 작성 완료
    private fun addPost() {
        btnAddPost.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()
            val tagList = selectedTags
            val image = imageView
            // userData
            if (title.isEmpty()) {
                Toast.makeText(activity, "게시글 제목이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            } else if (content.isEmpty()) {
                Toast.makeText(activity, "게시글 내용이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
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