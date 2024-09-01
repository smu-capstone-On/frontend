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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.FragmentAddPostBinding
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
            val tagList = selectedTags

            if (title.isEmpty()) {
                Toast.makeText(activity, "게시글 제목이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (body.isEmpty()) {
                Toast.makeText(activity, "게시글 내용이 입력되지 않았습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val data = JSONObject().apply {
                put("userId", 1)
                put("title", title)
                put("body", body)
                put("tagTypes", tagList)
            }

            val requestBody = data.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            var imagePart: MultipartBody.Part? = null

            selectedImageUri?.let { uri ->
                val bitmap = (imageView.drawable as BitmapDrawable).bitmap
                val file = File(requireContext().cacheDir, "image.jpg")
                val outputStream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                imagePart = MultipartBody.Part.createFormData("file", file.name, requestFile)
            }

            uploadPost(imagePart, requestBody)
        }
    }

    private fun uploadPost(imagePart: MultipartBody.Part?, requestBody: okhttp3.RequestBody) {
        val call = if (imagePart != null) {
            RetrofitObject.getRetrofitService.addPost(imagePart, requestBody)
        } else {
            RetrofitObject.getRetrofitService.addPost(null, requestBody)
        }
        call.enqueue(object : Callback<Retrofit.ResponseChatImage> {
            override fun onResponse(call: Call<Retrofit.ResponseChatImage>, response: Response<Retrofit.ResponseChatImage>) {
                if (response.isSuccessful) {
                    Toast.makeText(activity, "게시글이 업로드되었습니다.", Toast.LENGTH_SHORT).show()
                    requireActivity().supportFragmentManager.popBackStack()
                } else {
                    Toast.makeText(activity, "업로드 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Retrofit.ResponseChatImage>, t: Throwable) {
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
