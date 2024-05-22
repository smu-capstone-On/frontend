package com.example.team_on

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.team_on.databinding.FragmentPostDetailBinding

class FragmentPostDetail : Fragment() {

    private var _binding: FragmentPostDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var boardType: String
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

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

        toolbar = binding.postDetailToolbar

        binding.postDetailTitle.text = title
        binding.postDetailContent.text = content

        commentList = mutableListOf(
            Comment("User1", "This is a comment."),
            Comment("User2", "This is another comment."),
            Comment("User3", "This is another comment."),
            Comment("User4", "This is another comment."),
            Comment("User5", "This is another comment.")
        )

        commentAdapter = AdapterComment(commentList)

        binding.postDetailRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = commentAdapter
        }

        toolbar.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
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