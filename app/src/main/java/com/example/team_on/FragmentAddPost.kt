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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.databinding.FragmentAddPostBinding
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

class FragmentAddPost : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnAddPost: Button
    private lateinit var btnTagDog: Button
    private lateinit var btnTagCat: Button
    private lateinit var btnTagSmall: Button
    private lateinit var btnTagReptile: Button
    private lateinit var btnTagBird: Button
    private lateinit var btnTagQuestion: Button
    private lateinit var editTextTitle: EditText
    private lateinit var editTextContent: EditText
    private lateinit var btnAddImage: ImageButton
    private lateinit var imageView: ImageView
    private lateinit var toolbar: Toolbar

    private var selectedTags = mutableListOf<String>()
    private var selectedImageUri: Uri? = null

    // 태그 매핑을 위한 Map 생성
    private val tagMapping = mapOf(
        "강아지" to "DOG",
        "고양이" to "CAT",
        "소동물" to "SMALL_ANIMAL",
        "파충류" to "REPILES",
        "조류" to "BIRD",
        "질문" to "QUESTION"
    )

    // 태그 변환
    private fun convertTagToKorean(tag: String): String {
        return tagMapping[tag] ?: tag // 매핑에 없으면 원래 태그 반환
    }

    private val getImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            imageView.setImageURI(it)
            imageView.visibility = View.VISIBLE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddPostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnAddPost = binding.addPostEnterPost
        btnTagDog = binding.addPostTagDog
        btnTagCat = binding.addPostTagCat
        btnTagSmall = binding.addPostTagSmall
        btnTagReptile = binding.addPostTagReptile
        btnTagBird = binding.addPostTagBird
        btnTagQuestion = binding.addPostTagQuestion
        editTextTitle = binding.addPostTitle
        editTextContent = binding.addPostContent
        btnAddImage = binding.addPostAddImage
        imageView = binding.addPostImageView
        toolbar = binding.addPostToolbar

        setTagBtn()
        addImage()
        addPost()

        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun setTagBtn() {
        val btns = listOf(btnTagDog, btnTagCat, btnTagSmall, btnTagReptile, btnTagBird, btnTagQuestion)
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

    private fun addImage() {
        btnAddImage.setOnClickListener {
            getImage.launch("image/*")
        }
    }

    private fun addPost() {
        btnAddPost.setOnClickListener {
            val title = editTextTitle.text.toString()
            val body = editTextContent.text.toString()
            val userId = KakaoSDK.user.getString("userId", 0.toString())
            val tagList = selectedTags

            for (i in selectedTags.indices) {
                tagList[i] = convertTagToKorean(tagList[i])
            }

            if (title.isEmpty()) {
                Toast.makeText(activity, "게시글 제목이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (body.isEmpty()) {
                Toast.makeText(activity, "게시글 내용이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userIdJson = userId?.toRequestBody("text/plain".toMediaTypeOrNull())
            val titleJson = title.toRequestBody("text/plain".toMediaTypeOrNull())
            val bodyJson = body.toRequestBody("text/plain".toMediaTypeOrNull())
            val tagTypesString = tagList.joinToString(",")
            val tagTypesJson = tagTypesString.toRequestBody("text/plain".toMediaTypeOrNull())

            // 이미지 처리
            val imagePart: MultipartBody.Part? = selectedImageUri?.let { uri ->
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
            } ?: createDefaultImagePart() // 이미지가 없으면 기본 이미지 사용

            if (userIdJson != null) {
                uploadPost(imagePart, userIdJson, titleJson, bodyJson, tagTypesJson)
            }
        }
    }

    private fun uploadPost(imagePart: MultipartBody.Part?, id: RequestBody, title: RequestBody, body: RequestBody, tag: RequestBody) {
        val call = RetrofitObject2.getRetrofitService.addPost(imagePart, id, title, body, tag)

        call.enqueue(object : Callback<Retrofit.Post2> {
            override fun onResponse(call: Call<Retrofit.Post2>, response: Response<Retrofit.Post2>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity, "게시글이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    Toast.makeText(activity, "업로드 실패: $errorBody", Toast.LENGTH_SHORT).show()
                    Log.e("UploadError", "Response code: ${response.code()}, Error: $errorBody")
                }
            }

            override fun onFailure(call: Call<Retrofit.Post2>, t: Throwable) {
                Toast.makeText(activity, "업로드 중 오류 발생: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createDefaultImagePart(): MultipartBody.Part? {
        return try {
            // 1x1 픽셀의 투명한 비트맵 생성
            val bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

            // 비트맵을 파일로 저장
            val file = File(requireContext().cacheDir, "default_image.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            // 파일을 RequestBody로 변환
            val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())

            // MultipartBody.Part로 변환
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        } catch (e: Exception) {
            Toast.makeText(activity, "기본 이미지를 생성하는 데 실패했습니다: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? ActivityMain)?.showBottomNaviagtion()
        _binding = null
    }
}
