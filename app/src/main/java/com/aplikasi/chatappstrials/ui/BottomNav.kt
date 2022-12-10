package com.aplikasi.chatappstrials.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.aplikasi.chatappstrials.R
import com.aplikasi.chatappstrials.databinding.BottomNavBinding
import com.aplikasi.chatappstrials.databinding.CustomPopupNetworkBinding
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

        if(!isConnected(this)){
            dialogWarning()
        }

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


    private fun isConnected(context: Context): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm?.run {
                cm.getNetworkCapabilities(cm.activeNetwork)?.run {
                    result = when {
                        hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                        hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                        else -> false
                    }
                }
            }
        }

        return result
    }

    private fun dialogWarning() {
        val binding : CustomPopupNetworkBinding = CustomPopupNetworkBinding.inflate(layoutInflater)
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setContentView(binding.root)

        binding.btnExit.setOnClickListener {
            finishAffinity()
            dialog.dismiss()
        }

        dialog.show()
    }


    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()
    }
}