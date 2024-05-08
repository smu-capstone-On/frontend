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
    // 아이디 변경 요청
    data class RequestChangeId(
        @SerializedName("oldId")
        val oldId: String,
        @SerializedName("newId")
        val newId: String
    )
    // 비밀번호 변경 요청
    data class RequestChangePw(
        @SerializedName("id")
        val id: String,
        @SerializedName("oldPw")
        val oldPw: String,
        @SerializedName("newPw")
        val newPw: String
    )
    // 아이디 찾기
    data class RequestFindId(
        @SerializedName("mail")
        val mail: String
    )
    // 비밀번호 찾기
    data class RequestFindPw(
        @SerializedName("id")
        val id: String,
        @SerializedName("mail")
        val mail: String
    )
    // 아이디 찾기 응답
    data class ResponseFindId(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("id")
        val id: String
    )
    // 비밀번호 찾기 응답
    data class ResponseFindPw(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("pw")
        val pw: String
    )
}