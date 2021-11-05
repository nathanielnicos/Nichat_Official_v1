package id.nns.nichat.utils

import android.content.Context
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import id.nns.nichat.R

fun HorizontalBarChart.setSomething(
    dataSet: BarDataSet,
    maxValue: Float,
    values: Array<String>
) {
    this.apply {
        // Sumbu vertikal
        xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1F // Ketelitian
            labelCount = 1
            setDrawAxisLine(false) // Menghilangkan sumbu x
            setDrawGridLines(false) // Menghilangkan garis horizontal dari sumbu x
            valueFormatter = XAxisValueFormatter(values)
        }

        // Sumbu horizontal bawah
        axisRight.isEnabled = false // Menghilangkan sumbu y bawah

        // Sumbu horizontal atas
        axisLeft.apply {
            isEnabled = true
            axisMaximum = maxValue
            axisMinimum = 0F
            granularity = 1F
            setDrawAxisLine(false)
            setDrawGridLines(false)
        }

        setPinchZoom(false) // Tidak bisa dizoom
        legend.isEnabled = false
        setDrawBarShadow(true) // Menampilkan sisa bar yang belum terisi
        description.isEnabled = false
        data = BarData(dataSet)
        animateY(2000)
        invalidate()
    }
}

fun BarDataSet.setSomething(context: Context) : BarDataSet {
    return this.apply {
        color = ContextCompat.getColor(context, R.color.dark_blue)
        setDrawValues(true) // Menampilkan bar value
        valueTextSize = 10F
    }
}
