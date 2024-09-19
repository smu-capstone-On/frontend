package com.example.team_on

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.Retrofit
import com.example.team_on.databinding.RvMateBinding

class AdapterMate(private val mateList : MutableList<Retrofit.MateInfo>,
    private val context: Context) : RecyclerView.Adapter<AdapterMate.ViewHolder>() {

    inner class ViewHolder(binding: RvMateBinding) : RecyclerView.ViewHolder(binding.root){

        private val num = binding.rvMateTextNum
        private val info = binding.rvMateTextInfo
        private val post = binding.rvMateTextPost
        private val btnChat = binding.rvMateBtnChat

        fun bind(list : Retrofit.MateInfo) {
            num.text = list.num.toString()
            val start = list.sTime.substring(list.sTime.length - 5)
            val time = list.wTime.substring(list.wTime.length - 5)
            val h = time.split(":")[0]
            val m = time.split(":")[1]
            val walk = h.toInt()*60+m.toInt()
            val gender = if (list.gender == "MALE") {
                "남"
            } else {
                "여"
            }
            val pet = if (list.pet){
                "반려동물 있음"
            }else{
                "반려동물 없음"
            }
            info.text = "${list.age}/$gender/산책시작 $start/삭책시간 $walk/$pet"
            post.text = list.post

            btnChat.setOnClickListener {
                val intent = Intent(context, ActivityChat::class.java)
                context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvMateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterMate.ViewHolder, position: Int) {
        holder.bind(mateList[position])
    }

    override fun getItemCount() = mateList.size
}