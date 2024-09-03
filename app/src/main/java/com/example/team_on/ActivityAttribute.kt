package com.example.team_on

import android.os.Bundle
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import com.example.team_on.databinding.ActivityAttributeBinding

class ActivityAttribute : AppCompatActivity() {

    private val binding: ActivityAttributeBinding by lazy { ActivityAttributeBinding.inflate(layoutInflater) }
    private val sharedPreference = MySharedPreference.user
    private val editor = sharedPreference.edit()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val pet = sharedPreference.getString("pet", "")
        val gender = sharedPreference.getString("gender", "")
        val age = sharedPreference.getString("age", null)
        val editStart = binding.attributeEditAge1
        val editEnd = binding.attributeEditAge2
        val btnSave = binding.attributeBtnSave
        val btnReset = binding.attributeBtnReset
        val btnBack = binding.attributeBtnBack

        if(age != null){
            val ageRange = age.split(" ")
            editStart.setText(ageRange[0])
            editEnd.setText(ageRange[1])
        }

        val groupPet = binding.attributeRadioPet
        if(pet == "있음"){
            groupPet.check(R.id.attribute_btn_pet_O)
        }else if(pet == "없음"){
            groupPet.check(R.id.attribute_btn_pet_X)
        }

        val groupGender = binding.attributeRadioGender
        if(gender == "남자"){
            groupGender.check(R.id.attribute_btn_m)
        }else if(gender == "여자"){
            groupGender.check(R.id.attribute_btn_w)
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnReset.setOnClickListener {
            groupPet.clearCheck()
            groupGender.clearCheck()
            editStart.setText("")
            editEnd.setText("")
        }

        btnSave.setOnClickListener {

            val selectedGenderId = groupGender.checkedRadioButtonId
            if (selectedGenderId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedGenderId)
                val selectedText = selectedRadioButton.text.toString()
                editor.putString("gender", selectedText)
            } else {
                editor.remove("gender")  // 선택된 버튼이 없을 경우
            }

            val selectedPetId = groupPet.checkedRadioButtonId
            if (selectedPetId != -1) {
                val selectedRadioButton = findViewById<RadioButton>(selectedPetId)
                val selectedText = selectedRadioButton.text.toString()
                editor.putString("pet", selectedText)
            } else {
                editor.remove("pet")  // 선택된 버튼이 없을 경우
            }

            if(editStart.text.isEmpty() && editEnd.text.isNotEmpty()){
                val e = editEnd.text.toString()
                editor.putString("age", "00 $e")
            }else if(editStart.text.isNotEmpty() && editEnd.text.isEmpty()){
                val s = editStart.text.toString()
                editor.putString("age", "$s 100")
            }else{
                val s = editStart.text.toString()
                val e = editEnd.text.toString()
                editor.putString("age", "$s $e")
            }

            editor.apply()
            finish()
        }
    }
}