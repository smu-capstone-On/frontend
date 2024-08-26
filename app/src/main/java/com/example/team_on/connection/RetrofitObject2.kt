package com.example.team_on.connection

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject2 {
    private val getRetrofit by lazy {

        val clientBuilder = OkHttpClient.Builder()

        val client = clientBuilder.build()

        Retrofit.Builder()
            .baseUrl("http://34.231.37.92:8080/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getRetrofitService : RetrofitAPI by lazy { getRetrofit.create(RetrofitAPI::class.java) }
}