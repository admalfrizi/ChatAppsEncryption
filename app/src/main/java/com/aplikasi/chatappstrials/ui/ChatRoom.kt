package com.aplikasi.chatappstrials.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.aplikasi.chatappstrials.RetrofitInstance
import com.aplikasi.chatappstrials.databinding.ChatListViewBinding
import com.aplikasi.chatappstrials.databinding.ChatRoomBinding
import com.aplikasi.chatappstrials.models.Chat
import com.aplikasi.chatappstrials.models.ChatNotifications
import com.aplikasi.chatappstrials.models.PushNotifications
import com.aplikasi.chatappstrials.ui.adapter.ChatAdapter
import com.aplikasi.chatappstrials.utils.Constants
import com.aplikasi.chatappstrials.utils.CryptoFunc
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ChatRoom : AppCompatActivity() {
    private lateinit var binding: ChatRoomBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: ArrayList<Chat>
    private lateinit var mDbRef: DatabaseReference
    private var cryptoFunc = CryptoFunc()

    var receiverRoom : String? = null
    var senderRoom : String? = null

    var topic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("name")
        val img = intent.getStringExtra("img")
        val receiverUid = intent.getStringExtra("uid")

        Log.d("TAG", "uid Receiver : $receiverUid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_URL).reference

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        chatList = ArrayList()
        chatAdapter = ChatAdapter(this, chatList)

        binding.chatView.layoutManager = LinearLayoutManager(this)
        binding.chatView.adapter = chatAdapter

        binding.nameTitle.text = name
        Glide.with(this).load(img).into(binding.profileImg)

        binding.btnBack.setOnClickListener {
            super.onBackPressed()
        }

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object: ValueEventListener {

                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    chatList.clear()

                    for(postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Chat::class.java)
                        chatList.add(message!!)
                    }
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

        binding.sendBtn.setOnClickListener {
            val message = binding.chatEdt.text.toString()

            val msgEncrypt = cryptoFunc.encrypt(message)

            val messageObject = Chat(msgEncrypt, senderUid)

            if(message.isEmpty()){
                Toast.makeText(applicationContext, "message is empty", Toast.LENGTH_SHORT).show()
                binding.chatEdt.setText("")
            } else {
                mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                            .setValue(messageObject)
                    }
                binding.chatEdt.setText("")
                topic = "/topic/$receiverUid"
                PushNotifications(ChatNotifications(name!!, msgEncrypt!!), topic).also {
                    sendNotif(it)
                }
            }
        }
    }

    private fun sendNotif(it: PushNotifications) = CoroutineScope(Dispatchers.IO).launch {
        try {
            val response = RetrofitInstance.api.postNotif(it)
            if(response.isSuccessful){
                Log.d("TAG", "Response : ${response.message()}")
            } else {
                Log.e("TAG", response.errorBody()!!.toString())
            }
        } catch (e: Exception) {
            Log.e("TAG", e.toString())
        }
    }
}