package com.example.team_on

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk


class KakaoSDK : Application() {

    companion object {
        lateinit var user: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        KakaoMapSdk.init(this, KakaoKey.Key)
        KakaoSdk.init(this, KakaoKey.Key)

        user = getSharedPreferences("user", Context.MODE_PRIVATE)
    }
}