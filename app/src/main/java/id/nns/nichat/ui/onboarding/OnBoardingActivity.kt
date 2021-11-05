package id.nns.nichat.ui.onboarding

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.viewpager.widget.ViewPager
import id.nns.nichat.R
import id.nns.nichat.databinding.ActivityOnboardingBinding
import id.nns.nichat.ui.main.MainActivity
import id.nns.nichat.preference.OnBoardingPreference

class OnBoardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var vpAdapter: ViewPagerAdapter
    private lateinit var onBoardingPreference: OnBoardingPreference
    private val bullet = arrayOfNulls<TextView>(3)
    private var currentPage = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBoardingPreference = OnBoardingPreference(this)

        vpAdapter = ViewPagerAdapter(this)
        binding.vpWalkThrough.adapter = vpAdapter

        bulletIndicator(currentPage)
        initAction()
    }

    private fun initAction() {
        binding.vpWalkThrough.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) = Unit

            override fun onPageSelected(position: Int) {
                bulletIndicator(position)
                currentPage = position
            }

            override fun onPageScrollStateChanged(state: Int) = Unit
        })

        binding.tvNext.setOnClickListener {
            if (currentPage < bullet.size - 1) {
                binding.vpWalkThrough.currentItem = currentPage + 1
            } else {
                onBoardingPreference.isFirstInstall = false
                goToMainActivity()
            }
        }

        binding.tvSkip.setOnClickListener {
            onBoardingPreference.isFirstInstall = false
            goToMainActivity()
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun bulletIndicator(page: Int) {
        binding.llBullet.removeAllViews()

        for (i in bullet.indices) {
            bullet[i] = TextView(this)
            bullet[i]?.textSize = 40f
            bullet[i]?.setTextColor(ContextCompat.getColor(this, R.color.grey))
            bullet[i]?.text = HtmlCompat.fromHtml("&#8226;", HtmlCompat.FROM_HTML_MODE_LEGACY)

            binding.llBullet.addView(bullet[i])
        }

        if (bullet.isNotEmpty()) {
            bullet[page]?.setTextColor(ContextCompat.getColor(this, R.color.normal_blue))
        }
    }

}