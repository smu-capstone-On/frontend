package com.example.team_on

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.databinding.ItemViewPostBinding

data class Post(val title: String, val content: String, val tags: List<String>)

class AdapterPost(private val posts: List<Post>,
                  private val onItemClick: (Post) -> Unit
) : RecyclerView.Adapter<AdapterPost.PostViewHolder>() {

    inner class PostViewHolder(private val binding: ItemViewPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) {
            binding.postTitle.text = post.title
            binding.postContent.text = post.content

            val tags = post.tags

            if (tags.isNotEmpty()) {
                binding.postTag1.text = tags[0]
                binding.postTag1.visibility = View.VISIBLE
            } else {
                binding.postTag1.text = ""
                binding.postTag1.visibility = View.GONE
            }
            if (tags.size > 1) {
                binding.postTag2.text = tags[1]
                binding.postTag2.visibility = View.VISIBLE
            } else {
                binding.postTag2.text = ""
                binding.postTag2.visibility = View.GONE
            }
            if (tags.size > 2) {
                binding.postTag3.text = tags[2]
                binding.postTag3.visibility = View.VISIBLE
            } else {
                binding.postTag3.text = ""
                binding.postTag3.visibility = View.GONE
            }

            itemView.setOnClickListener {
                onItemClick(post)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemViewPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size
}