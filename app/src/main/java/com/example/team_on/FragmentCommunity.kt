package com.example.team_on

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.connection.RetrofitObject3
import com.example.team_on.databinding.FragmentCommunityBinding
import retrofit2.Call
import retrofit2.Callback
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
    private lateinit var progressBar: ProgressBar
    private var postList = mutableListOf<Retrofit.Post3>()
    private var filteredList = mutableListOf<Retrofit.Post3>()
    private var selectedTags = mutableListOf<String>()
    private lateinit var coordinatorLayout: CoordinatorLayout
    private lateinit var imageUrl: String

    private val tagMapping = mapOf(
        "DOG" to "강아지",
        "CAT" to "고양이",
        "SMALL_ANIMAL" to "소동물",
        "REPILES" to "파충류",
        "BIRD" to "조류",
        "QUESTION" to "질문"
    )

    // 태그를 한글로 변환하는 함수
    private fun convertTagToKorean(tag: String): String {
        return tagMapping[tag] ?: tag // 매핑에 없으면 원래 태그 반환
    }

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
        progressBar = binding.communityProgressBar

        setSearchFun()
        stopSearchFun()
        setTagBtn()
        addPost()
        loadItems()

        postList = mutableListOf()
        filteredList.addAll(postList)

        // Adapter 생성 시 loadImageUrl 함수 전달
        postAdapter = AdapterPost(filteredList) { post ->
            getUserNick(post.memberId.toInt()) { nickname ->
                val fragment = FragmentPostDetail.newInstance(
                    post.title,
                    post.body,
                    post.likeCount,
                    post.boardTags,
                    imageUrl,
                    post.time,
                    post.memberId.toInt(),
                    nickname
                )
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_frame, fragment)
                    ?.addToBackStack(null)
                    ?.commit()
                (activity as? ActivityMain)?.hideBottomNavigation()
            }
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
        val filteredPosts = postList.filter { post ->
            val matchesText = post.title.lowercase(Locale.ROOT).contains(searchText) ||
                    post.body.lowercase(Locale.ROOT).contains(searchText)
            val matchesTags = if(selectedTags.isEmpty()) {
                true
            } else {
                post.boardTags.any { tag ->
                    val koreanTags = convertTagToKorean(tag)
                    koreanTags in selectedTags
                }
            }
            matchesText && matchesTags
        }
        filteredList.clear()
        filteredList.addAll(filteredPosts)
        postAdapter.filterList(filteredList)
    }

    // 아이템 목록 최신화
    private fun loadItems() {
        progressBar.visibility = View.VISIBLE

        val call = RetrofitObject2.getRetrofitService.getAllPosts2()
        call.enqueue(object : Callback<List<Retrofit.Post2>> {
            override fun onResponse(call: Call<List<Retrofit.Post2>>, response: Response<List<Retrofit.Post2>>) {
                if (response.isSuccessful) {
                    val posts = response.body() ?: emptyList()
                    val subPost = posts.slice(10..13)
                    postList.clear()
                    val sortPosts = subPost.sortedByDescending { it.id }

                    var pendingCallbacks = subPost.size
                    if (pendingCallbacks == 0) {
                        progressBar.visibility = View.GONE
                        updatePostList() // 만약 product가 없을 때를 대비한 처리
                    }

                    for(post in sortPosts){
                        Log.d("포스트", post.toString())
                        if (post.fileInfo != null) {
                            val call = RetrofitObject3.getRetrofitService.loadImg(post.fileInfo.id)
                            call.enqueue(object : Callback<Retrofit.FileUrl> {
                                override fun onResponse(call: Call<Retrofit.FileUrl>, response: Response<Retrofit.FileUrl>) {
                                    if (response.isSuccessful) {
                                        val fileInfo = response.body()
                                        fileInfo?.let {
                                            imageUrl = it.url // 서버에서 받아온 URL을 추출
                                            Log.d("이미지", imageUrl)
                                            postList.add(
                                                Retrofit.Post3(
                                                    post.title,
                                                    post.body,
                                                    post.likeCount,
                                                    post.boardTags,
                                                    post.comments,
                                                    post.fileInfo,
                                                    post.memberId,
                                                    post.time,
                                                    imageUrl
                                                )
                                            )
                                        }
                                    }
                                    // 콜백 완료 시마다 카운터 감소
                                    pendingCallbacks--
                                    if (pendingCallbacks == 0) {
                                        progressBar.visibility = View.GONE
                                        updatePostList()
                                    }
                                }

                                override fun onFailure(call: Call<Retrofit.FileUrl>, t: Throwable) {
                                    val errorMessage = "Call Failed: ${t.message}"
                                    Log.d("Retrofit", errorMessage)
                                    // 실패해도 카운터 감소
                                    pendingCallbacks--
                                    if (pendingCallbacks == 0) {
                                        progressBar.visibility = View.GONE
                                        updatePostList()
                                    }
                                }
                            })
                        } else {
                            // fileInfo가 없는 경우 바로 productList에 추가
                            postList.add(
                                Retrofit.Post3(
                                    post.title,
                                    post.body,
                                    post.likeCount,
                                    post.boardTags,
                                    post.comments,
                                    null,
                                    post.memberId,
                                    post.time,
                                    null
                                )
                            )
                            // 콜백 완료 시마다 카운터 감소
                            pendingCallbacks--
                            if (pendingCallbacks == 0) {
                                progressBar.visibility = View.GONE
                                updatePostList()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: retrofit2.Call<List<Retrofit.Post2>>, t: Throwable) {
                progressBar.visibility = View.GONE
                Toast.makeText(context, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
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

    // getUserNick
    private fun getUserNick(id: Int, callback: (String) -> Unit) {
        val call = RetrofitObject2.getRetrofitService.searchUser((id + 1).toString())
        call.enqueue(object : Callback<Retrofit.ResponseUserInfo> {
            override fun onResponse(call: Call<Retrofit.ResponseUserInfo>, response: Response<Retrofit.ResponseUserInfo>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        callback(responseBody.nickName)
                    } else {
                        callback("Unknown")
                    }
                } else {
                    Toast.makeText(context, "사용자 정보 로드 실패: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                    callback("Unknown")
                }
            }

            override fun onFailure(call: Call<Retrofit.ResponseUserInfo>, t: Throwable) {
                Toast.makeText(context, "사용자 정보 로드 실패: ${t.message}", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
                callback("Unknown")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updatePostList() {
        filteredList.clear()
        filteredList.addAll(postList)
        postAdapter.filterList(postList)

        Toast.makeText(context, "게시글이 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
    }
}
