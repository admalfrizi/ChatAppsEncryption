package com.aplikasi.chatappstrials.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aplikasi.chatappstrials.R
import com.aplikasi.chatappstrials.databinding.BottomNavBinding
import com.google.firebase.auth.FirebaseAuth

class BottomNav : AppCompatActivity() {

    private lateinit var binding: BottomNavBinding

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = BottomNavBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        mAuth = FirebaseAuth.getInstance()
        navView.itemIconTintList = null

        val currentUser = mAuth.currentUser
        if(currentUser == null){
            Intent(this, LoginScreen::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        val navController = findNavController(R.id.nav_host_fragment_bottom_nav)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}