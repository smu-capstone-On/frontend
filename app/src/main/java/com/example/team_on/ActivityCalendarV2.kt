package com.example.team_on

import DatabaseWalk
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.team_on.databinding.ActivityCalendarV2Binding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.prolificinteractive.materialcalendarview.MaterialCalendarView

class ActivityCalendarV2 : AppCompatActivity() {

    private val binding: ActivityCalendarV2Binding by lazy { ActivityCalendarV2Binding.inflate(layoutInflater) }
    private lateinit var chart: BarChart
    private lateinit var cal: MaterialCalendarView
    private lateinit var btnCal: ImageButton
    private lateinit var btnSelect: Button
    private lateinit var calConst: ConstraintLayout
    private lateinit var calText: TextView
    private var calVisible = false

    private val databaseWalk: DatabaseWalk by lazy{ DatabaseWalk.getInstance(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        chart = binding.cal2Chart
        cal = binding.cal2Calendar
        cal.selectionMode = MaterialCalendarView.SELECTION_MODE_RANGE
        btnCal = binding.cal2BtnCal
        btnSelect = binding.cal2BtnSelect
        calConst = binding.cal2Const
        calText = binding.cal2TextDate

        initializeChart()

        btnCal.setOnClickListener {
            if(calVisible){
                calConst.visibility = View.GONE
                calVisible = false
            }else{
                calConst.visibility = View.VISIBLE
                calVisible = true
            }
        }

        btnSelect.setOnClickListener {
            updateChartData()
        }

        binding.cal2BtnBack.setOnClickListener {
            finish()
        }
    }

    private fun initializeChart() {
        // 초기 차트 설정 (데이터 없이 설정)
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
        }

        // X축 설정
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            spaceMax = 1f
            spaceMin = 1f
        }

        // 오른쪽 Y축 설정
        chart.axisRight.apply {
            axisMinimum = 0f
            setDrawAxisLine(false)
            isEnabled = true
        }

        // 왼쪽 Y축 설정
        chart.axisLeft.apply {
            axisMinimum = 0f
            setDrawAxisLine(false)
            isEnabled = true
        }
    }

    @SuppressLint("DefaultLocale")
    private fun updateChartData() {
        val dates = cal.selectedDates
        if (dates.size > 20) {
            Toast.makeText(this@ActivityCalendarV2, "20개 이하의 날짜만 선택 가능합니다.", Toast.LENGTH_SHORT).show()
        } else if (dates.size == 1){
            Toast.makeText(this@ActivityCalendarV2, "이틀 이상의 날짜를 선택해 주세요.", Toast.LENGTH_SHORT).show()
        } else if (dates.isNotEmpty()) {
            val barEntries = ArrayList<BarEntry>()
            val startDate = dates.first().date.toString().replace("-", ".")
            val endDate = dates.last().date.toString().replace("-", ".")
            var totalTime = 0
            var totalDistance = 0
            val total = dates.size
            calText.text = "$startDate-$endDate"
            val dateArray = mutableListOf<String>()

            for ((index, date) in dates.withIndex()) {
                val curDate = date.date.toString().replace("-", ".")
                dateArray.add(curDate.takeLast(5))
                val data = databaseWalk.getData(curDate)
                val distance = data?.distance?.toFloat() ?: 0f
                val time = data?.time?.toInt() ?: 0
                totalDistance+=distance.toInt()
                totalTime+=time
                barEntries.add(BarEntry(index.toFloat(), distance/1000))
            }

            val aTime = (totalTime.toFloat()/total)/3600
            val aDist = totalDistance.toFloat()/total/1000
            var aSpeed = 0f
            if(aTime != 0f && aDist != 0f){
                aSpeed = aDist/aTime
            }
            binding.cal2TextTime.text = (totalTime/total/60).toString()
            binding.cal2TextDist.text = String.format("%.2f", aDist)
            binding.cal2TextSpeed.text = String.format("%.2f", aSpeed)

            val barDataSet = BarDataSet(barEntries, null)
            barDataSet.color = getColor(R.color.yellow)
            barDataSet.valueTextSize = 10f

            val barData = BarData(barDataSet)
            barData.barWidth = 0.3f

            var chartWidth = 0

            if(barEntries.size<4){
                chartWidth = (barEntries.size * (1.6f) * 170).toInt() // 막대 개수에 맞게 조정
            }else if(barEntries.size<10) {
                chartWidth = (barEntries.size * (1.7f) * 100).toInt() // 막대 개수에 맞게 조정
            }else{
                chartWidth = (barEntries.size * (1.3f) * 100).toInt() // 막대 개수에 맞게 조정
            }

            val layoutParams = chart.layoutParams
            layoutParams.width = chartWidth
            chart.layoutParams = layoutParams

            chart.xAxis.apply {
                setLabelCount(barEntries.size+2, true)
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase): String {
                        // value 값을 인덱스로 사용하여 dateArray의 요소를 가져옵니다.
                        return if (value.toInt() in 0 until dateArray.size) {
                            dateArray[value.toInt()]
                        } else {
                            ""  // 범위를 벗어난 경우 빈 문자열 반환
                        }
                    }
                }
            }

            chart.axisRight.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase): String {
                        return String.format("%.1f km", value) // 소수점 첫 번째 자리까지 표시
                    }
                }
            }

            chart.axisLeft.apply {
                valueFormatter = object : ValueFormatter() {
                    override fun getAxisLabel(value: Float, axis: AxisBase): String {
                        return String.format("%.1f km", value) // 소수점 첫 번째 자리까지 표시
                    }
                }
            }

            chart.data = barData
            chart.notifyDataSetChanged()
            chart.invalidate()

            calConst.visibility = View.GONE
            calVisible = false

            binding.cal2Scroll.visibility = View.VISIBLE
        } else {
            Toast.makeText(this@ActivityCalendarV2, "날짜를 선택해주세요.", Toast.LENGTH_SHORT).show()
        }
    }
}
