package com.example.team_on

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
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
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.KakaoRetrofitObject
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.databinding.FragmentWalkBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import okhttp3.ResponseBody
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
    private lateinit var cameraPos: CameraPosition
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var editSearch: EditText
    private lateinit var recyclerViewMate: RecyclerView
    private lateinit var mateAdapter: AdapterMate

    private val sharedPreference = KakaoSDK.user
    private val userId = sharedPreference.getString("userId", null)
    private val gender = sharedPreference.getString("sex", null)
    private val age = sharedPreference.getInt("age", 0)

    private lateinit var mapView: MapView
    private lateinit var map: KakaoMap

    private lateinit var labelLayer: LabelLayer //라벨

    private val startZoomLevel = 16 //시작 카메라 레벨

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
        editSearch = binding.fwalkEditSearch
        recyclerViewMate = binding.fwalkRvMate
        bottomSheetBehavior = BottomSheetBehavior.from(binding.fwalkBottomSheet)

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        // BottomSheet가 완전히 펼쳐졌을 때
                        btnMapSearch.visibility = View.GONE
                    }
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        // BottomSheet가 축소되었을 때
                        btnMapSearch.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // 슬라이드 진행 중 처리할 동작
            }
        })

        mapView = binding.fwalkMap
        mapView.start(lifeCycleCallback, readyCallback)

        btnSearchAddress.setOnClickListener {
            val key = KakaoKey.API_KEY
            val query = editSearch.text.toString()
            val call = KakaoRetrofitObject.getRetrofitService.kakaoSearch("KakaoAK $key", query, "exact")
            call.enqueue(object : Callback<Retrofit.ResponseSearch> {
                override fun onResponse(call: Call<Retrofit.ResponseSearch>, response: Response<Retrofit.ResponseSearch>) {
                    if (response.isSuccessful) {
                        editSearch.text.clear()
                        val responseBody = response.body()
                        addressList = responseBody!!.documents.toMutableList()
                        if(addressList.size == 0){
                            Toast.makeText(requireContext(), "검색결과가 존재하지 않습니다.", Toast.LENGTH_SHORT).show()
                        }else{
                            val longitude = addressList[0].longitude //경도
                            val latitude = addressList[0].latitude //위도

                            map.moveCamera(CameraUpdateFactory.newCenterPosition(LatLng.from(latitude.toDouble(), longitude.toDouble())))
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
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            btnChangeJoin.visibility = View.GONE
            btnMapSearch.visibility = View.GONE
            btnChangeSearch.visibility = View.VISIBLE
            btnJoin.visibility = View.VISIBLE
            imgLoc.visibility = View.VISIBLE
            labelLayer.removeAll()
        }

        //현재 지도에서 찾기 눌렀을 때 데모
        btnMapSearch.setOnClickListener {
            val LabelStyle = map.labelManager?.addLabelStyles(LabelStyles.from(LabelStyle.from(R.drawable.icon)
                .setTextStyles(45, Color.parseColor("#000000"))
                .setAnchorPoint(0.5f,1f)))
            cameraPos = map.cameraPosition!!
            val longitude = cameraPos.position.longitude
            val latitude = cameraPos.position.latitude
            labelLayer.removeAll()
            val call = RetrofitObject2.getRetrofitService.findMate(latitude,longitude)
            call.enqueue(object : Callback<List<Retrofit.ResponseFindMate>> {
                override fun onResponse(call: Call<List<Retrofit.ResponseFindMate>>, response: Response<List<Retrofit.ResponseFindMate>>) {
                    if (response.isSuccessful) {
                        val matesList = response.body()
                        if (matesList!!.isNotEmpty()) {
                            var count = 1
                            val mateList = mutableListOf<Retrofit.MateInfo>()
                            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                            recyclerViewMate.layoutManager =
                                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                            mateAdapter = AdapterMate(mateList)
                            recyclerViewMate.adapter = mateAdapter
                            // 배열 데이터 출력 또는 처리
                            for (mate in matesList) {
                                val call2 = RetrofitObject2.getRetrofitService.searchUser((mate.memberId+1).toString())
                                call2.enqueue(object : Callback<Retrofit.ResponseUserInfo> {
                                    @SuppressLint("NotifyDataSetChanged")
                                    override fun onResponse(call: Call<Retrofit.ResponseUserInfo>, response: Response<Retrofit.ResponseUserInfo>) {
                                        if (response.isSuccessful) {
                                            val responseBody = response.body()
                                            if(responseBody != null){
                                                val userNick = responseBody.nickName
                                                mateList.add(Retrofit.MateInfo(count, userNick, mate.age, mate.sexType, mate.startDateTime, mate.endDateTime, mate.hasPet, mate.memo))
                                                labelLayer.addLabel(LabelOptions.from(count.toString(), LatLng.from(mate.latitude,mate.longitude)).setStyles(LabelStyle).setTexts(count.toString()))
                                                count++
                                                recyclerViewMate.adapter?.notifyDataSetChanged()
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<Retrofit.ResponseUserInfo>, t: Throwable) {
                                        val errorMessage = "Call Failed: ${t.message}"
                                        Log.d("Retrofit", errorMessage)
                                    }
                                })
                            }
                        } else {
                            Toast.makeText(requireContext(), "검색된 사용자가 없습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<List<Retrofit.ResponseFindMate>>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        btnJoin.setOnClickListener {
            cameraPos = map.cameraPosition!!
            val key = KakaoKey.API_KEY
            var roadAdd = "알 수 없음"
            val longitude = cameraPos.position.longitude.toString()
            val latitude = cameraPos.position.latitude.toString()
            val call = KakaoRetrofitObject.getRetrofitService.kakaoAddress("KakaoAK $key", longitude, latitude)
            call.enqueue(object : Callback<Retrofit.ResponseAddress> {
                @SuppressLint("CutPasteId", "DefaultLocale")
                override fun onResponse(call: Call<Retrofit.ResponseAddress>, response: Response<Retrofit.ResponseAddress>) {
                    if (response.isSuccessful) {
                        val arr = response.body()?.documents!![0].roadAddress
                        if(arr != null){
                            roadAdd = arr.addressName
                        }
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
                        val radioGroup = view.findViewById<RadioGroup>(R.id.fwalk_d_radio_group)
                        val button = view.findViewById<Button>(R.id.fwalk_d_btn_save)
                        var pet = false

                        hour1.minValue = 0
                        hour1.maxValue = 23
                        minute1.minValue = 0
                        minute2.minValue = 0
                        minute1.maxValue = 59
                        minute2.maxValue = 120
                        addressName.text = roadAdd

                        post.movementMethod = ScrollingMovementMethod.getInstance()

                        val alertDialog = builder.create()

                        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0)) // 50% 투명도 검정색

                        radioGroup.setOnCheckedChangeListener { _, checkedId ->
                            pet = when (checkedId) {
                                R.id.fwalk_d_radio_true -> true // '있음' 버튼의 ID
                                R.id.fwalk_d_radio_false -> false  // '없음' 버튼의 ID
                                else -> {
                                    Toast.makeText(requireContext(), "반려동물 유무를 선택해주세요.", Toast.LENGTH_SHORT).show()
                                    false
                                }
                            }
                        }

                        button.setOnClickListener {
                            val text = post.text?.toString() ?: ""

                            val sTime = "2024-01-01T00:"+String.format("%02d:%02d", hour1.value, minute1.value)
                            val totalMinutes = minute2.value
                            val hours = totalMinutes / 60
                            val minutes = totalMinutes % 60
                            val wTime = "2024-01-01T00:"+String.format("%02d:%02d", hours, minutes)

                            val call2 = RetrofitObject2.getRetrofitService.walkPut(Retrofit.RequestWalkPut(userId!!.toInt(),gender!!,age,pet, latitude.toDouble(), longitude.toDouble(), sTime, wTime, text))
                            call2.enqueue(object : Callback<ResponseBody> {
                                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                                    if (response.isSuccessful) {
                                        alertDialog.dismiss()
                                        Toast.makeText(requireContext(), "등록되었습니다!", Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                                    val errorMessage = "Call Failed: ${t.message}"
                                    Log.d("Retrofit", errorMessage)
                                }
                            })
                        }

                        alertDialog.show()
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseAddress>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        btnAttribute.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityAttribute::class.java))
        }
        return binding.root
    }
}