package com.example.team_on.connection

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object KakaoRetrofitObject {
    private val getRetrofit by lazy {

        val clientBuilder = OkHttpClient.Builder()

        val client = clientBuilder.build()

        Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getRetrofitService : RetrofitAPI by lazy { getRetrofit.create(RetrofitAPI::class.java) }
}