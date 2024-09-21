package com.example.team_on

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.example.team_on.databinding.FragmentDealDetailBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FragmentDealDetail : Fragment() {

    private var _binding: FragmentDealDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnchatting: Button
    private lateinit var toolbar: Toolbar

    private var title: String? = null
    private var body: String? = null
    private var tag: String? = null
    private var imgUrl: String? = null
    private var time: String? = null
    private var productId: Int? = null
    private var reservationStatus: Boolean? = null
    private var price: String? = null
    private val sharedPreference = KakaoSDK.user
    private val userId = sharedPreference.getString("userId", null)

    // 태그 매핑을 위한 Map 생성
    private val tagMapping = mapOf(
        "DOG" to "강아지",
        "CAT" to "고양이",
        "SMALL_ANIMAL" to "소동물",
        "REPILES" to "파충류",
        "BIRD" to "조류"
    )

    // 태그를 한글로 변환하는 함수
    private fun convertTagToKorean(tag: String): String {
        return tagMapping[tag] ?: tag // 매핑에 없으면 원래 태그 반환
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            title = it.getString(ARG_TITLE)
            body = it.getString(ARG_BODY)
            tag = it.getString(ARG_TAG)
            imgUrl = it.getString(ARG_IMGURL)
            time = it.getString(ARG_TIME)
            productId = it.getInt(ARG_PRODUCTID)
            reservationStatus = it.getBoolean(ARG_RESERVATIONSTATUS)
            price = it.getString(ARG_PRICE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDealDetailBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.dealDetailTitle.text = title
        binding.dealDetailContent.text = body
        binding.dealDetailDate.text = time?.let { formatPostTime(it) }
        binding.dealDetailPrice.text = price

        btnchatting = binding.dealDetailBtnChatting
        toolbar = binding.dealDetailToolbar

        if (productId.toString() == userId) {
            toolbar.inflateMenu(R.menu.nav_product)
        }

        val tags = tag

        if (tags != null) {
            val displayTag = convertTagToKorean(tags)
            binding.dealDetailTag1.text = displayTag
            binding.dealDetailTag1.visibility = View.VISIBLE
        }

        imgUrl?.let { url ->
            binding.dealDetailImageview.visibility = View.VISIBLE
            val uri = url.toUri().buildUpon().scheme("https").build()
            Glide.with(binding.dealDetailImageview.context)
                .load(uri) // URL을 URI로 변환하여 로드
                .error(R.drawable.svg_camera_error)
                .into(binding.dealDetailImageview) // 이미지가 로드될 ImageView
        }

        if (reservationStatus == true) {
            binding.dealDetailPreorder.visibility = View.VISIBLE
        }

        sendChat()

        toolbar.setNavigationOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        toolbar.setOnMenuItemClickListener { menu ->
            when (menu.itemId) {
                R.id.nav_product_edit -> {
                    (activity as? ActivityMain)?.hideBottomNavigation()

                    val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
                    transaction.replace(R.id.main_frame, FragmentEditDeal())
                    transaction.addToBackStack(null)
                    transaction.commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun sendChat() {
        btnchatting.setOnClickListener {

        }
    }

    fun formatPostTime(dateString: String): String {
        // 문자열을 LocalDateTime 객체로 파싱
        val dateTime = LocalDateTime.parse(dateString)

        // 원하는 형식으로 변환하기 위한 포맷
        val formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd   HH:mm")

        // 포맷팅된 문자열 반환
        return dateTime.format(formatter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (activity as? ActivityMain)?.showBottomNaviagtion()
        _binding = null
    }

    companion object {
        private const val ARG_TITLE = "title"
        private const val ARG_BODY = "body"
        private const val ARG_TAG = "tag"
        private const val ARG_IMGURL = "imgUrl"
        private const val ARG_TIME = "time"
        private const val ARG_PRODUCTID = "productId"
        private const val ARG_RESERVATIONSTATUS = "reservationStatus"
        private const val ARG_PRICE = "price"

        fun newInstance(title: String, body: String, tag: String, imgUrl: String?, time: String, reservationStatus: Boolean, price: String) =
            FragmentDealDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_BODY, body)
                    putString(ARG_TAG, tag)
                    putString(ARG_IMGURL, imgUrl)
                    putString(ARG_TIME, time)
                    putBoolean(ARG_RESERVATIONSTATUS, reservationStatus)
                    putString(ARG_PRICE, price + "원")
                }
            }
    }
}