package com.example.team_on

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView

class ActivityWalk : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var tvLat: TextView
    private lateinit var tvLng:TextView
    private lateinit var tvZoomLevel:TextView

    private val startZoomLevel = 15
    private val startPosition = LatLng.from(37.394660, 127.111182) // 판교역


    // MapReadyCallback 을 통해 지도가 정상적으로 시작된 후에 수신할 수 있다.
    private val readyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        override fun onMapReady(kakaoMap: KakaoMap) {
            Toast.makeText(applicationContext, "Map Start!", Toast.LENGTH_SHORT).show()
            tvLat!!.text = startPosition.getLatitude().toString()
            tvLng.setText(startPosition.getLongitude().toString())
            tvZoomLevel.setText(startZoomLevel.toString())
            Log.i(
                "k3f", "startPosition: "
                        + kakaoMap.cameraPosition!!.position.toString()
            )
            Log.i(
                "k3f", "startZoomLevel: "
                        + kakaoMap.zoomLevel
            )
        }

        override fun getPosition(): LatLng {
            return startPosition
        }

        override fun getZoomLevel(): Int {
            return startZoomLevel
        }
    }

    // MapLifeCycleCallback 을 통해 지도의 LifeCycle 관련 이벤트를 수신할 수 있다.
    private val lifeCycleCallback: MapLifeCycleCallback = object : MapLifeCycleCallback() {
        override fun onMapResumed() {
            super.onMapResumed()
        }

        override fun onMapPaused() {
            super.onMapPaused()
        }

        override fun onMapDestroy() {
            Toast.makeText(
                applicationContext, "onMapDestroy",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onMapError(error: Exception) {
            Toast.makeText(
                applicationContext, error.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_walk)
        title = "Map Start"
        tvLat = findViewById(R.id.tvLat)
        tvLng = findViewById<TextView>(R.id.tvLng)
        tvZoomLevel = findViewById<TextView>(R.id.tvZoomLevel)
        mapView = findViewById(R.id.map_view)
        mapView.start(lifeCycleCallback, readyCallback)
    }
}