package id.nns.nichat.ui.other_profile

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import id.nns.nichat.R
import id.nns.nichat.databinding.ActivityOtherProfileBinding
import id.nns.nichat.domain.model.User
import id.nns.nichat.ui.image.ImageActivity
import id.nns.nichat.utils.removeStatusBar

class OtherProfileActivity : AppCompatActivity() {

    companion object {
        const val KEY_OTHER_USER = "key_other_user"
    }

    private lateinit var binding: ActivityOtherProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        removeStatusBar()
        val user = intent.getParcelableExtra<User>(KEY_OTHER_USER)

        binding.etNameOtherProfile.text = user?.name
        binding.etStatusOtherProfile.text = user?.status
        binding.tvDateOfBirth.text = user?.dob

        Glide.with(this)
            .asBitmap()
            .load(user?.imgUrl)
            .placeholder(R.drawable.profile)
            .into(object: CustomTarget<Bitmap>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                ) {
                    binding.civOtherProfile.setImageBitmap(resource)
                    setGradient(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) = Unit
            })

        binding.civOtherProfile.setOnClickListener {
            Intent(this, ImageActivity::class.java).apply {
                putExtra(ImageActivity.OPEN_IMAGE, user?.imgUrl)
                startActivity(this)
            }
        }
    }

    private fun setGradient(resource: Bitmap) {
        Palette.from(resource).generate { palette ->
            val defValue = ContextCompat.getColor(this, R.color.dark_blue)
            val gradient = GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                intArrayOf(
                    ContextCompat.getColor(this, R.color.background),
                    ContextCompat.getColor(this, R.color.background),
                    ContextCompat.getColor(this, R.color.background),
                    palette?.getDominantColor(defValue) ?: defValue
                )
            )

            binding.clOtherProfile.background = gradient
        }
    }

}