package id.nns.nichat.utils

import com.github.mikephil.charting.formatter.ValueFormatter

class XAxisValueFormatter(private val values: Array<String>) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        return values[value.toInt()]
    }

}