package com.example.team_on

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.team_on.databinding.ActivityMainBinding

class ActivityMain : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var fragmentHome: FragmentHome? = null
    private var fragmentCommunity: FragmentCommunity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        initBottomNavigation()
    }

    private fun initBottomNavigation() {
        fragmentHome = FragmentHome()
        supportFragmentManager.beginTransaction().replace(R.id.main_frameLayout, fragmentHome!!).commit()

        binding.mainBottomNavi.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_bottom_home -> {
                    if (fragmentHome == null) {
                        fragmentHome = FragmentHome()
                        supportFragmentManager.beginTransaction().add(R.id.main_frameLayout, fragmentHome!!).commit()
                    }
                    if (fragmentHome != null) supportFragmentManager.beginTransaction().show(fragmentHome!!).commit()

                    if (fragmentCommunity != null) supportFragmentManager.beginTransaction().hide(fragmentCommunity!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.nav_bottom_walk -> {

                    if (fragmentHome != null) supportFragmentManager.beginTransaction().hide(fragmentHome!!).commit()

                    if (fragmentCommunity != null) supportFragmentManager.beginTransaction().hide(fragmentCommunity!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.nav_bottom_community -> {
                    if (fragmentCommunity == null){
                        fragmentCommunity = FragmentCommunity()
                        supportFragmentManager.beginTransaction().add(R.id.main_frameLayout, fragmentCommunity!!).commit()
                    }
                    if (fragmentHome != null) supportFragmentManager.beginTransaction().hide(fragmentHome!!).commit()

                    if (fragmentCommunity != null) supportFragmentManager.beginTransaction().show(fragmentCommunity!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.nav_bottom_deal -> {

                    if (fragmentHome != null) supportFragmentManager.beginTransaction().hide(fragmentHome!!).commit()

                    if (fragmentCommunity != null) supportFragmentManager.beginTransaction().hide(fragmentCommunity!!).commit()

                    return@setOnItemSelectedListener true
                }
                R.id.nav_bottom_mypage -> {

                    if (fragmentHome != null) supportFragmentManager.beginTransaction().hide(fragmentHome!!).commit()

                    if (fragmentCommunity != null) supportFragmentManager.beginTransaction().hide(fragmentCommunity!!).commit()

                    return@setOnItemSelectedListener true
                }
                else ->{
                    return@setOnItemSelectedListener true
                }
            }
        }
    }
}