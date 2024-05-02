package com.example.team_on.connection

import com.google.gson.annotations.SerializedName

class Retrofit {

    //로그인 요청
    data class RequestSignIn(
        @SerializedName("id")
        val id: String,
        @SerializedName("pw")
        val pw: String,
    )
    //이메일 인증 요청
    data class RequestMail(
        @SerializedName("mail")
        val mail: String,
    )
    //인증 번호 확인 요청
    data class RequestAuth(
        @SerializedName("mail")
        val mail: String,
        @SerializedName("authCode")
        val authCode: String
    )
    //응답
    data class ResponseSuccess(
        @SerializedName("success")
        val success: Boolean
    )
}