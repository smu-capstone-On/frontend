package com.example.team_on

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.FragmentCommunityBinding
import retrofit2.Response
import java.util.Locale

class FragmentCommunity : Fragment() {

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
    private var postList = mutableListOf<Retrofit.Post>()
    private var filteredList = mutableListOf<Retrofit.Post>()
    private var selectedTags = mutableListOf<String>()
    private var isRefreshing = false
    private lateinit var coordinatorLayout: CoordinatorLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
        coordinatorLayout = binding.communityCoordinatorlayout

        setSearchFun()
        stopSearchFun()
        setTagBtn()
        addPost()
        loadItems()

        _binding = FragmentCommunityBinding.inflate(layoutInflater)

        postList = mutableListOf()

        filteredList.addAll(postList)

        postAdapter = AdapterPost(filteredList) { post ->
            val fragment = FragmentPostDetail.newInstance(post.title, post.content, post.like, post.tag, post.imgUrl, post.time, post.postNum)
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

        // 화면 드래그 시 게시글 새로고침
//        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//
//                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
//                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && !isRefreshing && dy < 0) {
//                    loadItems()
//                }
//            }
//        })
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
        val filteredPosts = postList.filter { post ->
            val matchesText = post.title.lowercase(Locale.ROOT).contains(searchText) ||
                    post.content.lowercase(Locale.ROOT).contains(searchText)
            val matchesTags = selectedTags.isEmpty() || post.tag.any { it in selectedTags }
            matchesText && matchesTags
        }
        filteredList.clear()
        filteredList.addAll(filteredPosts)
        postAdapter.filterList(filteredList)
    }

    // 아이템 목록 최신화
    private fun loadItems() {
        val call = RetrofitObject.getRetrofitService.getAllPosts()
        call.enqueue(object : retrofit2.Callback<Retrofit.ResponsePost> {
            override fun onResponse(call: retrofit2.Call<Retrofit.ResponsePost>, response: Response<Retrofit.ResponsePost>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val posts = response.body()?.data ?: emptyList()

                    val sortedPosts = posts.sortedByDescending { it.time }

                    // 기존 목록을 지우고 서버에서 받은 데이터로 갱신
                    postList.clear()
                    postList.addAll(sortedPosts)

                    // 필터 리스트도 동일하게 갱신
                    filteredList.clear()
                    filteredList.addAll(postList)

                    // RecyclerView 갱신
                    postAdapter.filterList(filteredList)

                    Toast.makeText(context, "게시글이 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<Retrofit.ResponsePost>, t: Throwable) {
                Toast.makeText(context, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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