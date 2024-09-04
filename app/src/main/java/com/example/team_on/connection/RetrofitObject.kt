package com.example.team_on.connection

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject {
    private val getRetrofit by lazy {

        val clientBuilder = OkHttpClient.Builder()

        val client = clientBuilder.build()

        Retrofit.Builder()
            .baseUrl("https://ee10b2a8-7452-4ceb-be77-3ae52bb90552.mock.pstmn.io/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getRetrofitService: RetrofitAPI by lazy { getRetrofit.create(RetrofitAPI::class.java) }
}