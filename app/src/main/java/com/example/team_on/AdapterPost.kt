package com.example.team_on

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.team_on.connection.Retrofit
import com.example.team_on.databinding.ItemViewPostBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdapterPost(private val posts: List<Retrofit.Post>,
                  private val onItemClick: (Retrofit.Post) -> Unit
) : RecyclerView.Adapter<AdapterPost.PostViewHolder>() {

    inner class PostViewHolder(private val binding: ItemViewPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Retrofit.Post) {
            binding.postTitle.text = post.title
            binding.postContent.text = post.content
            binding.postCountLike.text = post.like.toString()
            binding.postCountComment.text = post.comment.toString()

            binding.postDate.text = formatPostTime(post.time)

            // 이미지 URL을 받아서 ImageView에 로드
            post.imgUrl?.let { url ->
                val uri = url.toUri().buildUpon().scheme("https").build()
                Glide.with(binding.postImage.context)
                    .load(uri) // URL을 URI로 변환하여 로드
                    .error(R.drawable.svg_camera)
                    .into(binding.postImage) // 이미지가 로드될 ImageView
            }

            Log.d("AdapterPost", "post: ${post}")

            val tags = post.tag
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

            // 유저의 좋아요 여부
            if (post.flag == 1) {
                binding.postImageLike.setColorFilter(ContextCompat.getColor(binding.postImageLike.context, R.color.yellow))
            } else {
                binding.postImageLike.setColorFilter(ContextCompat.getColor(binding.postImageLike.context, R.color.hint))
            }

            itemView.setOnClickListener {
                onItemClick(post)
            }
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemViewPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(posts[position])
    }

    override fun getItemCount() = posts.size
}