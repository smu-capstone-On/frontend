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
import com.example.team_on.databinding.FragmentDealBinding
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
    private var productList = mutableListOf<Product>()
    private var filteredList = mutableListOf<Product>()
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
            Product("Product1", 21000, Date(System.currentTimeMillis()), "Product1 sell", listOf("강아지")),
            Product("Product2", 2200, Date(System.currentTimeMillis()), "Product2 sell", listOf("강아지")),
            Product("Product3", 23000, Date(System.currentTimeMillis()), "Product3 sell", listOf("고양이")),
            Product("Product4", 24000, Date(System.currentTimeMillis()), "Product4 sell", listOf("강아지")),
            Product("Product5", 2500, Date(System.currentTimeMillis()), "Product5 sell", listOf("고양이")),
            Product("Product6", 26000, Date(System.currentTimeMillis()), "Product6 sell", listOf("소동물")),
            Product("Product7", 2700, Date(System.currentTimeMillis()), "Product7 sell", listOf("조류")),
            Product("Product8", 2000, Date(System.currentTimeMillis()), "Product8 sell", listOf("파충류")),
            Product("Product9", 29000, Date(System.currentTimeMillis()), "Product9 sell", listOf("소동물")),
            Product("Product10", 1000, Date(System.currentTimeMillis()), "Product10 sell", listOf("조류")),
            Product("Product11", 11000, Date(System.currentTimeMillis()), "Product11 sell", listOf("파충류")),
            Product("Product12", 1200, Date(System.currentTimeMillis()), "Product12 sell", listOf("강아지"))
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
            "new" -> compareBy<Product> {it.createdTime}
            "price" -> compareBy<Product> {it.price}
            else -> compareBy<Product> {it.createdTime}
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}