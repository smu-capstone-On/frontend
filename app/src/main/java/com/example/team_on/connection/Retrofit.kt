package com.example.team_on.connection

import com.google.gson.annotations.SerializedName

class Retrofit {
    data class RequestSignIn(
        @SerializedName("id")
        val id: String,
        @SerializedName("pw")
        val pw: String,
    )

    data class ResponseSignIn(
        @SerializedName("success")
        val success: Boolean,
        @SerializedName("token")
        val token: String
    )
}