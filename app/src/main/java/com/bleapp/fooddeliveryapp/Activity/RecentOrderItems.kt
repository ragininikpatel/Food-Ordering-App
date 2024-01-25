package com.bleapp.fooddeliveryapp.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.bleapp.fooddeliveryapp.Adapter.RecentBuyAdapter
import com.bleapp.fooddeliveryapp.Model.OrderDetails
import com.bleapp.fooddeliveryapp.R
import com.bleapp.fooddeliveryapp.databinding.ActivityRecentOrderItemsBinding

class RecentOrderItems : AppCompatActivity() {
    private val binding: ActivityRecentOrderItemsBinding by lazy {
        ActivityRecentOrderItemsBinding.inflate(layoutInflater)
    }
    private lateinit var allFoodNames: ArrayList<String>
    private lateinit var allFoodImage: ArrayList<String>
    private lateinit var allFoodPrice: ArrayList<String>
    private lateinit var allFoodQty: ArrayList<Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val recentOrderItems =
            intent.getSerializableExtra("RecentBuyOrderItem") as ArrayList<OrderDetails>
        recentOrderItems?.let { orderDetails ->
            if (orderDetails.isNotEmpty()) {
                val recentOrderItem = orderDetails[0]
                allFoodNames = recentOrderItem.foodNames as ArrayList<String>
                allFoodImage = recentOrderItem.foodImages as ArrayList<String>
                allFoodPrice = recentOrderItem.foodPrices as ArrayList<String>
                allFoodQty = recentOrderItem.foodQuantities as ArrayList<Int>
            }
        }
        setAdapter()

        binding.backBtn.setOnClickListener {
            finish()
        }
    }

    private fun setAdapter() {
        val rv = binding.rvRecentOrder
        rv.layoutManager = LinearLayoutManager(this)
        val adapter = RecentBuyAdapter(this, allFoodNames, allFoodImage, allFoodPrice, allFoodQty)
        rv.adapter = adapter
    }
}