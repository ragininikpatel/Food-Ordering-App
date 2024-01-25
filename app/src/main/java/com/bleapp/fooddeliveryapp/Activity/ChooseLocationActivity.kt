package com.bleapp.fooddeliveryapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.bleapp.fooddeliveryapp.R
import com.bleapp.fooddeliveryapp.databinding.ActivityChooseLocationBinding

class ChooseLocationActivity : AppCompatActivity() {
    private val binding: ActivityChooseLocationBinding by lazy {
        ActivityChooseLocationBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        val locationList: Array<String> = arrayOf("Jaipur", "Odisha", "Bundi", "Sikar");
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList);
        val autoCompleteTextView: AutoCompleteTextView =  binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)

    }
}