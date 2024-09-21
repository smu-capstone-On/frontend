package com.example.team_on.connection

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitObject3 {
    private val getRetrofit by lazy {

        val clientBuilder = OkHttpClient.Builder()

        val client = clientBuilder.build()

        Retrofit.Builder()
            .baseUrl("https://c750520c-7075-4895-8edc-88e20729d161.mock.pstmn.io")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val getRetrofitService : RetrofitAPI by lazy { getRetrofit.create(RetrofitAPI::class.java) }
}