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
import com.example.team_on.KakaoSDK.Companion.user
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject2
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
    private var loadCommentList = mutableListOf<Retrofit.Comment2>()

    private var title: String? = null
    private var body: String? = null
    private var like: Int? = null
    private var tag: List<String>? = null
    private var imgUrl: String? = null
    private var time: String? = null
    private var postNum: Int? = null
    private var userId: Int? = null

    // 태그 매핑을 위한 Map 생성
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

        arguments?.let {
            title = it.getString(ARG_TITLE)
            body = it.getString(ARG_BODY)
            like = it.getInt(ARG_LIKE)
            tag = it.getStringArrayList(ARG_TAG)
            imgUrl = it.getString(ARG_IMGURL)
            time = it.getString(ARG_TIME)
            postNum = it.getInt(ARG_BOARDID)
            userId = it.getInt(ARG_USERID)
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
        binding.postDetailUserName.text = userId.toString()

        val tags = tag
        val postTags = listOf(binding.postDetailTag1, binding.postDetailTag2, binding.postDetailTag3)

        if (tags != null) {
            for (i in tags.indices) {
                if (i < tags.size) {
                    val displayTag = convertTagToKorean(tags[i])
                    postTags[i].text = displayTag
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

        binding.postDetailImage.setImageResource(R.drawable.svg_camera_error)


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
        postNum?.let { loadComments(it.toLong()) }
    }

    // 댓글 불러오기
    private fun loadComments(boardId: Long) {
        val call = RetrofitObject2.getRetrofitService.getPost2(boardId)
        call.enqueue(object : Callback<Retrofit.Post2> {
            override fun onResponse(call: Call<Retrofit.Post2>, response: Response<Retrofit.Post2>) {
                if (response.isSuccessful) {
                    val comments = response.body()?.comments ?: emptyList()

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

            override fun onFailure(call: Call<Retrofit.Post2>, t: Throwable) {
                Toast.makeText(context, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 댓글 등록 기능
    private fun addCommentToServer(comment: String) {
        val userId = KakaoSDK.user.getString("userId", 0.toString())


        val commentRequest = postNum?.let { userId?.let { it1 -> Retrofit.SaveComment(it.toLong(), it1.toLong(), comment) } }

        val call = commentRequest?.let { RetrofitObject2.getRetrofitService.saveComment(it) }
        call?.enqueue(object : Callback<Retrofit.ResponseSaveComment> {
            override fun onResponse(call: Call<Retrofit.ResponseSaveComment>, response: Response<Retrofit.ResponseSaveComment>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "댓글 작성에 성공했습니다.", Toast.LENGTH_SHORT).show()

                    postNum?.let { loadComments(it.toLong()) }
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
            val userId = user.getLong("userId", 0L)
            val data = Retrofit.EditLikeStatus(userId, postNum)

            val call = RetrofitObject2.getRetrofitService.editLike(data)
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
        private const val ARG_LIKE = "likeCount"
        private const val ARG_TAG = "boardTags"
        private const val ARG_IMGURL = "imgUrl"
        private const val ARG_TIME = "time"
        private const val ARG_BOARDID = "boardId"
        private const val ARG_USERID = "userId"

        fun newInstance(title: String, body: String, likeCount: Int, boardTags: List<String>, imgUrl: String?, time: String?, boardId: Int, userId: Int) =
            FragmentPostDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_BODY, body)
                    putInt(ARG_LIKE, likeCount)
                    putStringArrayList(ARG_TAG, ArrayList(boardTags))
                    putString(ARG_IMGURL, imgUrl ?: "")
                    putString(ARG_TIME, time)
                    putInt(ARG_BOARDID, boardId)
                    putInt(ARG_USERID, userId)
                }
            }
    }
}