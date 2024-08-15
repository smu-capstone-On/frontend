package com.example.team_on

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.Retrofit
import com.example.team_on.databinding.ItemViewCommentBinding

class AdapterComment (
    private val comments: List<Retrofit.Comment>
) : RecyclerView.Adapter<AdapterComment.CommentViewHolder>() {

    inner class CommentViewHolder(private val binding: ItemViewCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Retrofit.Comment) {
            binding.commentUserId.text = comment.userName
            binding.commentContent.text = comment.comment
            binding.commentDate.text = comment.createdTime.toString()
            //binding.commentUserProfile = comment.userImage
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
