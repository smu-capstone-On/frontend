package com.example.team_on

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FragmentHome : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var btnGoWalk: Button
    private lateinit var btnGoCalendar: ImageButton
    private lateinit var btnGoCommunity: Button
    private lateinit var btnGoDeal: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        
        btnGoWalk = binding.homeBtnGoWalk
        btnGoCalendar = binding.homeBtnGoCalendar
        btnGoCommunity = binding.homeBtnGoCommunity
        btnGoDeal = binding.homeBtnGoDeal

        btnGoWalk.setOnClickListener{
            val intent = Intent(requireContext(), ActivityWalk::class.java)
            startActivity(intent)
        }

        btnGoCalendar.setOnClickListener{
            val intent = Intent(requireContext(), ActivityCalendar::class.java)
            startActivity(intent)
        }

        btnGoCommunity.setOnClickListener {
            (activity as ActivityMain).bnv.selectedItemId = R.id.nav_bottom_community
        }

        btnGoDeal.setOnClickListener {
            (activity as ActivityMain).bnv.selectedItemId = R.id.nav_bottom_deal
        }

        loadPost()
        loadItem()
        return binding.root
    }

    private fun loadPost() {
        val call = RetrofitObject2.getRetrofitService.getAllPosts2()
        call.enqueue(object : retrofit2.Callback<List<Retrofit.Post2>> {
            override fun onResponse(call: Call<List<Retrofit.Post2>>, response: Response<List<Retrofit.Post2>>) {
                if (response.isSuccessful) {
                    val posts = response.body() ?: emptyList()
                    val topPosts = posts.sortedByDescending { it.likeCount }.take(2)

                    if (topPosts.size == 2) {
                        adapterPost(topPosts[0], binding.homePostTitle1, binding.homePostBody1, binding.homePostTime1, binding.homePostImg1, binding.homeViewPopularPost1)
                        adapterPost(topPosts[1], binding.homePostTitle2, binding.homePostBody2, binding.homePostTime2, binding.homePostImg2, binding.homeViewPopularPost2)
                    } else if (topPosts.size == 1) {
                        adapterPost(topPosts[0], binding.homePostTitle1, binding.homePostBody1, binding.homePostTime1, binding.homePostImg1, binding.homeViewPopularPost1)
                    }
                }
            }

            override fun onFailure(call: Call<List<Retrofit.Post2>>, t: Throwable) {
                Toast.makeText(context, "인기 게시글 로드 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun adapterPost(post: Retrofit.Post2, title: TextView, body: TextView, date: TextView, image: ImageView, view: ImageView) {
        title.text = post.title
        title.visibility = View.VISIBLE
        body.text = post.body
        body.visibility = View.VISIBLE
        date.text = formatPostTime(post.time)
        date.visibility = View.VISIBLE
        view.visibility = View.VISIBLE
        image.visibility = View.VISIBLE
    }

    private fun loadItem() {
        val call = RetrofitObject2.getRetrofitService.getAllProducts()
        call.enqueue(object : retrofit2.Callback<List<Retrofit.Product2>> {
            override fun onResponse(call: Call<List<Retrofit.Product2>>, response: Response<List<Retrofit.Product2>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    val recentProducts = products.sortedByDescending { it.createDate }.take(3)

                    if (recentProducts.isNotEmpty()) {
                        binding.homeRelativeLayout.visibility = View.VISIBLE
                    }

                    when (recentProducts.size) {
                        3 -> {
                            adapterProduct(recentProducts[0], binding.homeDealTitle1, binding.homeDealPrice1, binding.homeDealDate1, binding.homeDealImg1)
                            adapterProduct(recentProducts[1], binding.homeDealTitle2, binding.homeDealPrice2, binding.homeDealDate2, binding.homeDealImg2)
                            adapterProduct(recentProducts[2], binding.homeDealTitle3, binding.homeDealPrice3, binding.homeDealDate3, binding.homeDealImg3)
                        }
                        2 -> {
                            adapterProduct(recentProducts[0], binding.homeDealTitle1, binding.homeDealPrice1, binding.homeDealDate1, binding.homeDealImg1)
                            adapterProduct(recentProducts[1], binding.homeDealTitle2, binding.homeDealPrice2, binding.homeDealDate2, binding.homeDealImg2)
                        }
                        1 -> {
                            adapterProduct(recentProducts[0], binding.homeDealTitle1, binding.homeDealPrice1, binding.homeDealDate1, binding.homeDealImg1)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Retrofit.Product2>>, t: Throwable) {
                Toast.makeText(context, "물품 로드 실패", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun adapterProduct(product2: Retrofit.Product2, title: TextView, price: TextView, date: TextView, image: ImageView) {
        title.text = product2.title
        title.visibility = View.VISIBLE
        price.text = product2.price.toString() + "원"
        price.visibility = View.VISIBLE
        date.text = formatPostTime(product2.createDate)
        date.visibility = View.VISIBLE
        image.visibility = View.VISIBLE
    }

    private fun formatPostTime(dateString: String): String {
        val dateTime = LocalDateTime.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd  HH:mm")
        return dateTime.format(formatter)
    }
}