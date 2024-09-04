package com.example.team_on

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.core.net.toUri
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
    private var tag: List<String>? = null
    private var imgUrl: String? = null
    private var time: String? = null
    private var productId: Int? = null
    private var reservationStatus: Boolean? = null
    private var price: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            title = it.getString(ARG_TITLE)
            body = it.getString(ARG_BODY)
            tag = it.getStringArrayList(ARG_TAG)
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

        val tags = tag
        val productTags = listOf(binding.dealDetailTag1, binding.dealDetailTag2, binding.dealDetailTag3)

        if (tags != null) {
            for (i in tags.indices) {
                if (i < tags.size) {
                    productTags[i].text = tags[i]
                    productTags[i].visibility = View.VISIBLE
                } else {
                    productTags[i].text = ""
                    productTags[i].visibility = View.GONE
                }
            }
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

        fun newInstance(title: String, body: String, tag: List<String>, imgUrl: String?, time: String, productId: Int, reservationStatus: Boolean, price: String) =
            FragmentDealDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_BODY, body)
                    putStringArrayList(ARG_TAG, ArrayList(tag))
                    putString(ARG_IMGURL, imgUrl)
                    putString(ARG_TIME, time)
                    putInt(ARG_PRODUCTID, productId)
                    putBoolean(ARG_RESERVATIONSTATUS, reservationStatus)
                    putString(ARG_PRICE, price + "원")
                }
            }
    }
}