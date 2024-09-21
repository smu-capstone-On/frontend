package com.example.team_on

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.team_on.connection.Retrofit
import com.example.team_on.databinding.ItemViewPostBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AdapterPost(
    private var posts: MutableList<Retrofit.Post3>,
    private val onItemClick: (Retrofit.Post3) -> Unit,
) : RecyclerView.Adapter<AdapterPost.PostViewHolder>() {

    private val originalPosts: MutableList<Retrofit.Post3> = posts.toMutableList()

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

    inner class PostViewHolder(private val binding: ItemViewPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Retrofit.Post3) {
            binding.postTitle.text = post.title
            binding.postContent.text = post.body
            binding.postCountLike.text = post.likeCount.toString()
            binding.postCountComment.text = post.comments.size.toString()
            binding.postImage.setImageResource(0) // 초기화
            binding.postDate.text = formatPostTime(post.time)

            // 이미지 로딩
            post.fileInfo?.id?.let { _ ->
                Glide.with(binding.postImage.context)
                    .load(post.url)
                    .placeholder(R.drawable.svg_camera) // 로딩 중 표시할 이미지
                    .error(R.drawable.svg_camera_error) // 로딩 실패 시 표시할 이미지
                    .into(binding.postImage)

                binding.postImage.visibility = View.VISIBLE
            } ?: run {
                binding.postImage.visibility = View.GONE
                binding.postImage.setImageResource(R.drawable.svg_camera)
            }

            Log.d("AdapterPost", "post: ${post}")

            val tags = post.boardTags
            val postTags = listOf(binding.postTag1, binding.postTag2, binding.postTag3)

            for (i in postTags.indices) {
                if (i < tags.size) {
                    val displayTag = convertTagToKorean(tags[i])
                    postTags[i].text = displayTag
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

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterPosts: List<Retrofit.Post3>) {
        posts = if (filterPosts.isEmpty()) {
            originalPosts.toMutableList()
        } else {
            filterPosts.toMutableList()
        }
        notifyDataSetChanged()
    }
}
