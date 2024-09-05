package com.example.team_on

import DatabaseWalk
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.example.team_on.databinding.ActivityCalendarBinding
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import java.text.SimpleDateFormat
import java.util.Calendar

class ActivityCalendar : AppCompatActivity() {

    private val binding: ActivityCalendarBinding by lazy { ActivityCalendarBinding.inflate(layoutInflater) }

    private lateinit var btnCal: ImageButton
    private lateinit var calConst: ConstraintLayout
    private lateinit var calendar: MaterialCalendarView
    private lateinit var calTextDate: TextView
    private lateinit var calTextTime: TextView
    private lateinit var calTextDistance: TextView
    private lateinit var calTextSpeed: TextView
    private lateinit var btnNext: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var calImg: ImageView
    private var tf = false

    private val databaseWalk: DatabaseWalk by lazy{ DatabaseWalk.getInstance(applicationContext) }

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        btnCal = binding.calBtnCal
        btnNext = binding.calBtnGraph
        btnBack = binding.calBtnBack
        calConst = binding.calConst
        calendar = binding.calCal
        calTextDate = binding.calTextDate
        calTextTime = binding.calTextTime
        calTextDistance = binding.calTextDis
        calTextSpeed = binding.calTextSpeed
        calImg = binding.calImgMap

        val today = getCurrentDate()
        calTextDate.text = today
        val currentData = databaseWalk.getData(today)
        if(currentData !=null){
            val time = currentData.time.toInt()
            calTextTime.text = (time/60).toString()
            val distanceInKm = currentData.distance.toFloat() / 1000
            val distance = String.format("%.2f", distanceInKm)
            calTextDistance.text = distance
            val Speed = distanceInKm.toDouble()/(time.toDouble()/3600)
            val newSpeed = String.format("%.2f", Speed)
            calTextSpeed.text = newSpeed
            val widthPx = dpToPx(this, 300)
            Glide.with(this)
                .load(currentData.img)
                .override(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)  // 가로를 300dp로 제한
                .transform(FitCenter())  // 세로 비율 유지
                .into(calImg)
            calImg.clipToOutline = true
            calImg.visibility = View.VISIBLE
        }

        btnCal.setOnClickListener {
            if(tf){
                calConst.visibility = View.GONE
                tf = false
            }else{
                calConst.visibility = View.VISIBLE
                tf = true
            }
        }

        calendar.setOnDateChangedListener{ _, date, _ ->
            // 날짜가 선택되었을 때 호출되는 부분
            val selectedDate = date.date.toString()
            val dateText = selectedDate.replace("-",".")
            val info = databaseWalk.getData(dateText)
            if(info == null){
                calTextTime.text = "0"
                calTextDistance.text = "0"
                calTextSpeed.text = "0"
                calImg.visibility = View.GONE
            }else{
                val time = info.time.toInt()
                calTextTime.text = (time/60).toString()
                val distanceInKm = info.distance.toFloat() / 1000
                val distance = String.format("%.2f", distanceInKm)
                calTextDistance.text = distance
                val Speed = distance.toFloat() / (time.toFloat()/3600)
                val newSpeed = String.format("%.2f", Speed)
                calTextSpeed.text = newSpeed
                val widthPx = dpToPx(this, 300)
                Glide.with(this)
                    .load(info.img)
                    .override(widthPx, ViewGroup.LayoutParams.WRAP_CONTENT)  // 가로를 300dp로 제한
                    .transform(FitCenter())  // 세로 비율 유지
                    .into(calImg)
                calImg.clipToOutline = true
                calImg.visibility = View.VISIBLE
            }
            calTextDate.text = dateText
            calConst.visibility = View.GONE
        }

        btnNext.setOnClickListener {
            startActivity(Intent(this@ActivityCalendar, ActivityCalendarV2::class.java))
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("yyyy.MM.dd")
        return dateFormat.format(calendar.time)
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}