package com.example.team_on

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.team_on.databinding.ActivityCalendarV2Binding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter

class ActivityCalendarV2 : AppCompatActivity() {

    private val binding: ActivityCalendarV2Binding by lazy { ActivityCalendarV2Binding.inflate(layoutInflater) }
    private lateinit var chart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        chart = binding.cal2Chart

        val barEntries = ArrayList<BarEntry>().apply {
            for (i in 0..19) { // 20개의 데이터 생성
                add(BarEntry(i.toFloat(), (Math.random() * 5).toFloat()))
            }
        }

        // 데이터 세트 생성
        val barDataSet = BarDataSet(barEntries, null)
        barDataSet.color = getColor(R.color.yellow)
        barDataSet.valueTextSize = 10f

        // BarData 생성
        val barData = BarData(barDataSet)

        // BarChart에 데이터 설정
        chart.data = barData
        barData.barWidth = 0.2f // 그래프 두깨 설정

        val chartWidth = (barEntries.size * (1.6f) * 100).toInt() // 막대 개수에 맞게 조정

        val layoutParams = chart.layoutParams
        layoutParams.width = chartWidth

        // X축 설정
        val xAxis = chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setLabelCount(barEntries.size+2, true)
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    if(value == 0f || value == (barEntries.size)+1f){
                        return ""
                    }
                    return "    Day ${value.toInt() + 1}" // 레이블을 "Day" 형식으로 설정
                }
            }
            spaceMax = 1f
            spaceMin = 1f
        }

        // 오른쪽 Y축 설정
        val yAxisRight = chart.axisRight.apply {
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    return String.format("%.1f km", value) // 소수점 첫 번째 자리까지 표시
                }
            }
            setDrawAxisLine(false) // 오른쪽 Y축 직선 숨기기
            isEnabled = true // 오른쪽 Y축 활성화
        }

        val yAxisLeft = chart.axisLeft.apply {
            valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase): String {
                    return String.format("%.1f km", value) // 소수점 첫 번째 자리까지 표시
                }
            }
            setDrawAxisLine(false) // 왼쪽 Y축 직선 숨기기
            isEnabled = true // 왼쪽 Y축 활성화
        }

        // 기타 차트 설정
        chart.apply {
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
        }
    }
}
