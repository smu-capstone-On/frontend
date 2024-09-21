package com.example.team_on

import DatabaseWalk
import android.annotation.SuppressLint
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
import com.bumptech.glide.Glide
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.databinding.FragmentHomeBinding
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class FragmentHome : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var btnGoWalk: Button
    private lateinit var btnGoCalendar: ImageButton
    private lateinit var btnGoCommunity: Button
    private lateinit var btnGoDeal: Button

    private val databaseWalk: DatabaseWalk by lazy{ DatabaseWalk.getInstance(requireContext()) }

    @SuppressLint("DefaultLocale")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)

        btnGoWalk = binding.homeBtnGoWalk
        btnGoCalendar = binding.homeBtnGoCalendar
        btnGoCommunity = binding.homeBtnGoCommunity
        btnGoDeal = binding.homeBtnGoDeal

        val today = getCurrentDate()
        val currentData = databaseWalk.getData(today)
        if(currentData !=null){
            val time = currentData.time.toInt()
            binding.homeTextTime.text = convertSecondsToHMS(time)
            val distanceInKm = currentData.distance.toFloat() / 1000
            val distance = String.format("%.2f", distanceInKm)
            binding.homeTextDistance.text = distance
        }

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
                    val topPosts = posts.sortedByDescending { it.likeCount }

                    if (topPosts.size >= 2) {
                        adapterPost(topPosts[0].title, topPosts[0].body, topPosts[0].time, "https://i.ibb.co/7JpMhwv/images.jpg", binding.homePostTitle1, binding.homePostBody1, binding.homePostTime1, binding.homePostImg1, binding.homeViewPopularPost1)
                        adapterPost(topPosts[0].title, topPosts[0].body, topPosts[0].time, "https://i.ibb.co/7JpMhwv/images.jpg", binding.homePostTitle2, binding.homePostBody2, binding.homePostTime2, binding.homePostImg2, binding.homeViewPopularPost2)
                    }
//                    else if (topPosts.size == 1) {
//                        adapterPost(topPosts[0].title, topPosts[0].body, topPosts[0].time, "", binding.homePostTitle1, binding.homePostBody1, binding.homePostTime1, binding.homePostImg1, binding.homeViewPopularPost1)
//                    }
                }
            }

            override fun onFailure(call: Call<List<Retrofit.Post2>>, t: Throwable) {
                Toast.makeText(context, "인기 게시글 로드 실패", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun adapterPost(postTitle: String, postBody: String, postTime:String, postUrl: String, title: TextView, body: TextView, date: TextView, image: ImageView, view: ImageView) {
        title.text = postTitle
        title.visibility = View.VISIBLE
        body.text = postBody
        body.visibility = View.VISIBLE
        date.text = formatPostTime(postTime)
        date.visibility = View.VISIBLE
        view.visibility = View.VISIBLE
        image.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(postUrl)
            .placeholder(R.drawable.svg_camera) // 로딩 중 표시할 이미지
            .error(R.drawable.svg_camera_error) // 로딩 실패 시 표시할 이미지
            .into(image)
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
                            adapterProduct(recentProducts[0].title, recentProducts[0].price.toString(), recentProducts[0].createDate, "https://i.ibb.co/7JpMhwv/images.jpg", binding.homeDealTitle1, binding.homeDealPrice1, binding.homeDealDate1, binding.homeDealImg1)
                            adapterProduct(recentProducts[0].title, recentProducts[0].price.toString(), recentProducts[0].createDate, "https://i.ibb.co/7JpMhwv/images.jpg", binding.homeDealTitle2, binding.homeDealPrice2, binding.homeDealDate2, binding.homeDealImg2)
                            adapterProduct(recentProducts[0].title, recentProducts[0].price.toString(), recentProducts[0].createDate, "https://i.ibb.co/7JpMhwv/images.jpg", binding.homeDealTitle3, binding.homeDealPrice3, binding.homeDealDate3, binding.homeDealImg3)
                        }
//                        2 -> {
//                            adapterProduct(recentProducts[0].title, recentProducts[0].price.toString(), recentProducts[0].createDate, "", binding.homeDealTitle1, binding.homeDealPrice1, binding.homeDealDate1, binding.homeDealImg1)
//                            adapterProduct(recentProducts[0].title, recentProducts[0].price.toString(), recentProducts[0].createDate, "", binding.homeDealTitle2, binding.homeDealPrice2, binding.homeDealDate2, binding.homeDealImg2)
//                        }
//                        1 -> {
//                            adapterProduct(recentProducts[0].title, recentProducts[0].price.toString(), recentProducts[0].createDate, "", binding.homeDealTitle1, binding.homeDealPrice1, binding.homeDealDate1, binding.homeDealImg1)
//                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Retrofit.Product2>>, t: Throwable) {
                Toast.makeText(context, "물품 로드 실패", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun adapterProduct(productTitle:String, productPrice:String, productDate:String, productUrl:String, title: TextView, price: TextView, date: TextView, image: ImageView) {
        title.text = productTitle
        title.visibility = View.VISIBLE
        price.text = productPrice + "원"
        price.visibility = View.VISIBLE
        date.text = formatPostTime(productDate)
        date.visibility = View.VISIBLE
        image.visibility = View.VISIBLE
        Glide.with(requireContext())
            .load(productUrl)
            .placeholder(R.drawable.svg_camera) // 로딩 중 표시할 이미지
            .error(R.drawable.svg_camera_error) // 로딩 실패 시 표시할 이미지
            .into(image)
    }

    private fun formatPostTime(dateString: String): String {
        val dateTime = LocalDateTime.parse(dateString)
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd  HH:mm")
        return dateTime.format(formatter)
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        return dateFormat.format(calendar.time)
    }

    @SuppressLint("DefaultLocale")
    fun convertSecondsToHMS(seconds: Int): String {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format("%02d:%02d:%02d", hours, minutes, secs)
    }
}