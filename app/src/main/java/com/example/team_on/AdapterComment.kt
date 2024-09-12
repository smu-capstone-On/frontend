package com.example.team_on

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.Retrofit
import com.example.team_on.databinding.ItemViewCommentBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdapterComment (
    private val loadComments: List<Retrofit.Comment2>
) : RecyclerView.Adapter<AdapterComment.CommentViewHolder>() {

    inner class CommentViewHolder(private val binding: ItemViewCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(loadComment: Retrofit.Comment2) {
            binding.commentUserId.text = loadComment.id.toString()
            binding.commentContent.text = loadComment.body
            //binding.commentUserProfile = comment.userImage

            binding.commentDate.text = formatPostTime(loadComment.modifyDate)
        }

        fun formatPostTime(dateString: String): String {
            // 문자열을 LocalDateTime 객체로 파싱
            val dateTime = LocalDateTime.parse(dateString)

            // 원하는 형식으로 변환하기 위한 포맷
            val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd   HH:mm")

            // 포맷팅된 문자열 반환
            return dateTime.format(formatter)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemViewCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(loadComments[position])
    }

    override fun getItemCount() = loadComments.size
}
