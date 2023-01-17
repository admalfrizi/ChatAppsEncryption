package com.aplikasi.chatappstrials.ui.navscreen.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.chatappstrials.databinding.FragmentHomeBinding
import com.aplikasi.chatappstrials.models.User
import com.aplikasi.chatappstrials.ui.adapter.UserAdapter
import com.aplikasi.chatappstrials.utils.Constants
import com.aplikasi.chatappstrials.utils.FirebaseNotifService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var userAdapter: UserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef : DatabaseReference
    private lateinit var requestPermission: ActivityResultLauncher<String>

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.loading.startShimmer()

        userList = ArrayList()
        setListChat()

        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                FirebaseNotifService()
            }
        }

        if(ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
            FirebaseNotifService()
        } else {
            requestPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        return root
    }


    private fun setListChat() {
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireContext())

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_URL).reference

        binding.userRv.layoutManager = layoutManager
        userAdapter = UserAdapter(requireActivity(), userList)
        binding.userRv.adapter = userAdapter

        val userId = mAuth.currentUser?.uid

        mDbRef.child("users").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for(postSnapshot in snapshot.children){
                    val currentUser = postSnapshot.getValue(User::class.java)

                    binding.loading.stopShimmer()
                    binding.loading.visibility = View.GONE

                    binding.userRv.visibility = View.VISIBLE

                    if(userId != currentUser?.uid){

                        userList.add(currentUser!!)
                    }
                }
                userAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}