
package com.example.team_on

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.team_on.databinding.ActivityWalkBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.GestureType
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.TrackingManager


class ActivityWalk : AppCompatActivity() {

    private val binding: ActivityWalkBinding by lazy { ActivityWalkBinding.inflate(layoutInflater) }

    private lateinit var mapView: MapView
    private lateinit var map: KakaoMap

    private val startZoomLevel = 17
    private val startLocation = LatLng.from(37.602638,126.955252)
    private lateinit var userPosition: LatLng
    private lateinit var labelLayer: LabelLayer
    private lateinit var trackingManager: TrackingManager
    private lateinit var trackingLabel: Label
    private lateinit var btnPosition: ImageButton
    private var locationAble = false

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    private lateinit var timeText: TextView
    private var seconds = 0
    private var cnt = 0
    private val handler = Handler(Looper.getMainLooper())
    private val locationHandler = Handler(Looper.getMainLooper())
    private var startTime = 0L

    private lateinit var distanceText: TextView
    private lateinit var speedText: TextView
    private var lastLocation: Location? = null
    private var totalDistance = 0f

    private val runnable = object : Runnable {
        override fun run() {
            handler.postDelayed(this, 1000)
            val currentTime = SystemClock.elapsedRealtime()
            seconds = ((currentTime - startTime) / 1000).toInt()
            updateTimerText()
        }
    }

    private val locationRunnable = object : Runnable {
        override fun run(){
            locationHandler.postDelayed(this, 5000)
            val newLocation = Location("newLocation").apply {
                latitude = userPosition.latitude
                longitude = userPosition.longitude
            }
            val distance = lastLocation!!.distanceTo(newLocation)
            totalDistance += distance
            distanceText.text = totalDistance.toInt().toString()
            lastLocation = newLocation
            if(seconds != 0){
                val speed = (totalDistance/seconds)
                speedText.text = String.format("%.2f", speed)
                Log.d("distance", userPosition.toString())
            }
            rootLabel(userPosition,(seconds).toString()+"user")
        }
    }

    private fun rootLabel(pos: LatLng, id: String) {
        // 라벨 스타일 생성
        val styles = map.labelManager?.addLabelStyles(LabelStyles.from(
            LabelStyle.from(R.drawable.circle17).setZoomLevel(11),
            LabelStyle.from(R.drawable.circle17).setZoomLevel(13),
            LabelStyle.from(R.drawable.circle17).setZoomLevel(15),
            LabelStyle.from(R.drawable.circle17).setZoomLevel(17),
            LabelStyle.from(R.drawable.circle19).setZoomLevel(19)))

        labelLayer.addLabel(LabelOptions.from(id, pos).setStyles(styles))
        labelLayer.getLabel(id).changeRank(0)
    }

    //산책 시간 나타내기
    private fun updateTimerText() {
        val hours = seconds / 3600
        val minutes = (seconds % 3600) / 60
        val secs = seconds % 60
        timeText.text = String.format("%02d : %02d : %02d", hours, minutes, secs)
    }

    //현재 위치 보여주기
    private fun showLabel(pos: LatLng) {
        Log.d("kakaomap", "실행 됨")
        // 라벨 스타일 생성
        val styles = map.labelManager?.addLabelStyles(LabelStyles.from(
            LabelStyle.from(R.drawable.circle_red_17).setZoomLevel(11),
            LabelStyle.from(R.drawable.circle_red_17).setZoomLevel(13),
            LabelStyle.from(R.drawable.circle_red_17).setZoomLevel(15),
            LabelStyle.from(R.drawable.circle_red_17).setZoomLevel(17),
            LabelStyle.from(R.drawable.circle_red_19).setZoomLevel(19)))

        if (::trackingLabel.isInitialized) {
            trackingLabel.moveTo(pos)
        }

        // 라벨 생성
        trackingLabel = labelLayer.addLabel(LabelOptions.from("curPos", pos).setStyles(styles))
        labelLayer.getLabel("curPos").changeRank(1)
    }

    //현재 위치 가져오기
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.create().apply {
            interval = 1000 // 5초마다 위치 업데이트
            fastestInterval = 1000 // 가장 빠른 업데이트 간격
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY // 높은 정확도
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return // 위치 정보가 없다면 종료
                for (location in locationResult.locations) {
                    // 새 위치를 받을 때마다 카카오맵에 위치 업데이트
                    userPosition = LatLng.from(location.latitude, location.longitude)
                    showLabel(userPosition)
                    locationAble = true
                }
            }
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) { return }

        // 위치 업데이트 요청
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    // MapReadyCallback 을 통해 지도가 정상적으로 시작된 후에 수신할 수 있다.
    private val readyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        override fun onMapReady(kakaoMap: KakaoMap) {
            this@ActivityWalk.map = kakaoMap
            labelLayer = kakaoMap.labelManager!!.layer!!
            trackingManager = kakaoMap.trackingManager!!
            map.setOnCameraMoveStartListener{ _, gestureType ->
                if (gestureType != GestureType.Unknown) {
                    trackingManager.stopTracking()
                    btnPosition.alpha = 0.5f
                }
            }

            getLocation()
        }

        override fun getPosition(): LatLng {
            return startLocation
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

        }

        override fun onMapError(error: Exception) {
            Toast.makeText(
                applicationContext, error.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission() // 권한이 있을 경우 위치 정보 요청
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        }

        val btnStart = binding.awalkBtnStart
        val btnPlay = binding.awalkBtnPlay
        val btnPause = binding.awalkBtnPause
        val btnWrite = binding.awalkBtnWrite
        btnPosition = binding.awalkBtnMyPosition
        timeText = binding.awalkTextTime
        distanceText = binding.awalkTextDistance
        speedText = binding.awalkTextSpeed

        btnStart.setOnClickListener {
            if (locationAble){
                btnStart.visibility = View.GONE
                btnPause.visibility = View.VISIBLE
                btnWrite.visibility = View.VISIBLE
                startTime = SystemClock.elapsedRealtime()
                handler.post(runnable)
                lastLocation = Location("lastLocation").apply {
                    latitude = userPosition.latitude
                    longitude = userPosition.longitude
                }
                locationHandler.post(locationRunnable)
            }else{
                Toast.makeText(this, "잠시만 기다려주세요.", Toast.LENGTH_LONG).show()
            }
        }

        btnPause.setOnClickListener {
            btnPause.visibility = View.GONE
            btnPlay.visibility = View.VISIBLE
            handler.removeCallbacks(runnable)
            locationHandler.removeCallbacks(locationRunnable)
        }

        btnPlay.setOnClickListener {
            btnPause.visibility = View.VISIBLE
            btnPlay.visibility = View.GONE
            handler.post(runnable)
            locationHandler.post(locationRunnable)
        }

        btnWrite.setOnClickListener {
            btnPause.visibility = View.GONE
            btnPlay.visibility = View.GONE
            btnWrite.visibility = View.GONE
            btnStart.visibility = View.VISIBLE
            handler.removeCallbacks(runnable)
            locationHandler.removeCallbacks(locationRunnable)
            cnt = 0
            seconds = 0
            speedText.text = "0"
            distanceText.text = "0"
            updateTimerText()
        }

        btnPosition.setOnClickListener {
            if (locationAble){
                trackingManager.startTracking(trackingLabel)
                btnPosition.alpha = 1f
            }else{
                Toast.makeText(this, "잠시만 기다려주세요.", Toast.LENGTH_LONG).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable)
        locationHandler.removeCallbacks(locationRunnable)
        fusedLocationClient.removeLocationUpdates(locationCallback)
        Log.d("MapView", "종료 됨")
    }

    //권한 확인
    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 권한이 부여되지 않은 경우 사용자에게 권한 요청
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            // 권한이 이미 허용된 경우, 위치 관련 작업 수행
            performLocationTask()
        }
    }

    //권한 허용 또는 거부했을 때
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // 사용자가 권한을 허용했을 경우, 위치 관련 작업 수행
                    performLocationTask()
                } else {
                    // 사용자가 권한을 거부했을 경우, 권한 요구 사유 설명 필요
                    Toast.makeText(this, "위치 권한이 없으면 위치 기반 기능을 사용할 수 없습니다.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    //허용했을 때 로직
    private fun performLocationTask() {
        mapView = binding.mapView
        mapView.start(lifeCycleCallback, readyCallback)
    }

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            handler.removeCallbacks(runnable)
            val intent = Intent(this@ActivityWalk, ActivityMain::class.java)
            startActivity(intent)
            finish()
        }
    }
}