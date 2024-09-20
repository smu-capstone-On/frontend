package com.example.team_on.connection

import android.content.SharedPreferences
import com.google.gson.annotations.SerializedName
import java.math.BigInteger
import java.time.LocalDateTime

class Retrofit {

    //회원가입
    data class RequestSignUp(
        @SerializedName("loginId")
        val id: String,
        @SerializedName("password")
        val pw: String,
        @SerializedName("email")
        val email: String
    )
    //로그인 요청
    data class RequestSignIn(
        @SerializedName("loginId")
        val id: String,
        @SerializedName("password")
        val pw: String,
    )
    data class ResponseSignIn(
        @SerializedName("id")
        val id: Int,
        @SerializedName("email")
        val email: String,
        @SerializedName("loginId")
        val loginId: String,
        @SerializedName("profile")
        val profile: SignIn
    )
    data class SignIn(
        @SerializedName("nickName")
        val nickName: String,
        @SerializedName("sex")
        val sex: String,
        @SerializedName("age")
        val age: Int
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
    //프로필 생성
    data class RequestProfile(
        @SerializedName("nickName")
        val nickName: String,
        @SerializedName("sex")
        val sex: String,
        @SerializedName("age")
        val age: Int,
        @SerializedName("petStatus")
        val petStatus: String
    )
    // 아이디 변경 요청
    data class RequestChangeId(
        @SerializedName("loginId")
        val oldId: String,
        @SerializedName("modifyLoginId")
        val newId: String
    )
    // 비밀번호 변경 요청
    data class RequestChangePw(
        @SerializedName("loginId")
        val id: String,
        @SerializedName("modifyPassword")
        val newPw: String
    )
    // 아이디 찾기 응답
    data class ResponseFindId(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: FindId
    )
    data class FindId(
        @SerializedName("loginId")
        val loginId: String
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
        val latitude: Double,
        @SerializedName("longitude")
        val longitude: Double,
        @SerializedName("startDateTime")
        val sTime: String,
        @SerializedName("endDateTime")
        val wTime: String,
        @SerializedName("memo")
        val memo: String?
    )

    data class MateInfo(
        val num: Int,
        val nick: String,
        val age: Int,
        val gender: String,
        val sTime: String,
        val wTime: String,
        val pet: Boolean,
        val post: String
    )

    //메이트 찾기 응답
    data class ResponseFindMate(
        @SerializedName("id")
        val id: Int,
        @SerializedName("memberId")
        val memberId: Int,
        @SerializedName("sexType")
        val sexType: String,
        @SerializedName("age")
        val age: Int,
        @SerializedName("hasPet")
        val hasPet: Boolean,
        @SerializedName("latitude")
        val latitude: Double,
        @SerializedName("longitude")
        val longitude: Double,
        @SerializedName("startDateTime")
        val startDateTime: String,
        @SerializedName("endDateTime")
        val endDateTime: String,
        @SerializedName("memo")
        val memo: String
    )
    //게시판 댓글 저장
    data class SaveComment(
        @SerializedName("boardId")
        val boardId: Long,
        @SerializedName("userId")
        val userId: Long,
        @SerializedName("body")
        val body: String
    )
    // 댓글 저장 응답
    data class ResponseSaveComment(
        @SerializedName("id")
        val id: Long,
        @SerializedName("createDate")
        val createDate: String,
        @SerializedName("modifyDate")
        val modifyDate: String,
        @SerializedName("body")
        val body: String
    )
    //좋아요 여부 전송
    data class EditLikeStatus(
        @SerializedName("memberId")
        val memberId: Long,
        @SerializedName("boardId")
        val boardId: Int
    )

    // post2
    data class Post2(
        @SerializedName("id")
        val id: Long,
        @SerializedName("title")
        val title: String,
        @SerializedName("body")
        val body: String,
        @SerializedName("likeCount")
        val likeCount: Int,
        @SerializedName("boardTags")
        val boardTags: List<String>,
        @SerializedName("comments")
        val comments: List<Comment2>,
        @SerializedName("fileInfo")
        val fileInfo: FileInfo?,
        @SerializedName("memberId")
        val memberId: Long,
        @SerializedName("createDate")
        val time: String
//        @SerializedName("flag")
//        val flag: Int,
//
    )
    // comment2
    data class Comment2(
        @SerializedName("id")
        val id: Long,
        @SerializedName("createDate")
        val createDate: String,
        @SerializedName("modifyDate")
        val modifyDate: String,
        @SerializedName("body")
        val body: String
    )
    data class Product2(
        @SerializedName("id")
        val id: Long,
        @SerializedName("createDate")
        val createDate: String,
        @SerializedName("modifyDate")
        val modifyDate: String,
        @SerializedName("title")
        val title: String,
        @SerializedName("body")
        val body: String,
        @SerializedName("price")
        val price: BigInteger,
        @SerializedName("reservationStatus")
        val reservationStatus: Boolean,
        @SerializedName("saleStatus")
        val saleStatus: Boolean,
        @SerializedName("tagType")
        val tagType: String,
        @SerializedName("fileInfo")
        val fileInfo: FileInfo?
    )
    data class FileInfo(
        @SerializedName("id")
        val id: Long,
        @SerializedName("fileName")
        val fileName: String,
        @SerializedName("fileUrl")
        var fileUrl: String
    )

    data class ResponseUserInfo(
        @SerializedName("nickName")
        val nickName: String,
        @SerializedName("sex")
        val sex: String,
        @SerializedName("age")
        var age: Int,
        @SerializedName("petStatus")
        var petStatus: String
    )
}