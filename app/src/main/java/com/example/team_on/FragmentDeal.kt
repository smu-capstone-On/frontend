package com.example.team_on

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.team_on.connection.Retrofit
import com.example.team_on.connection.RetrofitObject
import com.example.team_on.databinding.FragmentDealBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
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

    private lateinit var productAdapter: AdapterProduct
    private var productList = mutableListOf<Retrofit.Product>()
    private var filteredList = mutableListOf<Retrofit.Product>()
    private var selectedTags = mutableListOf<String>()
    private var isPreOrderSelected: Boolean? = null
    private var sortCriteria: String? = null
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

        setTagBtn()
        setSearchFun()
        setSearchCondition()
        setSortBtn()
        stopSearchFun()
        addDeal()

        productList = mutableListOf(
            Retrofit.Product(1, "user1", "Product1", "Product1 sell", listOf("강아지"), Date(System.currentTimeMillis()), null, 21000, false),
            Retrofit.Product(2, "user2", "Product2", "Product2 sell", listOf("강아지"), Date(System.currentTimeMillis()), null, 2200, false),
            Retrofit.Product(3, "user3", "Product3", "Product3 sell", listOf("고양이"), Date(System.currentTimeMillis()), null, 23000, false),
            Retrofit.Product(4, "user4", "Product4", "Product4 sell", listOf("강아지"), Date(System.currentTimeMillis()), null, 24000, true),
            Retrofit.Product(5, "user5", "Product5", "Product5 sell", listOf("고양이"), Date(System.currentTimeMillis()), null, 2500, false),
            Retrofit.Product(6, "user6", "Product6", "Product6 sell", listOf("소동물"), Date(System.currentTimeMillis()), null, 26000, false),
            Retrofit.Product(7, "user7", "Product7", "Product7 sell", listOf("조류"), Date(System.currentTimeMillis()), null, 2700, true),
            Retrofit.Product(8, "user8", "Product8", "Product8 sell", listOf("파충류"), Date(System.currentTimeMillis()), null, 2000, false),
            Retrofit.Product(9, "user9", "Product9", "Product9 sell", listOf("소동물"), Date(System.currentTimeMillis()), null, 29000, true),
            Retrofit.Product(10, "user10", "Product10", "Product10 sell", listOf("조류"), Date(System.currentTimeMillis()), null, 1000, true),
            Retrofit.Product(11, "user11", "Product11", "Product11 sell", listOf("파충류"), Date(System.currentTimeMillis()), null, 11000, false),
            Retrofit.Product(12, "user12", "Product12", "Product12 sell", listOf("강아지"), Date(System.currentTimeMillis()), null, 1200, false)
        )

        productAdapter = AdapterProduct(productList.toMutableList()) { product ->
            val fragment = FragmentDealDetail.newInstance(product.title, product.price.toString(), product.createdTime.toString())
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
            val matchesTag = selectedTags.isEmpty() || product.tags!!.any { it in selectedTags }
            val matchesPreorder = isPreOrderSelected == null || product.isPreorder == isPreOrderSelected
            matchesText && matchesTag && matchesPreorder
        }
        filteredList.clear()
        filteredList.addAll(filteredProducts)
        sortProduct(sortCriteria)
    }

    // 정렬 기준에 따른 물건 리스트 정렬
    private fun sortProduct(criteria: String?) {
        val comparator = when (criteria) {
            "new" -> compareBy<Retrofit.Product> {it.createdTime}
            "price" -> compareBy {it.price}
            else -> compareBy {it.createdTime}
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
        val call = RetrofitObject.getRetrofitService.readProducts()
        call.enqueue(object : Callback<List<Retrofit.Product>> {
            override fun onResponse(call: Call<List<Retrofit.Product>>, response: Response<List<Retrofit.Product>>) {
                if (response.isSuccessful) {
                    val product = response.body()
                    if (product != null) {
                        // 성공
                    } else {
                        // 데이터가 없는 경우
                    }
                } else {
                    // 실패한 경우
                }
            }

            override fun onFailure(call: Call<List<Retrofit.Product>>, t: Throwable) {
                // 네트워크 에러 등 실패
            }

        })
    }

    // 가져온 데이터 적용
    fun display() {}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}