package com.example.covid.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.covid.R
import com.example.covid.covidtracker.Covid19Tracker
import com.sawolabs.androidsdk.Sawo
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        supportActionBar!!.hide()

        lottie.playAnimation()

        lottie.loop(true)

        Login_btn.setOnClickListener {
            Sawo(
                this,
                "65a3cd26-9be4-4b56-9b8d-82fddd55cb77", // your api key
                "6150922909e2ab2081566b4dccOqhooubh41IXUJvPcbnx7o"  // your api key secret
            ).login(
                "phone_number_sms", // can be one of 'email' or 'phone_number_sms'
                Covid19Tracker::class.java.name // Callback class name
            )

        }
    }
}