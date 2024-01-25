package com.bleapp.adminwaveoffood.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bleapp.adminwaveoffood.Adapter.OrderDetailAdapter
import com.bleapp.adminwaveoffood.Model.OrderDetails
import com.bleapp.adminwaveoffood.R
import com.bleapp.adminwaveoffood.databinding.ActivityOrderDetailsBinding

class OrderDetailsActivity : AppCompatActivity() {
    private val binding: ActivityOrderDetailsBinding by lazy {
        ActivityOrderDetailsBinding.inflate(layoutInflater)
    }

    private var userName: String? = null
    private var address: String? = null
    private var phoneNumber: String? = null
    private var totalPrice: String? = null
    private var foodNames: ArrayList<String> = arrayListOf()
    private var foodImages: ArrayList<String> = arrayListOf()
    private var foodQuantity: ArrayList<Int> = arrayListOf()
    private var foodPrices: ArrayList<String> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.imageButton.setOnClickListener {
            finish()
        }

        getDataFromIntent()
    }

    private fun getDataFromIntent() {
        val receivedOrderDetails = intent.getSerializableExtra("userOrderDetails") as OrderDetails
        receivedOrderDetails?.let { orderDetails ->

            userName = receivedOrderDetails.userName
            foodNames = receivedOrderDetails.foodNames as ArrayList<String>
            foodImages = receivedOrderDetails.foodImages as ArrayList<String>
            foodQuantity = receivedOrderDetails.foodQuantities as ArrayList<Int>
            address = receivedOrderDetails.address!!
            phoneNumber = receivedOrderDetails.phoneNumber!!
            foodPrices = receivedOrderDetails.foodPrices as ArrayList<String>
            totalPrice = receivedOrderDetails.totalPrice!!

            setDetails()
            setAdapter()

        }

    }

    private fun setDetails() {
        binding.etName.text = userName
        binding.etAddress.text = address
        binding.etPhone.text = phoneNumber
        binding.totalPay.text = totalPrice
    }

    private fun setAdapter() {
        binding.rvDetails.layoutManager = LinearLayoutManager(this)
        val adapter = OrderDetailAdapter(this, foodNames, foodImages, foodQuantity, foodPrices)
        binding.rvDetails.adapter = adapter
    }

}