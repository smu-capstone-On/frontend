package com.example.team_on

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.FragmentAddDealBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class FragmentAddDeal : Fragment() {

    private var _binding: FragmentAddDealBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnAddDeal: Button
    private lateinit var btnTagDog: Button
    private lateinit var btnTagCat: Button
    private lateinit var btnTagSmall: Button
    private lateinit var btnTagReptile: Button
    private lateinit var btnTagBird: Button
    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var editTextPrice: EditText
    private lateinit var btnAddImage: ImageButton
    private lateinit var toolbar: Toolbar

    private var selectedTags = mutableListOf<String>()

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            btnAddImage.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddDealBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddDeal = binding.addDealBtnEnterPost
        btnTagDog = binding.addDealTagDog
        btnTagCat = binding.addDealTagCat
        btnTagSmall = binding.addDealTagSmall
        btnTagReptile = binding.addDealTagReptile
        btnTagBird = binding.addDealTagBird
        editTextTitle = binding.addDealTitle
        editTextContent = binding.addDealContent
        editTextPrice = binding.addDealEditPrice
        btnAddImage = binding.addDealAddImage
        toolbar = binding.addDealToolbar

        setTagBtn()
        addDeal()
        addImage()

        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setTagBtn() {
        val btns = listOf(btnTagDog, btnTagCat, btnTagSmall, btnTagReptile, btnTagBird)

        btns.forEach { button ->
            button.setOnClickListener {
                if (selectedTags.size == 3 && !button.isSelected) {
                    Toast.makeText(activity, "태그는 최대 3개까지 선택할 수 있습니다.", Toast.LENGTH_SHORT).show()
                }
                else {
                    button.isSelected = !button.isSelected
                    if (button.isSelected) {
                        button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
                        selectedTags.add(button.text.toString())
                    } else {
                        button.setTextColor(ContextCompat.getColor(button.context, R.color.hint))
                        selectedTags.remove(button.text.toString())
                    }
                }
            }
        }
    }

    // 물품 이미지 추가
    private fun addImage() {
        btnAddImage.setOnClickListener {
            getImage.launch("image/*")
        }
    }

    // 물품 등록하기
    private fun addDeal() {
        btnAddDeal.setOnClickListener {
            // 사용자 정보(이름) 받는 방법 수정 필요

            val title = editTextTitle.text.toString()
            val body = editTextContent.text.toString()
            val tagList = selectedTags
            val price= editTextPrice.text.toString().toInt()

            if (title.isEmpty()) {
                Toast.makeText(activity, "게시글 제목이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }

            if (body.isEmpty()) {
                Toast.makeText(activity, "게시글 내용이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }

            if (editTextPrice.text.isEmpty()) {
                Toast.makeText(activity, "가격이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
            }

            val data = JSONObject().apply {
                put("userId", 1)
                put("title", title)
                put("price", price)
                put("body", body)
                put("tagTypes", tagList)
                put("reservationStatus", false)
                put("saleStatus", false)
            }

            val requestBody = data.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            var imagePart: MultipartBody.Part? = null

            btnAddImage?.let { uri ->
                val bitmap = (btnAddImage.drawable as BitmapDrawable).bitmap
                val file = File(requireContext().cacheDir, "image.jpg")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            }

            uploadDeal(imagePart, requestBody)
        }
    }

    private fun uploadDeal(imagePart: MultipartBody.Part?, requestBody: okhttp3.RequestBody) {
        val call = if (imagePart != null) {
            RetrofitObject.getRetrofitService.addProduct(imagePart, requestBody)
        } else {
            RetrofitObject.getRetrofitService.addProduct(null, requestBody)
        }

        call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
            override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity, "게시글이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    Toast.makeText(activity, "업로드 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                Toast.makeText(activity, "업로드 중 오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? ActivityMain)?.showBottomNaviagtion()
        _binding = null
    }

}