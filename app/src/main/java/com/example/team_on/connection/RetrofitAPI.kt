package com.example.team_on.connection

import com.google.android.gms.common.api.Response
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
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
    @POST("/member/login")
    fun signIn(@Body request: Retrofit.RequestSignIn): Call<Retrofit.ResponseSignIn>
    //아이디 중복 확인
    @GET("/member/join/loginid")
    fun checkId(@Query("loginId") id: String): Call<Retrofit.ResponseSuccess>
    //인증 메일 발송
    @GET("/member/sendCodeOnlyEmail")
    fun sendMail(@Query("email") email: String): Call<ResponseBody>
    //인증 번호 확인
    @GET("/member/confirm-email")
    fun checkAuth(
        @Query("email") email: String,
        @Query("emailCode") code: Int): Call<ResponseBody>
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

    //산책 등록
    @POST("/walkmate")
    fun walkPut(@Body request: Retrofit.RequestWalkPut): Call<Retrofit.ResponseSuccess>

    //게시글 전체 조회
    @GET("/board")
    fun getAllPosts2(): Call<List<Retrofit.Post2>>
    //게시글 상세 조회(댓글 조회)
    @GET("/board/{boardId}")
    fun getPost2(@Path("boardId") boardId: Long): Call<Retrofit.Post2>
    //댓글 저장
    @POST("/comments")
    fun saveComment(@Body comment:Retrofit.SaveComment): Call<Retrofit.ResponseSaveComment>
    //게시글 저장
    @Multipart
    @POST("/board")
    fun addPost(
        @Part file: MultipartBody.Part?,
        @Part("userId") userId: RequestBody,
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part("tagTypes") tagTypes: RequestBody): Call<Retrofit.Post2>
    //게시글 좋아요
    @POST("/likes")
    fun editLike(@Body data: Retrofit.EditLikeStatus): Call<Retrofit.ResponseSuccess>

    //물품 등록
    @Multipart
    @POST("/products")
    fun addProduct(
        @Part("title") title: RequestBody,
        @Part("body") body: RequestBody,
        @Part("price") price: RequestBody,
        @Part("reservationStatus") reservationStatus: RequestBody,
        @Part("saleStatus") saleStatus: RequestBody,
        @Part("tagType") tagType: RequestBody,
        @Part file: MultipartBody.Part?): Call<Retrofit.Product2>
    //모든 물품 조회
    @GET("/products")
    fun getAllProducts(): Call<List<Retrofit.Product2>>
    //물품 수정
    @PATCH("/api/products/{productId}")
    fun updateProduct(
        @Part postImage: MultipartBody.Part?,
        @Part("info") info: RequestBody): Call<Retrofit.ResponseSuccess>

    @GET("/file")
    fun loadImg(@Part id: Long): Call<Retrofit.FileInfo>
}
