package id.nns.nichat.ui.stat

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import id.nns.nichat.R
import id.nns.nichat.preference.UserPreference
import id.nns.nichat.databinding.FragmentStatBinding
import id.nns.nichat.utils.extensions.setSomething
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.util.*

class StatFragment : Fragment() {

    private var _binding: FragmentStatBinding? = null
    private val binding get() = _binding as FragmentStatBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setChart()
    }

    private fun setChart() {
        val preference = UserPreference(requireContext())
        val dob = preference.getUser().dob

        if (dob != activity?.resources?.getString(R.string.date)) {
            val dateFormat = SimpleDateFormat(
                activity?.resources?.getString(R.string.date_pattern),
                Locale.getDefault()
            )
            val calendar = Calendar.getInstance()

            calendar.time = dateFormat.parse(dob) as Date

            val year = calendar[Calendar.YEAR]
            val month = calendar[Calendar.MONTH] + 1
            val date = calendar[Calendar.DAY_OF_MONTH]

            // Year
            binding.tvAge.text = getAge(year, month, date, 0).toString()

            // Month
            val ageMonth = arrayListOf(
                BarEntry(
                    0.0f,
                    getAge(year, month, date, 1).toFloat()
                )
            )
            val ageMonthDataSet = BarDataSet(
                ageMonth,
                ""
            ).setSomething(requireContext())

            binding.hcbAgeMonth.setSomething(
                dataSet = ageMonthDataSet,
                maxValue = 12F,
                values = arrayOf(activity?.resources?.getString(R.string.month) ?: "")
            )

            // Day
            val ageDay = arrayListOf(
                BarEntry(
                    0.0f,
                    getAge(year, month, date, 2).toFloat()
                )
            )
            val ageDayDataSet = BarDataSet(
                ageDay,
                ""
            ).setSomething(requireContext())

            binding.hcbAgeDay.setSomething(
                dataSet = ageDayDataSet,
                maxValue = 30F,
                values = arrayOf(activity?.resources?.getString(R.string.day) ?: "")
            )
        }
    }

    private fun getAge(year: Int, month: Int, date: Int, type: Int): Int {
        return if (Build.VERSION.SDK_INT < 26) {
            0
        } else when (type) {
            0 -> Period.between(LocalDate.of(year, month, date), LocalDate.now()).years
            1 -> Period.between(LocalDate.of(year, month, date), LocalDate.now()).months
            2 -> Period.between(LocalDate.of(year, month, date), LocalDate.now()).days
            else -> 0
        }
    }

}
