package com.example.team_on

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.databinding.ItemViewCommentBinding

data class Comment(val user: String, val content: String)

class AdapterComment (
    private val comments: List<Comment>
) : RecyclerView.Adapter<AdapterComment.CommentViewHolder>() {

    inner class CommentViewHolder(private val binding: ItemViewCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.commentUserId.text = comment.user
            binding.commentContent.text = comment.content
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemViewCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount() = comments.size
}
