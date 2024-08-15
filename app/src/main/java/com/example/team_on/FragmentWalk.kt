package com.example.team_on

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.team_on.connection.KakaoRetrofitObject
import com.example.team_on.connection.Retrofit
import com.example.team_on.databinding.FragmentWalkBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class FragmentWalk : Fragment() {

    private lateinit var binding: FragmentWalkBinding
    private lateinit var btnAttribute: ImageButton
    private lateinit var btnSearchAddress: ImageButton
    private lateinit var addressList: MutableList<Retrofit.Documents>
    private lateinit var btnChangeJoin: ImageButton
    private lateinit var btnJoin: Button
    private lateinit var btnMapSearch: Button
    private lateinit var btnChangeSearch: ImageButton
    private lateinit var imgLoc: ImageView
    private lateinit var textAddName: TextView

    private lateinit var mapView: MapView
    private lateinit var map: KakaoMap

    private lateinit var labelLayer: LabelLayer //라벨

    private val startZoomLevel = 17 //시작 카메라 레벨

    private val startLocation = LatLng.from(37.602638,126.955252) //시작 위치

    private val readyCallback: KakaoMapReadyCallback = object : KakaoMapReadyCallback() {
        override fun onMapReady(kakaoMap: KakaoMap) {
            map = kakaoMap
            labelLayer = kakaoMap.labelManager!!.layer!!
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
            Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalkBinding.inflate(layoutInflater)

        btnAttribute = binding.fwalkBtnAttribute
        btnSearchAddress = binding.fwalkBtnSearch
        btnMapSearch = binding.fwalkBtnMapSearch
        btnChangeSearch = binding.fwalkBtnChangeSearch
        btnChangeJoin = binding.fwalkBtnChangeJoin
        btnJoin = binding.fwalkBtnJoin
        imgLoc = binding.fwalkImgLoc

        mapView = binding.fwalkMap
        mapView.start(lifeCycleCallback, readyCallback)

        btnSearchAddress.setOnClickListener {
            val key = KakaoKey.API_KEY
            val query = binding.fwalkEditSearch.text.toString()
            val call = KakaoRetrofitObject.getRetrofitService.kakaoSearch("KakaoAK $key", query, "exact")
            call.enqueue(object : Callback<Retrofit.ResponseSearch> {
                override fun onResponse(call: Call<Retrofit.ResponseSearch>, response: Response<Retrofit.ResponseSearch>) {
                    if (response.isSuccessful) {
                        Log.d("AddressRes", response.body().toString())
                        val responseBody = response.body()
                        addressList = responseBody!!.documents.toMutableList()
                        if(addressList.size == 0){
                            Toast.makeText(requireContext(), "검색결과가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }else{
                            val longitude = addressList[0].longitude //경도
                            val latitude = addressList[0].latitude //위도
                            val addressName = addressList[0].addressName

                            map.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(latitude.toDouble(), longitude.toDouble())))
                            Log.d("camera pos", map.cameraPosition.toString())
                        }
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseSearch>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        //상대방 찾는걸로 넘어갈 때
        btnChangeSearch.setOnClickListener {
            btnChangeSearch.visibility = View.GONE
            btnJoin.visibility = View.GONE
            btnChangeJoin.visibility = View.VISIBLE
            btnMapSearch.visibility = View.VISIBLE
            imgLoc.visibility = View.GONE
        }

        //위치 등록으로 넘어갈 때
        btnChangeJoin.setOnClickListener {
            btnChangeJoin.visibility = View.GONE
            btnMapSearch.visibility = View.GONE
            btnChangeSearch.visibility = View.VISIBLE
            btnJoin.visibility = View.VISIBLE
            imgLoc.visibility = View.VISIBLE
        }

        btnJoin.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog)
            val view = LayoutInflater.from(context).inflate(
                R.layout.custom_dialog_fwalk,
                null
            )

            // 다이얼로그 텍스트 설정
            builder.setView(view)
            val hour1 = view.findViewById<NumberPicker>(R.id.fwalk_d_nf_h1)
            val minute1 = view.findViewById<NumberPicker>(R.id.fwalk_d_nf_m1)
            val minute2 = view.findViewById<NumberPicker>(R.id.fwalk_d_nf_m2)
            val addressName = view.findViewById<TextView>(R.id.fwalk_d_text_address)
            val post = view.findViewById<EditText>(R.id.fwalk_d_edit_post)

            hour1.minValue = 0
            hour1.maxValue = 23
            minute1.minValue = 0
            minute2.minValue = 0
            minute1.maxValue = 59
            minute2.maxValue = 120

            post.movementMethod = ScrollingMovementMethod.getInstance()

            val alertDialog = builder.create()

            alertDialog.window?.setBackgroundDrawable(ColorDrawable(0)) // 50% 투명도 검정색

            alertDialog.show()
        }

        btnAttribute.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityAttribute::class.java))
        }
        return binding.root
    }
}