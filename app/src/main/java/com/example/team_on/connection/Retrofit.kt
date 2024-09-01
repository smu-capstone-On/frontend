package com.example.team_on.connection

import com.google.gson.annotations.SerializedName
import java.io.File
import java.util.Date

class Retrofit {

    //회원가입
    data class RequestSignup(
        @SerializedName("loginId")
        val id: String,
        @SerializedName("password")
        val pw: String,
        @SerializedName("email")
        val email: String
    )

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
    //카카오 주소 검색 응답
    data class ResponseSearch(
        @SerializedName("documents")
        val documents : List<Documents>
    )
    data class Documents(
        @SerializedName("address_name")
        val addressName: String,
        @SerializedName("x")
        val longitude: String,
        @SerializedName("y")
        val latitude: String
    )

    //산책 기록
    data class WalkData(
        val date: String?,
        val time: String,
        val distance: String,
        val img: ByteArray?
    )
    //카카오 좌표 검색 응답
    data class ResponseAddress(
        @SerializedName("documents")
        val documents: List<Document>
    )
    data class Document(
        @SerializedName("road_address")
        val roadAddress: RoadAddress
    )
    data class RoadAddress(
        @SerializedName("address_name")
        val addressName: String
    )

    //산책 등록
    data class RequestWalkPut(
        @SerializedName("memberId")
        val memberId: Int,
        @SerializedName("sexType")
        val sexType: String,
        @SerializedName("age")
        val age: Int,
        @SerializedName("hasPet")
        val hasPet: Boolean,
        @SerializedName("latitude")
        val latitude: String,
        @SerializedName("longitude")
        val longitude: String,
        @SerializedName("startDateTime")
        val sTime: String,
        @SerializedName("walkTime")
        val wTime: String,
        @SerializedName("memo")
        val memo: String?
    )

    //커뮤니티 게시글
    data class Post(
        @SerializedName("id")
        val id: Long,
        @SerializedName("authorName")
        val authorName: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("tags")
        val tags: List<String>,
        @SerializedName("createdTime")
        val createdTime: Date,
        @SerializedName("likeCount")
        val likeCount: Int,
        @SerializedName("commentCount")
        val commentCount: Int,
        @SerializedName("likeByUser")
        val likeByUser: Boolean,
        @SerializedName("postImage")
        val postImage: File? = null
    )
    //거래게시판 물품
    data class Product(
        @SerializedName("id")
        val id: Long? = null,
        @SerializedName("authorName")
        val authorName: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("tags")
        val tags: List<String>,
        @SerializedName("createdTime")
        val createdTime: Date,
        @SerializedName("modifiedTime")
        val modifiedTime: Date? = null,
        @SerializedName("Price")
        val price: Int,
        @SerializedName("isPreorder")
        val isPreorder: Boolean? = null,
        @SerializedName("isSold")
        val isSold: Boolean? = null,
        @SerializedName("postImage")
        val postImage: File? = null,
    )
    //게시판 댓글
    data class Comment(
        @SerializedName("userName")
        val userName: String,
        @SerializedName("comment")
        val comment: String,
        @SerializedName("Date")
        val createdTime: Date,
        @SerializedName("userImage")
        val userImage: File? = null
    )
}