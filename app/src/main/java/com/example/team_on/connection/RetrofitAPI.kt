package com.example.team_on.connection

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
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
    //카카오 검색
    @GET("/v2/local/search/address.json")
    fun kakaoSearch(
        @Header("Authorization") apiKey: String,
        @Query("query") query: String,
        @Query("analyze_type") type: String): Call<Retrofit.ResponseSearch>
    //좌표 주소 변환
    @GET("/v2/local/geo/coord2address.json")
    fun kakaoAddress(
        @Header("Authorization") apiKey: String,
        @Query("x") longitude: String,
        @Query("y") latitude: String): Call<Retrofit.ResponseAddress>

    //물품 생성
    @Multipart
    @POST
    fun createProduct(
        @Part("id") id: RequestBody?,
        @Part("authorName") authorName: RequestBody,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("tags") tags: List<MultipartBody.Part>,
        @Part("createdTime") createdTime: RequestBody,
        @Part("price") price: RequestBody,
        @Part postImage: MultipartBody.Part?
    ): Call<Retrofit.ResponseSuccess>
    //모든 제품 조회
    @GET("/api/products")
    fun readProducts(): Call<List<Retrofit.Product>>
    //물품 조회
    @GET("/api/products/{id}")
    fun readProduct(@Path("id") id: Long): Call<Retrofit.Product>
    //물품 삭제
    @DELETE("/api/products/{id}")
    fun deleteProduct(@Path("id") id: Long): Call<Retrofit.ResponseSuccess>
    //물품 수정
    @PATCH("/api/products/{id}")
    fun updateProduct(
        @Part("id") id: RequestBody?,
        @Part("title") title: RequestBody,
        @Part("content") content: RequestBody,
        @Part("tags") tags: List<MultipartBody.Part>,
        @Part("modifiedTime") modifiedTime: RequestBody,
        @Part("price") price: RequestBody,
        @Part("isPreorder") isPreorder: RequestBody,
        @Part("isSold") isSold: RequestBody,
        @Part postImage: MultipartBody.Part?
    ): Call<Retrofit.ResponseSuccess>
}