package com.example.team_on

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.databinding.FragmentPostDetailBinding
import java.util.Date

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
    private var commentList = mutableListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        val title = arguments?.getString(ARG_TITLE)
        val content = arguments?.getString(ARG_CONTENT)

        binding.postDetailTitle.text = title
        binding.postDetailContent.text = content

        btnLike = binding.postDetailBtnLike
        btnSendComment = binding.postDetailBtnSendComment
        editComment = binding.postDetailEditComment
        recyclerView = binding.postDetailRecyclerview
        toolbar = binding.postDetailToolbar

        commentList = mutableListOf(
            Comment("User1", "This is a comment."),
            Comment("User2", "This is another comment."),
            Comment("User3", "This is third comment."),
            Comment("User4", "This is 4th comment."),
            Comment("User5", "This is last comment.")
        )

        commentAdapter = AdapterComment(commentList)

        recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }

        // 좋아요 버튼 클릭
        btnLike.setOnClickListener {
            btnLike.isSelected = !btnLike.isSelected
            if (btnLike.isSelected) {
                btnLike.setColorFilter(ContextCompat.getColor(btnLike.context, R.color.yellow))
                // 증가값, 종아요 여부 전송
            } else {
                btnLike.setColorFilter(ContextCompat.getColor(btnLike.context, R.color.hint))
                // 감소, 좋아요
            }
        }

        // 댓글 버튼 클릭
        btnSendComment.setOnClickListener {
            val newComment = editComment.text.toString()
            if (newComment.isNotBlank()) {
                addComment(Comment("User", newComment))
                editComment.text.clear()
            }
        }

        // 뒤로가기 버튼
        toolbar.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
    }

    // 댓글 등록 기능
    private fun addComment(comment: Comment) {
        commentList.add(comment)
        commentAdapter.notifyItemInserted(commentList.size - 1)
        recyclerView.scrollToPosition(commentList.size - 1)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? ActivityMain)?.showBottomNaviagtion()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_CONTENT = "content"
        fun newInstance(title: String, content: String) =
            FragmentPostDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_CONTENT, content)
                }
            }
    }
}