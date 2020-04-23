package com.example.selfbuy.presentation.splashScreen.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatDelegate
import com.example.selfbuy.R
import com.example.selfbuy.presentation.home.activity.HomeActivity

class SplashScreenActivity : AppCompatActivity() {

    private val SPLASH_TIME_OUT:Long=1000 // 1 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            startActivity(Intent(this, HomeActivity::class.java))

            // close this activity
            finish()
        }, SPLASH_TIME_OUT)
    }
}
