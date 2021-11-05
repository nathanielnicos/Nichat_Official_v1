package id.nns.nichat.ui.onboarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import id.nns.nichat.R
import id.nns.nichat.databinding.SlideOnboardingBinding

class ViewPagerAdapter(val context: Context) : PagerAdapter() {

    private val imgArray: IntArray = intArrayOf(R.drawable.easy, R.drawable.fast, R.drawable.safe)
    private val titleArray: Array<String> = arrayOf("Easy", "Fast", "Safe")
    private val subtitleArray: Array<String> = arrayOf(
        "Keep in touch with your friends",
        "Get the latest news in every second",
        "Protect your account from any threats"
    )

    private lateinit var binding: SlideOnboardingBinding

    override fun getCount(): Int = imgArray.size

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        binding = SlideOnboardingBinding.inflate(LayoutInflater.from(context), container, false)

        val img = binding.ivSlide
        val title = binding.tvTitleSlide
        val subtitle = binding.tvSubtitleSlide

        img.setImageDrawable(ContextCompat.getDrawable(context, imgArray[position]))
        title.text = titleArray[position]
        subtitle.text = subtitleArray[position]

        container.addView(binding.root)
        return binding.root
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        val view = `object` as View
        container.removeView(view)
    }

}