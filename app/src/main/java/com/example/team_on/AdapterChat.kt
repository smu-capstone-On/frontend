package com.example.team_on

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.databinding.RvChattingBinding

class AdapterChat(private val chatList : MutableList<ChatMessage>, ) : RecyclerView.Adapter<AdapterChat.ViewHolder>() {

    private val user = KakaoSDK.user
    private val senderId = user.getString("userId","")

    inner class ViewHolder(binding: RvChattingBinding) : RecyclerView.ViewHolder(binding.root){

        private val myChatConst = binding.rvChattingConstMy
        private val myChat = binding.rvChattingTextMy
        private val myChatTime = binding.rvChattingTimeMy

        private val otherConst1 = binding.rvChattingConst1
        private val otherChat1 = binding.rvChattingChat1
        private val otherTime1 = binding.rvChattingTime1

        private val dateChatConst = binding.rvChattingDay
        private val dateChat = binding.rvChattingDayText

        fun bind(list : ChatMessage) {

            fun myChatting(){
                myChatConst.visibility= View.VISIBLE
                otherConst1.visibility= View.GONE
                dateChatConst.visibility= View.GONE
                myChat.text=list.message
                myChatTime.text=list.time.substring(9)
            }

            when (list.flag) {
                0 -> {
                    if(list.sender == senderId){
                        myChatting()
                    }else{
                        myChatConst.visibility= View.GONE
                        otherConst1.visibility= View.VISIBLE
                        dateChatConst.visibility= View.GONE
                        otherChat1.text=list.message
                        otherTime1.text=list.time.substring(9)
                    }
                }
                3 -> { // 시스템
                    dateChatConst.visibility= View.VISIBLE
                    otherConst1.visibility= View.GONE
                    myChatConst.visibility= View.GONE
                    dateChat.text=list.message
                }
            }
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