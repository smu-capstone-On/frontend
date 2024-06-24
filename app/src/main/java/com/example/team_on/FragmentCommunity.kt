package com.example.team_on

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.databinding.FragmentCommunityBinding
import java.util.Locale

class FragmentCommunity: Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnTagDog: Button
    private lateinit var btnTagCat: Button
    private lateinit var btnTagSmall: Button
    private lateinit var btnTagReptile: Button
    private lateinit var btnTagBird: Button
    private lateinit var btnTagQuestion: Button
    private lateinit var btnSearch: ImageButton
    private lateinit var btnSearchCancel: ImageButton
    private lateinit var btnAddPost: ImageButton
    private lateinit var editTextSearch: EditText
    private lateinit var recyclerView: RecyclerView

    private lateinit var postAdapter: AdapterPost
    private var postList = mutableListOf<Post>()
    private var filteredList = mutableListOf<Post>()
    private var selectedTags = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnTagDog = binding.communityTagDog
        btnTagCat = binding.communityTagCat
        btnTagSmall = binding.communityTagSmall
        btnTagReptile = binding.communityTagReptile
        btnTagBird = binding.communityTagBird
        btnTagQuestion = binding.communityTagQuestion
        btnSearch = binding.communityBtnSearch
        btnSearchCancel = binding.communityBtnSearchCancel
        btnAddPost = binding.communityBtnPost
        editTextSearch = binding.communityEditSearch
        recyclerView = binding.communityRecyclerview

        setSearchFun()
        stopSearchFun()
        setTagBtn()
        addPost()

        postList = mutableListOf(
            Post("Post Dog", "This is the content of post 1\ndog", listOf("강아지")),
            Post("Post Cat Question", "This is the content of post 2\ncat, question", listOf("고양이", "질문")),
            Post("Post Cat", "This is the content of post 3\ncat", listOf("고양이")),
            Post("Post Dog Question", "This is the content of post 4\ndog, question", listOf("강아지", "질문")),
            Post("Post Dog Cat Question", "This is the content of post 5\ndog, cat, question", listOf("강아지","고양이","질문")),
            Post("Post Small Animal ", "This is the content of post 6\nsmall", listOf("소동물")),
            Post("Post Reptile", "This is the content of post7\nreptile", listOf("파충류"))
        )

        filteredList.addAll(postList)

        postAdapter = AdapterPost(filteredList) { post ->
            val fragment = FragmentPostDetail.newInstance(post.title, post.content)
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_frame, fragment)
                ?.addToBackStack(null)
                ?.commit()
            (activity as? ActivityMain)?.hideBottomNavigation()
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = postAdapter
        }
    }

    // 검색 버튼 클릭 시
    private fun setSearchFun() {
        btnSearch.setOnClickListener {
            btnSearch.isEnabled = false
            btnSearchCancel.visibility = View.VISIBLE
            editTextSearch.visibility = View.VISIBLE
            editTextSearch.requestFocus()
        }
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    // 검색 취소 버튼 클릭 시
    private fun stopSearchFun() {
        btnSearchCancel.setOnClickListener {
            btnSearch.isEnabled = true
            btnSearchCancel.visibility = View.GONE
            editTextSearch.visibility = View.GONE
            editTextSearch.text.clear()
            editTextSearch.clearFocus()
            filter("")
        }
    }

    // 검색 단어 및 태그 필터링
    private fun filter(text: String) {
        val searchText = text.lowercase(Locale.ROOT)
        filteredList.clear()
        val filteredPosts = postList.filter { post ->
            val matchesText = post.title.lowercase(Locale.ROOT).contains(searchText) ||
                    post.content.lowercase(Locale.ROOT).contains(searchText)
            val matchesTags = selectedTags.isEmpty() || post.tags.any { it in selectedTags }
            matchesText && matchesTags
        }
        filteredList.addAll(filteredPosts)
        postAdapter.notifyDataSetChanged()
    }

    // 아이템 목록 최신화
    private fun loadItems() {
        filter(editTextSearch.text.toString())
    }

    // 태그 클릭 시 색 변환
    private fun setTagBtn() {
        val btns = listOf(btnTagDog, btnTagCat, btnTagSmall, btnTagReptile, btnTagBird, btnTagQuestion)

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
                filter(editTextSearch.text.toString())
            }
        }
    }

    // 게시글 작성 화면으로 전환
    private fun addPost() {
        btnAddPost.setOnClickListener {
            (activity as? ActivityMain)?.hideBottomNavigation()

            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frame, FragmentAddPost())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
