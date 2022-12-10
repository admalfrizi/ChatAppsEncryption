package com.aplikasi.chatappstrials.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.aplikasi.chatappstrials.databinding.SplashScreenBinding

@SuppressLint("CustomSplashScreen")
@Suppress("Deprecation")
class SplashScreen : AppCompatActivity() {

    private lateinit var binding: SplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            installSplashScreen()
            super.onCreate(savedInstanceState)
            val intent = Intent(baseContext, LoginScreen::class.java)
            startActivity(intent)
            finish()
        } else {
            super.onCreate(savedInstanceState)
            binding = SplashScreenBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val handler = Handler()
            handler.postDelayed({
                val intent = Intent(this, LoginScreen::class.java)
                startActivity(intent)
                finish()
            },2000)
        }
    }
}