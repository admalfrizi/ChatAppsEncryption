package com.aplikasi.chatappstrials.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aplikasi.chatappstrials.databinding.ChatReceiverBinding
import com.aplikasi.chatappstrials.databinding.ChatSenderBinding
import com.aplikasi.chatappstrials.databinding.ChatViewBinding
import com.aplikasi.chatappstrials.models.Chat
import com.google.firebase.auth.FirebaseAuth


class ChatAdapter(val context: Context, private val chatList: ArrayList<Chat>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //private var cryptoFunc = CryptoFunc()
    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    class SentViewHolder(binding: ChatSenderBinding): RecyclerView.ViewHolder(binding.root) {
        val sender = binding.senderMsg
    }

    class ReceiveViewHolder(binding: ChatReceiverBinding): RecyclerView.ViewHolder(binding.root){
        val receiver = binding.receiveMsg
    }

    class EmptyViewHolder(binding: ChatViewBinding): RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == ITEM_RECEIVE) {
            val binding = ChatReceiverBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ReceiveViewHolder(binding)

        } else if (viewType == ITEM_SENT){

            val binding = ChatSenderBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return SentViewHolder(binding)

        } else {
            val binding = ChatViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)

            return EmptyViewHolder(binding)
        }

    }

    override fun getItemCount(): Int {
        return chatList.size
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMsg = chatList[position]

        if(getItemViewType(position) == ITEM_SENT){
            (holder as SentViewHolder).sender.text = currentMsg.message!!

        } else {
            (holder as ReceiveViewHolder).receiver.text = currentMsg.message!!

        }
    }

    override fun getItemViewType(position: Int): Int {
        val currentMsg = chatList[position]

        if(currentMsg.senderId == FirebaseAuth.getInstance().currentUser?.uid){
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
    }
    

}