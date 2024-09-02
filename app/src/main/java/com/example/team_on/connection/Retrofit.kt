package com.example.team_on.connection

import android.content.SharedPreferences
import com.google.gson.annotations.SerializedName

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
        @SerializedName("title")
        val title: String,
        @SerializedName("content")
        val content: String,
        @SerializedName("postNum")
        val postNum: Int,
        @SerializedName("like")
        val like: Int,
        @SerializedName("comment")
        val comment: Int,
        @SerializedName("userId")
        val userId: Int,
        @SerializedName("tag")
        val tag: List<String>,
        @SerializedName("flag")
        val flag: Int,
        @SerializedName("imgUrl")
        val imgUrl: String?,
        @SerializedName("time")
        val time: String
    )
    //커뮤니티 게시글 응답
    data class ResponsePost(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: List<Post>
    )
    //커뮤니티 게시글 작성 응답
    data class ResponseChatImage(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: List<Post>
    )
    //거래게시판 물품
    data class Product(
        @SerializedName("title")
        val title: String,
        @SerializedName("body")
        val body: String,
        @SerializedName("ProductId")
        val productId: Int,
        @SerializedName("reservationStatus")
        val reservationStatus: Boolean,
        @SerializedName("saleStatus")
        val saleStatus: Boolean,
        @SerializedName("userId")
        val userId: Int,
        @SerializedName("tag")
        val tags: List<String>,
        @SerializedName("imgUrl")
        val imgUrl: String?,
        @SerializedName("time")
        val time: String,
        @SerializedName("price")
        val price: Int
    )
    // 거래게시판 물품 조회 반응
    data class ResponseProduct(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: List<Product>
    )
    //게시판 댓글 불러오기
    data class LoadComment(
        @SerializedName("userId")
        val userId: Int,
        @SerializedName("comment")
        val comment: String,
        @SerializedName("time")
        val createdTime: String
    )
    //댓글 불러오기 응답
    data class ResponseLoadComment(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: List<LoadComment>
    )
    //게시판 댓글 저장
    data class SaveComment(
        @SerializedName("boardId")
        val boardId: Int,
        @SerializedName("userId")
        val userId: Long,
        @SerializedName("body")
        val body: String
    )
    // 댓글 저장 응답
    data class ResponseSaveComment(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("data")
        val data: SaveComment
    )
    //좋아요 여부 전송
    data class EditLikeStatus(
        @SerializedName("memberId")
        val memberId: Long,
        @SerializedName("boardId")
        val boardId: Int
    )
}