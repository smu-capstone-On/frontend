package com.example.team_on

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.team_on.databinding.ActivityMypageBinding

class ActivityMypage : AppCompatActivity() {

    private val binding: ActivityMypageBinding by lazy { ActivityMypageBinding.inflate(layoutInflater) }

    private lateinit var btnEditProfile: ImageButton
    private lateinit var btnViewArticle: ImageButton
    private lateinit var btnViewComment: ImageButton
    private lateinit var btnViewBlacklist: ImageButton
    private lateinit var btnManageAccount: ImageButton
    private lateinit var btnLogout: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        btnEditProfile = binding.mypageBtnEditProfile
        btnViewArticle = binding.mypageBtnArticle
        btnViewComment = binding.mypageBtnComment
        btnViewBlacklist = binding.mypageBtnBlacklist
        btnManageAccount = binding.mypageBtnManageAccount
        btnLogout = binding.mypageBtnLogout

        btnEditProfile.setOnClickListener {
            val intent = Intent(this, ActivityEditProfile::class.java)
            startActivity(intent)
        }

        btnManageAccount.setOnClickListener {
            val intent = Intent(this, ActivityManageAccount::class.java)
            startActivity(intent)
        }
    }
}