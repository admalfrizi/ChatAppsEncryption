package com.aplikasi.chatappstrials.ui

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.toolbox.JsonObjectRequest
import com.aplikasi.chatappstrials.databinding.ChatRoomBinding
import com.aplikasi.chatappstrials.models.Chat
import com.aplikasi.chatappstrials.ui.adapter.ChatAdapter
import com.aplikasi.chatappstrials.utils.Constants
import com.aplikasi.chatappstrials.utils.CryptoFunc
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import org.json.JSONObject


class ChatRoom : AppCompatActivity() {
    private lateinit var binding: ChatRoomBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var chatList: ArrayList<Chat>
    private lateinit var mDbRef: DatabaseReference
    private var cryptoFunc = CryptoFunc()

    var receiverRoom : String? = null
    var senderRoom : String? = null

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

                val database = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_URL).getReference("users").child(receiverUid!!)
                database.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.exists()){

                            Firebase.messaging.token.addOnCompleteListener(
                                OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                                        return@OnCompleteListener
                                    }

                                    // Get new FCM registration token
                                    val token = task.result

                                    val all = "all"

                                    FirebaseMessaging.getInstance().subscribeToTopic(all)

                                    val uidRef = mDbRef.child("users").child(senderUid!!)

                                    val to = JSONObject()
                                    val data = JSONObject()
                                    val topic = "/topics/all"

                                    uidRef.get().addOnCompleteListener {
                                        if (it.isSuccessful){
                                            val dataName = it.result
                                            val sentUser = dataName.child("name").getValue(String::class.java)

                                            data.put("senderUid", senderUid)
                                            data.put("title", sentUser)
                                            data.put("message", message)

                                            to.put("to", topic)
                                            to.put("token", token)
                                            to.put("data", data)
                                            sendNotif(to)

                                            Log.d(TAG, "Token : $token")
                                            Log.d(TAG, "Topic : $topic")
                                            Log.d(TAG, "$name")
                                        }
                                    }

                                })
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
            }
        }
    }

    private fun sendNotif(to: JSONObject) {

        val request: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            Constants.FCM_URL,
            to,
            Response.Listener { response: JSONObject ->
                Log.d("TAG", "onResponse : $response")
            },
            Response.ErrorListener {
                Log.d("TAG","onError: $it")
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val map: MutableMap<String, String> = HashMap()

                map["Authorization"] = "key=" + Constants.SERVER_KEY
                map["Content-type"] = "application/json"
                return map
            }

            override fun getBodyContentType(): String {
                return "application/json"
            }
        }

        val requestQueue = Volley.newRequestQueue(this)
        request.retryPolicy = DefaultRetryPolicy(
            30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        requestQueue.add(request)
    }


}