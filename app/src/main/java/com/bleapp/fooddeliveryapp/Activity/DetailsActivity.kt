package com.bleapp.fooddeliveryapp.Activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bleapp.fooddeliveryapp.Model.CardItems
import com.bleapp.fooddeliveryapp.R
import com.bleapp.fooddeliveryapp.databinding.ActivityDetailsBinding
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class DetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailsBinding

    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodIngredient: String? = null
    private var foodPrice: String? = null
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        foodName = intent.getStringExtra("MenuItemName")
        foodImage = intent.getStringExtra("MenuItemImage")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredient = intent.getStringExtra("MenuItemIngredient")
        foodPrice = intent.getStringExtra("MenuItemPrice")

        with(binding) {
            detailFoodName.text = foodName
            descTextview.text = foodDescription
            ingradientTextview.text = foodIngredient
            Glide.with(this@DetailsActivity).load(Uri.parse(foodImage)).into(detailFoodImage)
        }

        binding.addToCart.setOnClickListener {
            addItemToCart()
        }

        binding.imageButton.setOnClickListener {
            finish()
        }
    }

    private fun addItemToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: ""
        val cardItems = CardItems(
            foodName.toString(),
            foodPrice.toString(),
            foodDescription.toString(),
            foodImage.toString(), 1)

        database.child("user").child(userId).child("CartItems").push().setValue(cardItems)
            .addOnSuccessListener {
                Toast.makeText(this, "Item Added in cart successfully", Toast.LENGTH_LONG)
                    .show()
            }.addOnFailureListener {
                Toast.makeText(this, "Item Not Added", Toast.LENGTH_LONG).show()

            }
    }
}