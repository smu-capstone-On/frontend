package com.example.team_on

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AlertDialog.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.KakaoRetrofitObject
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.FragmentWalkBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FragmentWalk : Fragment() {

    private lateinit var binding: FragmentWalkBinding
    private lateinit var btnAttribute: ImageButton
    private lateinit var btnSearch: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWalkBinding.inflate(layoutInflater)

        btnAttribute = binding.fwalkBtnAttribute
        btnSearch = binding.fwalkBtnSearch

        btnSearch.setOnClickListener {
            val call = KakaoRetrofitObject.getRetrofitService.kakaoSearch(KakaoKey.Key)
            call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
                override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if(responseBody != null){
                            if(responseBody.success) {
                                startActivity(Intent(this@ActivityLogin, ActivityMain::class.java))
                            }
                        }
                    }
                    else{
                        Toast.makeText(this@ActivityLogin,"입력하신 내용을 다시 확인해 주세요.",Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                    val errorMessage = "Call Failed: ${t.message}"
                    Log.d("Retrofit", errorMessage)
                }
            })
        }

        btnAttribute.setOnClickListener {
            startActivity(Intent(requireContext(), ActivityAttribute::class.java))
        }
        return binding.root
    }

    private fun openDialog(addressList : MutableList<String>) {
        val builder = AlertDialog.Builder(requireContext(),R.style.CustomAlertDialog)
        val view = LayoutInflater.from(requireContext()).inflate(
            R.layout.custom_dialog_search_result,
            null
        )

        // 다이얼로그 텍스트 설정
        builder.setView(view)

        val alertDialog = builder.create()

        val dialogRv = view.findViewById<RecyclerView>(R.id.cd_search_rv)
        dialogRv.layoutManager = LinearLayoutManager(requireContext())
        val addressAdapter = AdapterSearch(addressList)
        dialogRv.adapter = addressAdapter

        alertDialog.window?.setBackgroundDrawable(ColorDrawable(0))

        alertDialog.show()
    }
}