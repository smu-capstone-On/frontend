package com.example.team_on

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.connection.RetrofitObject2
import com.example.team_on.databinding.FragmentDealBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class FragmentDeal : Fragment() {

    private var _binding: FragmentDealBinding? = null
    private val binding get() = _binding!!

    private lateinit var btnTagDog: Button
    private lateinit var btnTagCat: Button
    private lateinit var btnTagSmall: Button
    private lateinit var btnTagReptile: Button
    private lateinit var btnTagBird: Button
    private lateinit var btnSortNew: Button
    private lateinit var btnSortPrice: Button
    private lateinit var btnAddPreorder: Button
    private lateinit var btnSubPreorder: Button
    private lateinit var editTextSearch: EditText
    private lateinit var btnSearch: ImageButton
    private lateinit var btnSearchCondition: ImageButton
    private lateinit var btnSearchCancel: ImageButton
    private lateinit var btnAddDeal: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutSearchCondition: ConstraintLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var imageUrl: String

    private lateinit var productAdapter: AdapterProduct
    private var productList = mutableListOf<Retrofit.Product3>()
    private var filteredList = mutableListOf<Retrofit.Product3>()
    private var selectedTags = mutableListOf<String>()
    private var isPreOrderSelected: Boolean? = null
    private var sortCriteria: String? = null

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDealBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnTagDog = binding.dealTagDog
        btnTagCat = binding.dealTagCat
        btnTagSmall = binding.dealTagSmall
        btnTagReptile = binding.dealTagReptile
        btnTagBird = binding.dealTagBird
        btnSortNew = binding.dealBtnSortNew
        btnSortPrice = binding.dealBtnSortPrice
        btnAddPreorder = binding.dealBtnAddPreorder
        btnSubPreorder = binding.dealBtnSubPreorder
        editTextSearch = binding.dealEditSearch
        btnSearch = binding.dealBtnSearch
        btnSearchCondition = binding.dealBtnSearchCondition
        btnSearchCancel = binding.dealBtnSearchCancel
        btnAddDeal = binding.dealBtnPost
        recyclerView = binding.dealRecyclerview
        layoutSearchCondition = binding.dealLayoutSearchCondition
        progressBar = binding.dealProgressBar

        productList = mutableListOf()

        productAdapter = AdapterProduct(productList) { product ->
            val fragment = FragmentDealDetail.newInstance(product.title, product.body, product.tagType, product.url, product.createDate, product.reservationStatus, product.price.toString())
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.main_frame, fragment)
                ?.addToBackStack(null)
                ?.commit()
            (activity as? ActivityMain)?.hideBottomNavigation()
        }

        recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = productAdapter
        }

        fetchProduct()
        setTagBtn()
        setSearchFun()
        setSearchCondition()
        setSortBtn()
        stopSearchFun()
        addDeal()

    }

    // 검색 활성화
    private fun setSearchFun() {
        btnSearch.setOnClickListener {
            btnSearch.isEnabled = false
            btnSearchCancel.visibility = View.VISIBLE
            editTextSearch.visibility = View.VISIBLE
            editTextSearch.requestFocus()
        }
        editTextSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter()
            }

            override fun afterTextChanged(s: Editable?) {}

        })
    }

    // 검색 취소
    private fun stopSearchFun(){
        btnSearchCancel.setOnClickListener {
            btnSearch.isEnabled = true
            btnSearchCancel.visibility = View.GONE
            editTextSearch.visibility = View.GONE
            editTextSearch.text.clear()
            editTextSearch.clearFocus()
            filter()
        }
    }

    // 상품 태그 클릭 시
    private fun setTagBtn() {
        val btns = listOf(btnTagDog, btnTagCat, btnTagSmall, btnTagReptile, btnTagBird)

        btns.forEach { button ->
            button.setOnClickListener {
                button.isSelected = !button.isSelected
                if (button.isSelected) {
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.white))
                    selectedTags.add(button.text.toString())
                } else {
                    button.setTextColor(ContextCompat.getColor(button.context, R.color.hint))
                    selectedTags.remove(button.text.toString())
                }
                filter()
            }
        }
    }

    // 검색 단어 및 태그 필터링
    private fun filter() {
        val searchText = editTextSearch.text.toString().lowercase(Locale.ROOT)
        val filteredProducts = productList.filter { product ->
            val matchesText = product.title.lowercase(Locale.ROOT).contains(searchText)
            val matchesTag = if(selectedTags.isEmpty()) {
                true
            } else {
                val koreanTags = convertTagToKorean(product.tagType)
                koreanTags in selectedTags
            }
            val matchesPreorder = when (isPreOrderSelected) {
                true -> true // preorder가 true인 경우, 모든 reservationStatus 포함
                false -> product.reservationStatus == false // preorder가 false인 경우, reservationStatus가 false인 것만 포함
                else -> true // preorder가 선택되지 않은 경우, 모든 제품 포함
            }
            matchesText && matchesTag && matchesPreorder
        }
        filteredList.clear()
        filteredList.addAll(filteredProducts)
        sortProduct(sortCriteria)
    }

    // 정렬 기준에 따른 물건 리스트 정렬
    private fun sortProduct(criteria: String?) {
        val comparator = when (criteria) {
            "new" -> compareByDescending<Retrofit.Product3> { it.createDate }
            "price" -> compareBy { it.price }  // 가격에 따른 오름차순 정렬
            else -> compareByDescending { it.createDate }
        }
        comparator.let {
            filteredList.sortWith(it)
        }
        productAdapter.filterList(filteredList)
    }

    // 버튼 클릭 시, 다른 버튼은 클릭 취소
    private fun setSwitchBtn(selectedBtn: Button, canceledBtn: Button) {
        selectedBtn.setOnClickListener {
            selectedBtn.isSelected = !selectedBtn.isSelected
            if (selectedBtn.isSelected) {
                setClearBtn(canceledBtn)
                selectedBtn.setTextColor(ContextCompat.getColor(selectedBtn.context, R.color.white))
                when (selectedBtn) {
                    btnAddPreorder -> isPreOrderSelected = true
                    btnSubPreorder -> isPreOrderSelected = false
                    btnSortNew -> sortCriteria = "new"
                    btnSortPrice -> sortCriteria = "price"
                }
                filter()
            } else {
                selectedBtn.setTextColor(ContextCompat.getColor(selectedBtn.context, R.color.hint))
                if (selectedBtn == btnAddPreorder || selectedBtn == btnSubPreorder) {
                    isPreOrderSelected = null
                } else if (selectedBtn == btnSortNew || selectedBtn == btnSortPrice) {
                    sortCriteria = null
                }
                filter()
            }
        }
    }

    private fun setSortBtn() {
        setSwitchBtn(btnSortNew, btnSortPrice)
        setSwitchBtn(btnSortPrice, btnSortNew)
        setSwitchBtn(btnAddPreorder, btnSubPreorder)
        setSwitchBtn(btnSubPreorder, btnAddPreorder)
    }

    // 검색 조건 세부 설정 버튼
    private fun setSearchCondition() {
        btnSearchCondition.setOnClickListener {
            if (layoutSearchCondition.isVisible) {
                layoutSearchCondition.visibility = View.GONE
                setClearBtn(btnSortNew)
                setClearBtn(btnSortPrice)
                setClearBtn(btnAddPreorder)
                setClearBtn(btnSubPreorder)
            } else {
                layoutSearchCondition.visibility = View.VISIBLE
            }
        }
    }

    // 버튼 초기화
    private fun setClearBtn(clearBtn: Button) {
        clearBtn.isSelected = false
        clearBtn.setTextColor(ContextCompat.getColor(clearBtn.context, R.color.hint))
    }

    // 중고거래 상품 추가
    private fun addDeal(){
        btnAddDeal.setOnClickListener {
            (activity as? ActivityMain)?.hideBottomNavigation()

            val transaction: FragmentTransaction = parentFragmentManager.beginTransaction()
            transaction.replace(R.id.main_frame, FragmentAddDeal())
            transaction.addToBackStack(null)
            transaction.commit()
        }
    }

    // 물품 데이터 가져오기
    private fun fetchProduct() {
        progressBar.visibility = View.VISIBLE

        val call = RetrofitObject2.getRetrofitService.getAllProducts()
        call.enqueue(object : Callback<List<Retrofit.Product2>> {
            override fun onResponse(call: Call<List<Retrofit.Product2>>, response: Response<List<Retrofit.Product2>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    productList.clear()
                    val sortedProducts = products.sortedByDescending { it.createDate }

                    // 비동기 작업의 총 개수를 추적
                    var pendingCallbacks = products.size
                    if (pendingCallbacks == 0) {
                        progressBar.visibility = View.GONE
                        updateProductList() // 만약 product가 없을 때를 대비한 처리
                    }

                    for (product in sortedProducts) {
                        if (product.fileInfo != null) {
                            val call = RetrofitObject.getRetrofitService.loadImg(product.fileInfo.id)
                            call.enqueue(object : Callback<Retrofit.FileUrl> {
                                override fun onResponse(call: Call<Retrofit.FileUrl>, response: Response<Retrofit.FileUrl>) {
                                    if (response.isSuccessful) {
                                        val fileInfo = response.body()
                                        fileInfo?.let {
                                            imageUrl = it.url // 서버에서 받아온 URL을 추출
                                            productList.add(
                                                Retrofit.Product3(
                                                    product.createDate,
                                                    product.title,
                                                    product.body,
                                                    product.price,
                                                    product.reservationStatus,
                                                    product.saleStatus,
                                                    product.tagType,
                                                    product.fileInfo,
                                                    imageUrl
                                                )
                                            )
                                        }
                                    }
                                    // 콜백 완료 시마다 카운터 감소
                                    pendingCallbacks--
                                    if (pendingCallbacks == 0) {
                                        progressBar.visibility = View.GONE
                                        updateProductList()
                                    }
                                }

                                override fun onFailure(call: Call<Retrofit.FileUrl>, t: Throwable) {
                                    val errorMessage = "Call Failed: ${t.message}"
                                    Log.d("Retrofit", errorMessage)
                                    // 실패해도 카운터 감소
                                    pendingCallbacks--
                                    if (pendingCallbacks == 0) {
                                        progressBar.visibility = View.GONE
                                        updateProductList()
                                    }
                                }
                            })
                        } else {
                            // fileInfo가 없는 경우 바로 productList에 추가
                            productList.add(
                                Retrofit.Product3(
                                    product.createDate,
                                    product.title,
                                    product.body,
                                    product.price,
                                    product.reservationStatus,
                                    product.saleStatus,
                                    product.tagType,
                                    null,
                                    null
                                )
                            )
                            // 콜백 완료 시마다 카운터 감소
                            pendingCallbacks--
                            if (pendingCallbacks == 0) {
                                progressBar.visibility = View.GONE
                                updateProductList()
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Error: ${response.code()} - ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Retrofit.Product2>>, t: Throwable) {
                Toast.makeText(context, "Failure: ${t.message}", Toast.LENGTH_SHORT).show()
                t.printStackTrace()  // 실패 시 로그 남기기
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateProductList() {
        filteredList.clear()
        filteredList.addAll(productList)
        productAdapter.filterList(filteredList)

        Toast.makeText(context, "물품이 업데이트되었습니다.", Toast.LENGTH_SHORT).show()
    }
}