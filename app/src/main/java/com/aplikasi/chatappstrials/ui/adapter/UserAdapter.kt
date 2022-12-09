package com.aplikasi.chatappstrials.ui.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.chatappstrials.databinding.ChatListViewBinding
import com.aplikasi.chatappstrials.databinding.CustomPopupWindowBinding
import com.aplikasi.chatappstrials.models.User
import com.aplikasi.chatappstrials.ui.ChatRoom
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class UserAdapter(val context: Context, private val userList: ArrayList<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    inner class UserViewHolder(val binding: ChatListViewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context)
        val binding : ChatListViewBinding = ChatListViewBinding.inflate(view, parent, false)

        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

        with(holder){
            with(userList[position]){
                binding.username.text = this.name
                binding.email.text = this.email
                Glide.with(context).load(this.img_profile).into(binding.imgProfile)
                binding.root.setOnClickListener {
                    val intent = Intent(context, ChatRoom::class.java)

                    intent.putExtra("name", this.name)
                    intent.putExtra("uid", this.uid)
                    intent.putExtra("img", this.img_profile)

                    context.startActivity(intent)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}