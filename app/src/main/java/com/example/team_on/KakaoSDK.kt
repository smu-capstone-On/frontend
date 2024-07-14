package com.example.team_on

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk


class KakaoSDK : Application() {

    companion object {
        lateinit var searchAttribute: SharedPreferences
        private set
    }

    override fun onCreate() {
        super.onCreate()
        searchAttribute = getSharedPreferences("attribute", Context.MODE_PRIVATE)
        KakaoMapSdk.init(this, KakaoKey.Key)
        KakaoSdk.init(this, KakaoKey.Key)
    }
}