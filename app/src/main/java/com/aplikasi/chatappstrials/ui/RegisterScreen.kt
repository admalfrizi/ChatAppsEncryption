package com.aplikasi.chatappstrials.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aplikasi.chatappstrials.databinding.RegisterScreenBinding
import com.aplikasi.chatappstrials.models.User
import com.aplikasi.chatappstrials.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterScreen : AppCompatActivity() {
    private lateinit var binding: RegisterScreenBinding
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.regBtn.setOnClickListener {
            binding.ld.visibility = View.VISIBLE

            val name = binding.nameEdt.text.toString()
            val email = binding.emailEdt.text.toString()
            val password = binding.pwEdt.text.toString()

            register(name, email, password)
        }
    }

    private fun register(name: String, email: String, password: String) {
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    binding.ld.visibility = View.GONE
                    createUserDb(name, email, mAuth.currentUser?.uid!!)
                    val intent = Intent(this, LoginScreen::class.java)
                    startActivity(intent)
                } else {
                    binding.ld.visibility = View.GONE
                    Toast.makeText(this, "Data Anda Salah atau Tidak Ada", Toast.LENGTH_SHORT).show()
                }
        }

    }

    private fun createUserDb(name: String, email: String, uid: String) {
        val img : String? = null
        mDbRef = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_URL).reference
        mDbRef.child("users").child(uid).setValue(User(name, email, img, uid))

    }
}