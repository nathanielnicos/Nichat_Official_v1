package id.nns.nichat.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import id.nns.nichat.R
import id.nns.nichat.preference.UserPreference
import id.nns.nichat.databinding.ActivityMainBinding
import id.nns.nichat.ui.splash.SplashActivity

class MainActivity : AppCompatActivity() {

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }
    private lateinit var binding: ActivityMainBinding
    private val mainViewModel: MainViewModel by viewModels()
    private lateinit var preference: UserPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preference = UserPreference(this)
        observeValue()

        binding.tvTapToContinue.setOnClickListener {
            openFirebaseUIAuth()
            binding.tvTapToContinue.visibility = View.GONE
        }
    }

    private fun observeValue() {
        mainViewModel.isFinish.observe(this) {
            if (it) {
                goToSplashActivity()
            }
        }
        mainViewModel.message.observe(this) {
            if (it != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openFirebaseUIAuth() {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build()
        )

        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .setTheme(R.style.NichatCustomTheme)
            .setIsSmartLockEnabled(false)
            .build()

        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        when (result.resultCode) {
            RESULT_OK -> {
                mainViewModel.onResultOk(preference)
                Toast.makeText(
                    this@MainActivity,
                    "Please wait!",
                    Toast.LENGTH_SHORT
                ).show()
            }
            RESULT_CANCELED -> {
                if (response == null) return
                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK -> {
                        Toast.makeText(this, "No network!", Toast.LENGTH_SHORT).show()
                    }
                    ErrorCodes.UNKNOWN_ERROR -> {
                        Toast.makeText(this, "Unknown error!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        Toast.makeText(this, "Sign in failed!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else -> {
                Toast.makeText(this, "Sign in failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun goToSplashActivity() {
        val intent = Intent(this, SplashActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}
