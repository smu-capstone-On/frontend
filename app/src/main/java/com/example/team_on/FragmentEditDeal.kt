package com.example.team_on

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.databinding.FragmentEditDealBinding
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class FragmentEditDeal : Fragment() {

    private var _binding: FragmentEditDealBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnSaveEditDeal: Button
    private lateinit var btnPreorder: Button
    private lateinit var btnSold: Button
    private lateinit var btnTagDog: Button
    private lateinit var btnTagCat: Button
    private lateinit var btnTagSmall: Button
    private lateinit var btnTagReptile: Button
    private lateinit var btnTagBird: Button
    private lateinit var btnEditImage: ImageButton
    private lateinit var toolbar: Toolbar
    private lateinit var selectedTags: String

    private var isSelectedPreorder: Boolean = false
    private var isSelectedSold: Boolean = false
    private var imageUri: Uri? = null // 선택된 이미지의 Uri를 저장할 변수

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            btnEditImage.setImageURI(it)
        }
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditDealBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnSaveEditDeal = binding.editDealBtnSave
        btnPreorder = binding.editDealBtnPreorder
        btnSold = binding.editDealBtnSold
        btnTagDog = binding.editDealTagDog
        btnTagCat = binding.editDealTagCat
        btnTagSmall = binding.editDealTagSmall
        btnTagReptile = binding.editDealTagReptile
        btnTagBird = binding.editDealTagBird
        btnEditImage = binding.editDealAddImage
        toolbar = binding.editDealToolbar

        setTagBtn()
        setBtn(btnPreorder)
        setBtn(btnSold)
        addImage()
        saveEditDeal()

        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setBtn(btn: Button) {
        btn.setOnClickListener {
            btn.isSelected = !btn.isSelected
            if (btn.isSelected) {
                btn.setTextColor(ContextCompat.getColor(btn.context, R.color.white))
                when (btn) {
                    btnPreorder -> isSelectedPreorder = true
                    btnSold -> isSelectedSold = true
                }
            } else {
                btn.setTextColor(ContextCompat.getColor(btn.context, R.color.hint))
            }
        }
    }

    // 태그 버튼 클릭 시
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

    // 물품 이미지 추가
    private fun addImage() {
        btnEditImage.setOnClickListener {
            getImage.launch("image/*")
        }
    }

    // 등록 버튼 클릭 시
    private fun saveEditDeal() {
        btnSaveEditDeal.setOnClickListener {
            val title = binding.editDealTitle.text.toString()
            val body = binding.editDealContent.text.toString()
            val price = binding.editDealEditPrice.text.toString()
            val reservationStatus = binding.editDealBtnPreorder.isSelected.toString()
            val saleStatus = binding.editDealBtnSold.isSelected.toString()
            val tag = convertTagToKorean(selectedTags)

            if (title.isEmpty() || body.isEmpty() || price.isEmpty()) {
                Toast.makeText(activity, "모든 필드를 올바르게 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val titleJson = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val bodyJson = body.toRequestBody("text/plain".toMediaTypeOrNull())
            val priceJson = price.toRequestBody("text/plain".toMediaTypeOrNull())
            val reservationJson = reservationStatus.toRequestBody("text/plain".toMediaTypeOrNull())
            val saleJson = saleStatus.toRequestBody("text/plain".toMediaTypeOrNull())
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

    private fun uploadDeal(title: RequestBody, body: RequestBody, price: RequestBody, reservation: RequestBody, sale: RequestBody, tag: RequestBody, imagePart: MultipartBody.Part?) {
        val call = if (imagePart != null) {
            RetrofitObject2.getRetrofitService.updateProduct(title, body, price, reservation, sale, tag, imagePart)
        } else {
            RetrofitObject2.getRetrofitService.updateProduct(title, body, price, reservation, sale, tag, null)
        }

        call.enqueue(object : Callback<Retrofit.Product2> {
            override fun onResponse(call: Call<Retrofit.Product2>, response: Response<Retrofit.Product2>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity, "물품이 수정되었습니다", Toast.LENGTH_SHORT).show()
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