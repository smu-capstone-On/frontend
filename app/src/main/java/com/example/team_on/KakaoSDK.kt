package com.example.team_on

import android.app.Application
import com.kakao.vectormap.KakaoMapSdk


class KakaoSDK : Application() {

    override fun onCreate() {
        super.onCreate()

        // KakaoMapSdk.init(this, KakaoKey.Key)
    }
}