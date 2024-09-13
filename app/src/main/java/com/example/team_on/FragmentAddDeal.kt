package com.example.team_on

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import com.example.team_on.connection.RetrofitObject2
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
    private var imageUri: Uri? = null // 선택된 이미지의 Uri를 저장할 변수

    // 이미지 선택을 위한 ActivityResultContracts 사용
    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            btnAddImage.setImageURI(it) // 선택된 이미지 버튼에 표시
        }
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
                } else {
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

    // 이미지 선택 버튼 설정
    private fun addImage() {
        btnAddImage.setOnClickListener {
            getImage.launch("image/*") // 이미지 선택 창 열기
        }
    }

    // 물품 등록하기
    private fun addDeal() {
        btnAddDeal.setOnClickListener {
            val title = editTextTitle.text.toString()
            val body = editTextContent.text.toString()
            val tagList = selectedTags
            val price = editTextPrice.text.toString().toIntOrNull()

            if (title.isEmpty() || body.isEmpty() || price == null) {
                Toast.makeText(activity, "모든 필드를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = JSONObject().apply {
                put("title", title)
                put("body", body)
                put("price", price)
                put("reservationStatus", false)
                put("saleStatus", false)
                put("tagTypes", tagList)
            }

            val requestBody = data.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

            // 이미지를 MultipartBody.Part로 변환
            val imagePart = imageUri?.let { uri ->
                val bitmap = requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    BitmapDrawable(resources, inputStream).bitmap
                }

                bitmap?.let {
                    val file = File(requireContext().cacheDir, "image.jpg")
                    val outputStream = FileOutputStream(file)
                    it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", file.name, requestFile)
                }
            }

            uploadDeal(imagePart, requestBody)
        }
    }

    // 서버로 데이터 전송
    private fun uploadDeal(imagePart: MultipartBody.Part?, requestBody: okhttp3.RequestBody) {
        val call = if (imagePart != null) {
            RetrofitObject2.getRetrofitService.addProduct(imagePart, requestBody)
        } else {
            RetrofitObject2.getRetrofitService.addProduct(null, requestBody)
        }

        call.enqueue(object : Callback<Retrofit.ResponseSuccess> {
            override fun onResponse(call: Call<Retrofit.ResponseSuccess>, response: Response<Retrofit.ResponseSuccess>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity, "물품이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(activity, "업로드 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("UploadError", "Response code: ${response.code()}, Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<Retrofit.ResponseSuccess>, t: Throwable) {
                Toast.makeText(activity, "업로드 중 오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
