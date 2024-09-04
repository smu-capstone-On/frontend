package com.example.team_on

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.FragmentPostDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FragmentPostDetail : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var editComment: EditText
    private lateinit var btnLike: ImageButton
    private lateinit var btnSendComment: ImageButton
    private lateinit var imageContent: ImageView
    private lateinit var imageUserProfile: ImageView
    private lateinit var recyclerView: RecyclerView


    private lateinit var toolbar: Toolbar

    private lateinit var commentAdapter: AdapterComment
    private var loadCommentList = mutableListOf<Retrofit.LoadComment>()

    private var title: String? = null
    private var body: String? = null
    private var like: Int? = null
    private var tag: List<String>? = null
    private var imgUrl: String? = null
    private var time: String? = null
    private var postNum: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            title = it.getString(ARG_TITLE)
            body = it.getString(ARG_BODY)
            like = it.getInt(ARG_LIKE)
            tag = it.getStringArrayList(ARG_TAG)
            imgUrl = it.getString(ARG_IMGURL)
            time = it.getString(ARG_TIME)
            postNum = it.getInt(ARG_POSTNUM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.postDetailTitle.text = title
        binding.postDetailContent.text = body
        binding.postDetailTextLike.text = like.toString()
        binding.postDetailPostDate.text = time?.let { formatPostTime(it) }

        val tags = tag
        val postTags = listOf(binding.postDetailTag1, binding.postDetailTag2, binding.postDetailTag3)

        if (tags != null) {
            for (i in tags.indices) {
                if (i < tags.size) {
                    postTags[i].text = tags[i]
                    postTags[i].visibility = View.VISIBLE
                } else {
                    postTags[i].text = ""
                    postTags[i].visibility = View.GONE
                }
            }
        }

        imgUrl?.let { url ->
            binding.postDetailImage.visibility = View.VISIBLE
            val uri = url.toUri().buildUpon().scheme("https").build()
            Glide.with(binding.postDetailImage.context)
                .load(uri) // URL을 URI로 변환하여 로드
                .error(R.drawable.svg_camera_error)
                .into(binding.postDetailImage) // 이미지가 로드될 ImageView
        }

        btnLike = binding.postDetailBtnLike
        btnSendComment = binding.postDetailBtnSendComment
        editComment = binding.postDetailEditComment
        recyclerView = binding.postDetailRecyclerview
        toolbar = binding.postDetailToolbar

        loadCommentList = mutableListOf()

        commentAdapter = AdapterComment(loadCommentList)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }

        // 좋아요 버튼 클릭
        btnLike.setOnClickListener {
            // updateLikeStatus(postNum)
            // 임시
            btnLike.isSelected = !btnLike.isSelected

            if (btnLike.isSelected) {
                btnLike.setColorFilter(ContextCompat.getColor(btnLike.context, R.color.yellow))
                // 좋아요 수 증가
                like = (like ?: 0) + 1
                binding.postDetailTextLike.text = like.toString()
            } else {
                btnLike.setColorFilter(ContextCompat.getColor(btnLike.context, R.color.hint))
                // 좋아요 수 감소
                like = (like ?: 0) - 1
                binding.postDetailTextLike.text = like.toString()
            }
        }

        // 댓글 버튼 클릭
        btnSendComment.setOnClickListener {
            val newComment = editComment.text.toString()
            if (newComment.isNotBlank()) {
                addCommentToServer(newComment)
                editComment.text.clear()
            }
        }

        // 뒤로가기 버튼
        toolbar.setOnClickListener{
            parentFragmentManager.popBackStack()
        }

        // 댓글 데이터 불러오기
        postNum?.let { loadComments(it) }
    }

    // 댓글 불러오기
    private fun loadComments(boardId: Int) {
        val call = RetrofitObject.getRetrofitService.getPost(boardId)
        call.enqueue(object : Callback<Retrofit.ResponseLoadComment> {
            override fun onResponse(call: Call<Retrofit.ResponseLoadComment>, response: Response<Retrofit.ResponseLoadComment>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val comments = response.body()?.data ?: emptyList()

                    // 댓글 리스트 갱신
                    loadCommentList.clear()
                    loadCommentList.addAll(comments)

                    binding.postDetailCommentCounter.text = loadCommentList.size.toString()

                    // RecyclerView 갱신
                    commentAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(context, "댓글을 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Retrofit.ResponseLoadComment>, t: Throwable) {
                Toast.makeText(context, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 댓글 등록 기능
    private fun addCommentToServer(comment: String) {
//        val userId = MySharedPreference.user.getLong("userId", 0L)
        val user = 99

        val commentRequest = postNum?.let { Retrofit.SaveComment(it, user, comment) }

        val call = commentRequest?.let { RetrofitObject.getRetrofitService.saveComment(it) }
        call?.enqueue(object : Callback<Retrofit.ResponseSaveComment> {
            override fun onResponse(call: Call<Retrofit.ResponseSaveComment>, response: Response<Retrofit.ResponseSaveComment>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(context, "댓글 작성에 성공했습니다.", Toast.LENGTH_SHORT).show()

                    val loadComment = Retrofit.LoadComment(user, comment, LocalDateTime.now().toString())

                    // 댓글 리스트 갱신
                    loadCommentList.addAll(listOf(loadComment))

                    binding.postDetailCommentCounter.text = loadCommentList.size.toString()

                    // RecyclerView 갱신
                    commentAdapter.notifyDataSetChanged()
                } else {
                    // 서버 응답이 실패했을 때 처리
                    Toast.makeText(context, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Retrofit.ResponseSaveComment>, t: Throwable) {
                // 네트워크 오류 등으로 요청이 실패했을 때 처리
                Toast.makeText(context, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun formatPostTime(dateString: String): String {
        // 문자열을 LocalDateTime 객체로 파싱
        val dateTime = LocalDateTime.parse(dateString)

        // 원하는 형식으로 변환하기 위한 포맷
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd   HH:mm")

        // 포맷팅된 문자열 반환
        return dateTime.format(formatter)
    }

    // 좋아요 여부 전송
    private fun updateLikeStatus(postNum: Int?) {
        postNum?.let {
            val userId = KakaoSDK.user.getLong("userId", 0L)
            val data = Retrofit.EditLikeStatus(userId, postNum)

            val call = RetrofitObject.getRetrofitService.editLike(data)
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    if (response.isSuccessful) {
                        btnLike.isSelected = !btnLike.isSelected

                        if (btnLike.isSelected) {
                            btnLike.setColorFilter(ContextCompat.getColor(btnLike.context, R.color.yellow))
                            // 좋아요 수 증가
                            like = (like ?: 0) + 1
                            binding.postDetailTextLike.text = like.toString()
                        } else {
                            btnLike.setColorFilter(ContextCompat.getColor(btnLike.context, R.color.hint))
                            // 좋아요 수 감소
                            like = (like ?: 0) - 1
                            binding.postDetailTextLike.text = like.toString()
                        }
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }

            })
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? ActivityMain)?.showBottomNaviagtion()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_BODY = "body"
        private const val ARG_LIKE = "like"
        private const val ARG_TAG = "tag"
        private const val ARG_IMGURL = "imgUrl"
        private const val ARG_TIME = "time"
        private const val ARG_POSTNUM = "postNum"

        fun newInstance(title: String, content: String, like: Int, tag: List<String>, imgUrl: String?, time: String, postNum: Int) =
            FragmentPostDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_BODY, content)
                    putInt(ARG_LIKE, like)
                    putStringArrayList(ARG_TAG, ArrayList(tag))
                    putString(ARG_IMGURL, imgUrl)
                    putString(ARG_TIME, time)
                    putInt(ARG_POSTNUM, postNum)
                }
            }
    }
}