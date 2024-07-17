package com.example.team_on

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.databinding.RvSearchResultBinding

class AdapterSearch (private val searchList : MutableList<String>) : RecyclerView.Adapter<AdapterSearch.ViewHolder>() {

    inner class ViewHolder(binding: RvSearchResultBinding) : RecyclerView.ViewHolder(binding.root){

        private val address = binding.rvSearchResultText

        fun bind(t: String) {
            address.text = t
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RvSearchResultBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AdapterSearch.ViewHolder, position: Int) {
        holder.bind(searchList[position])
    }

    override fun getItemCount() = searchList.size
}