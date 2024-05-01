package com.example.team_on.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetrofitAPI {

    @POST("/api/login")
    fun signIn(@Body request: Retrofit.RequestSignIn): Call<Retrofit.ResponseSignIn>
}