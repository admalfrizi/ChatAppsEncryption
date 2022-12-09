package com.aplikasi.chatappstrials.ui.navscreen.dashboard

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.aplikasi.chatappstrials.databinding.CustomPopupWindowBinding
import com.aplikasi.chatappstrials.databinding.FragmentDashboardBinding
import com.aplikasi.chatappstrials.ui.BottomNav
import com.aplikasi.chatappstrials.ui.EditData
import com.aplikasi.chatappstrials.ui.LoginScreen
import com.aplikasi.chatappstrials.utils.Constants
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var mDbRef: DatabaseReference
    private lateinit var mAuth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mAuth = FirebaseAuth.getInstance()

        return root
    }

    override fun onStart() {
        super.onStart()
        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        mDbRef = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_URL).reference
        val uidRef = mDbRef.child("users").child(uid)

        uidRef.get().addOnCompleteListener { task ->
            if(task.isSuccessful){

                val snapshot = task.result
                val name = snapshot?.child("name")?.getValue(String::class.java)
                val email = snapshot?.child("email")?.getValue(String::class.java)
                val img = snapshot?.child("img_profile")?.getValue(String::class.java).toString()

                binding.tvNama.text = name
                binding.tvEmail.text = email
                Glide.with(requireActivity()).load(img).into(binding.profileImg)

            } else {
                Log.d("TAG", task.exception!!.message!!)
            }
        }

        binding.logoutBtn.setOnClickListener {
            dialogLogout()
        }

        binding.editDataBtn.setOnClickListener {
            val intent = Intent(requireContext(), EditData::class.java)
            startActivity(intent)
        }
    }

    private fun dialogLogout() {
        val binding : CustomPopupWindowBinding = CustomPopupWindowBinding.inflate(layoutInflater)
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(binding.root)

        binding.btnLogout.setOnClickListener {
            mAuth.signOut()
            Intent(context, LoginScreen::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}