package com.example.team_on.connection

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface RetrofitAPI {

    //로그인
    @POST("/api/login")
    fun signIn(@Body request: Retrofit.RequestSignIn): Call<Retrofit.ResponseSuccess>
    //아이디 중복 확인
    @GET("/api/checkId")
    fun checkId(@Query("id") id: String): Call<Retrofit.ResponseSuccess>
    //인증 메일 발송
    @POST("/api/mail")
    fun sendMail(@Body request: Retrofit.RequestMail): Call<Retrofit.ResponseSuccess>
    //인증 번호 확인
    @POST("/api/mail/auth")
    fun checkAuth(@Body request: Retrofit.RequestAuth): Call<Retrofit.ResponseSuccess>
    //닉네임 중복 확인
    @GET("/api/checkNick")
    fun checkNick(@Query("nick") nick: String): Call<Retrofit.ResponseSuccess>
    //아이디 변경 요청
    @PATCH("/api/changeId")
    fun changeId(@Body request: Retrofit.RequestChangeId): Call<Retrofit.ResponseSuccess>
    //비밀번호 변경 요청
    @PATCH("/api/changePw")
    fun changePw(@Body request: Retrofit.RequestChangePw): Call<Retrofit.ResponseSuccess>
    //아이디 찾기
    @GET("/api/findId")
    fun findId(@Body request: Retrofit.RequestFindId): Call<Retrofit.ResponseFindId>
    //비밀번호 찾기
    @GET("/api/findPw")
    fun findPw(@Body request: Retrofit.RequestFindPw): Call<Retrofit.ResponseFindPw>
}