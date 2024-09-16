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
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
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

    private lateinit var selectedTags: String
    private var imageUri: Uri? = null // 선택된 이미지의 Uri를 저장할 변수

    // 태그 매핑을 위한 Map 생성
    private val tagMapping = mapOf(
        "강아지" to "DOG",
        "고양이" to "CAT",
        "소동물" to "SMALL_ANIMAL",
        "파충류" to "REPILES",
        "조류" to "BIRD"
    )

    // 태그 변환
    private fun convertTagToKorean(tag: String): String {
        return tagMapping[tag] ?: tag // 매핑에 없으면 원래 태그 반환
    }

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
                // 선택된 태그를 제외한 나머지 버튼들은 선택 해제
                btns.forEach { otherButton ->
                    if (otherButton != button) {
                        otherButton.isSelected = false
                        otherButton.setTextColor(ContextCompat.getColor(otherButton.context, R.color.hint))
                    }
                }
                // 현재 클릭한 버튼의 선택 상태
                button.isSelected = !button.isSelected

                if (button.isSelected) {
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
                    selectedTags = button.text.toString()
                } else {
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.hint))
                    selectedTags = ""
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
            val price = editTextPrice.text.toString()
            val tag = convertTagToKorean(selectedTags)

            if (title.isEmpty() || body.isEmpty() || price.isEmpty()) {
                Toast.makeText(activity, "모든 필드를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val titleJson = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val bodyJson = body.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceJson = price.toRequestBody("text/plain".toMediaTypeOrNull())
            val reservationJson = "false".toRequestBody("text/plain".toMediaTypeOrNull())
            val saleJson = "false".toRequestBody("text/plain".toMediaTypeOrNull())
            val tagTypesJson = tag.toRequestBody("text/plain".toMediaTypeOrNull())

            // 이미지를 MultipartBody.Part로 변환
            val imagePart = imageUri?.let { uri ->
                try {
                    val file = File(requireContext().cacheDir, "image.jpg")
                    requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                        file.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("file", file.name, requestFile)
                } catch (e: Exception) {
                    Toast.makeText(activity, "이미지를 처리하는 데 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
                    null
                }
            }

            uploadDeal(titleJson, bodyJson, priceJson, reservationJson, saleJson, tagTypesJson, imagePart)
        }
    }

    // 서버로 데이터 전송
    private fun uploadDeal(title: RequestBody, body: RequestBody, price: RequestBody, reservation: RequestBody, sale: RequestBody, tag: RequestBody, imagePart: MultipartBody.Part?) {
        val call = if (imagePart != null) {
            RetrofitObject2.getRetrofitService.addProduct(title, body, price, reservation, sale, tag, imagePart)
        } else {
            RetrofitObject2.getRetrofitService.addProduct(title, body, price, reservation, sale, tag, null)
        }

        call.enqueue(object : Callback<Retrofit.Product2> {
            override fun onResponse(call: Call<Retrofit.Product2>, response: Response<Retrofit.Product2>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity, "물품이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(activity, "업로드 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                    Log.e("UploadError", "Response code: ${response.code()}, Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<Retrofit.Product2>, t: Throwable) {
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
