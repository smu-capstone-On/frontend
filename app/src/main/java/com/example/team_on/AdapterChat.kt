package com.example.team_on

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.databinding.RvChattingBinding

class AdapterChat(private val chatList : MutableList<ChatMessage>, ) : RecyclerView.Adapter<AdapterChat.ViewHolder>() {

    inner class ViewHolder(binding: RvChattingBinding) : RecyclerView.ViewHolder(binding.root){

        private val myChat = binding.rvChattingTextMy
        private val myChatTime = binding.rvChattingTimeMy

        fun bind(list : ChatMessage) {
            myChat.text = list.message
            myChatTime.text = list.time
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvChattingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterChat.ViewHolder, position: Int) {
        holder.bind(chatList[position])
    }

    override fun getItemCount() = chatList.size
}