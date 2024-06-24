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
            val postTags = listOf(binding.postTag1, binding.postTag2, binding.postTag3)

            for (i in tags.indices) {
                if (i < tags.size) {
                    postTags[i].text = tags[i]
                    postTags[i].visibility = View.VISIBLE
                } else {
                    postTags[i].text = ""
                    postTags[i].visibility = View.GONE
                }
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