package id.nns.nichat.ui.my_profile

import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.firebase.ui.auth.AuthUI
import id.nns.nichat.R
import id.nns.nichat.databinding.ActivityProfileBinding
import id.nns.nichat.preference.UserPreference
import id.nns.nichat.data.response.UserResponse
import id.nns.nichat.ui.splash.SplashActivity
import id.nns.nichat.utils.removeStatusBar
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var preference: UserPreference
    private val profileViewModel: ProfileViewModel by viewModels()

    private lateinit var user: UserResponse
    private var selectedImageBytes: ByteArray? = null
    private var imgUrl: String? = null
    private var pictureJustChanged = false

    companion object {
        private const val SELECT_IMAGE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        removeStatusBar()

        setView()
        setEditTextListener()

        binding.civProfile.setOnClickListener {
            openStorage()
        }

        binding.ibCalendar.setOnClickListener {
            setBornDate()
        }

        binding.btnSave.setOnClickListener {
            save()
        }

        binding.btnSignOut.setOnClickListener {
            showDialog("Sign Out") {
                signOut()
            }
        }

        binding.btnDeleteAccount.setOnClickListener {
            showDialog("Delete Account") {
                deleteAccount()
            }
        }

        observeValue()
    }

    private fun setView() {
        // Set value from preference
        preference = UserPreference(this)
        user = preference.getUser()

        if (!pictureJustChanged) {
            binding.etNameProfile.setText(user.name)
            binding.etStatusProfile.setText(user.status)
            binding.tvSelectedDate.text = user.dob

            Glide.with(this)
                .asBitmap()
                .load(user.imgUrl)
                .placeholder(R.drawable.profile)
                .into(object: CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?
                    ) {
                        binding.civProfile.setImageBitmap(resource)
                        setGradient(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) = Unit
                })
        }
    }

    private fun setBornDate() {
        val calendar = Calendar.getInstance()
        val format = "dd/MM/yyyy"
        val dateListener =
            DatePickerDialog.OnDateSetListener { _, year, month, day ->
                calendar.apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, day)
                }

                binding.tvSelectedDate.text =
                    SimpleDateFormat(format, Locale.getDefault()).format(calendar.time)

                binding.btnSave.visibility = View.VISIBLE
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DatePickerDialog(
                this,
                dateListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun save() {
        binding.pbProfile.visibility = View.VISIBLE
        binding.btnSave.visibility = View.GONE
        profileViewModel.saveChanges(
            selectedImageBytes = selectedImageBytes,
            name = binding.etNameProfile.text.toString().trim(),
            status = binding.etStatusProfile.text.toString().trim(),
            dob = binding.tvSelectedDate.text.toString()
        )
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

            binding.clFragmentProfile.background = gradient
        }
    }

    private fun setEditTextListener() {
        // Set edittext listener
        binding.etNameProfile.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString() == user.name) {
                    binding.btnSave.visibility = View.GONE
                } else {
                    binding.btnSave.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })

        binding.etStatusProfile.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) = Unit

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                if (s.toString() == user.status) {
                    binding.btnSave.visibility = View.GONE
                } else {
                    binding.btnSave.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun openStorage() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        @Suppress("DEPRECATION")
        startActivityForResult(
            intent,
            SELECT_IMAGE
        )
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == SELECT_IMAGE
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.data != null
        ) {
            val selectedImagePath = data.data
            @Suppress("DEPRECATION")
            val selectedImageBitmap = MediaStore.Images.Media
                .getBitmap(this.contentResolver, selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            selectedImageBytes = outputStream.toByteArray()

            // Change to new image
            Glide.with(this)
                .load(selectedImageBytes)
                .into(binding.civProfile)

            pictureJustChanged = true
            binding.btnSave.visibility = View.VISIBLE
        }
    }

    private fun observeValue() {
        profileViewModel.imgUrl.observe(this) {
            imgUrl = it
        }
        profileViewModel.isSuccessUpdate.observe(this) {
            if (it) {
                saveToPreferenceWithOrWithoutImgUrl(it)
            } else {
                binding.btnSave.visibility = View.VISIBLE
            }
        }
    }

    private fun saveToPreferenceWithOrWithoutImgUrl(isSuccessUpdate:Boolean) {
        if (isSuccessUpdate) {
            if (imgUrl != null) {
                preference.updateUserWithImgUrl(
                    name = binding.etNameProfile.text.toString().trim(),
                    imgUrl = imgUrl,
                    status = binding.etStatusProfile.text.toString().trim(),
                    dob = binding.tvSelectedDate.text.toString()
                )
            } else {
                preference.updateUserWithoutImgUrl(
                    name = binding.etNameProfile.text.toString().trim(),
                    status = binding.etStatusProfile.text.toString().trim(),
                    dob = binding.tvSelectedDate.text.toString()
                )
            }
            binding.pbProfile.visibility = View.GONE
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            binding.pbProfile.visibility = View.GONE
            Toast.makeText(this, "Saving failed. Try again!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDialog(title: String, onClickPositiveButton: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage("Are you sure?\n\nPlease clear application cache and data after you click on YES button!")
            .setPositiveButton("YES") { _, _ ->
                onClickPositiveButton()
            }
            .setNegativeButton("NO") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }

    private fun signOut() {
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener {
                preference.clearPrefs()

                val toast = Toast.makeText(this, "Sign out success!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()

                goToSplashActivity()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Sign out failed!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteAccount() {
        AuthUI.getInstance()
            .delete(this)
            .addOnCompleteListener {
                preference.clearPrefs()

                val toast = Toast.makeText(this, "Delete account success!", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()

                goToSplashActivity()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Delete account failed!", Toast.LENGTH_SHORT).show()
            }
    }

    private fun goToSplashActivity() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}