package id.nns.nichat.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import id.nns.nichat.ui.onboarding.OnBoardingActivity
import id.nns.nichat.preference.OnBoardingPreference
import id.nns.nichat.ui.home.HomeActivity
import id.nns.nichat.ui.main.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var onBoardingPreference: OnBoardingPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goSomewhere()
    }

    private fun goSomewhere() {
        onBoardingPreference = OnBoardingPreference(this)
        val uid = FirebaseAuth.getInstance().uid

        if (onBoardingPreference.isFirstInstall) {
            goToOnBoardingActivity()
        } else {
            if (uid == null) {
                goToMainActivity()
            } else {
                goToHomeActivity()
            }
        }
    }

    private fun goToOnBoardingActivity() {
        val intent = Intent(this, OnBoardingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

}