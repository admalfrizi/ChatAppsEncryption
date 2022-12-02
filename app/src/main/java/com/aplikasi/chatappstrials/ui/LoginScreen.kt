package com.aplikasi.chatappstrials.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.aplikasi.chatappstrials.databinding.LoginScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginScreen : AppCompatActivity() {

    private lateinit var binding : LoginScreenBinding
    private lateinit var mAuth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        binding.loginBtn.setOnClickListener {
            binding.ld.visibility = View.VISIBLE
            val email = binding.emailEdt.text.toString()
            val password = binding.pwEdt.text.toString()

            login(email,password)
        }

        binding.toSignup.setOnClickListener {
            val intent = Intent(this,RegisterScreen::class.java)
            startActivity(intent)
        }

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        if(currentUser != null){
            Intent(this, BottomNav::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }
    }

    private fun login(email: String, password: String) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {
                    binding.ld.visibility = View.GONE

                    Intent(this, BottomNav::class.java).also {
                        it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(it)
                    }
                } else if(email.isEmpty() || password.isEmpty()) {
                    binding.ld.visibility = View.GONE
                    Toast.makeText(this, "Semua Data Harus Di Isi", Toast.LENGTH_LONG).show()
                }
                else {
                    binding.ld.visibility = View.GONE
                    Toast.makeText(this, "Data Anda Salah atau Tidak Ada Di Sistem", Toast.LENGTH_SHORT).show()
                }
            }

    }
}