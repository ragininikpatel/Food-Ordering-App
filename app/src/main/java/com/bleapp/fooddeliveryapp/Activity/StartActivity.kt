package com.bleapp.fooddeliveryapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bleapp.fooddeliveryapp.R
import com.bleapp.fooddeliveryapp.databinding.ActivityStart2Binding

class StartActivity : AppCompatActivity() {
    private val binding: ActivityStart2Binding by lazy {
        ActivityStart2Binding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        binding.btnNxt.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }
}