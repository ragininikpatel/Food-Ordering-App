package com.bleapp.fooddeliveryapp.Fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bleapp.fooddeliveryapp.Activity.PayOutActivity
import com.bleapp.fooddeliveryapp.Adapter.CartAdapter
import com.bleapp.fooddeliveryapp.Model.CardItems
import com.bleapp.fooddeliveryapp.R
import com.bleapp.fooddeliveryapp.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class CartFragment : Fragment() {
    private lateinit var binding: FragmentCartBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var foodNames: MutableList<String>
    private lateinit var foodPrices: MutableList<String>
    private lateinit var foodDesc: MutableList<String>
    private lateinit var foodImagesUri: MutableList<String>
    private lateinit var foodIngredient: MutableList<String>
    private lateinit var qty: MutableList<Int>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCartBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        retrieveCartItems()


        binding.btnProceed.setOnClickListener {
            //get order items details before proceeding to check out
            getOrderItemDetails()
        }
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun getOrderItemDetails() {
        val orderIdReferences: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")

        val foodName = mutableListOf<String>()
        val foodPrice = mutableListOf<String>()
        val foodImage = mutableListOf<String>()
        val foodDesc = mutableListOf<String>()
        val foodIngredient = mutableListOf<String>()

        //get item qty
        val foodQty = cartAdapter.getUpdatedQty()

        orderIdReferences.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    //get the card items to respectative list
                    val orderItems = foodSnapshot.getValue(CardItems::class.java)

                    //add item details into list
                    orderItems?.foodName?.let { foodName.add(it) }
                    orderItems?.foodPrice?.let { foodPrice.add(it) }
                    orderItems?.foodDescription?.let { foodDesc.add(it) }
                    orderItems?.foodImage?.let { foodImage.add(it) }
                    orderItems?.foodIngredient?.let { foodIngredient.add(it) }
                }

                orderNow(foodName, foodPrice, foodDesc, foodImage, foodIngredient, foodQty)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,
                    "order making failed..please try again..",
                    Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun orderNow(
        foodName: MutableList<String>,
        foodPrice: MutableList<String>,
        foodDesc: MutableList<String>,
        foodImage: MutableList<String>,
        foodIngredient: MutableList<String>,
        foodQty: MutableList<Int>,
    ) {
        if (isAdded && context != null) {
            val intent = Intent(requireContext(), PayOutActivity::class.java)
            intent.putExtra("FoodItemName", foodName as ArrayList<String>)
            intent.putExtra("FoodItemPrice", foodPrice as ArrayList<String>)
            intent.putExtra("FoodItemImage", foodImage as ArrayList<String>)
            intent.putExtra("FoodItemDescription", foodDesc as ArrayList<String>)
            intent.putExtra("FoodItemIngredient", foodIngredient as ArrayList<String>)
            intent.putExtra("FoodItemQuantities", foodQty as ArrayList<String>)
            startActivity(intent)
        }
    }

    private fun retrieveCartItems() {
        database = FirebaseDatabase.getInstance()
        userId = auth.currentUser?.uid ?: ""
        val foodRef: DatabaseReference =
            database.reference.child("user").child(userId).child("CartItems")

        //list to store cart items
        foodNames = mutableListOf()
        foodPrices = mutableListOf()
        foodDesc = mutableListOf()
        foodImagesUri = mutableListOf()
        foodIngredient = mutableListOf()
        qty = mutableListOf()

        //fetch data from database
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (foodSnapshot in snapshot.children) {
                    val cartItems = foodSnapshot.getValue(CardItems::class.java)

                    cartItems?.foodName?.let { foodNames.add(it) }
                    cartItems?.foodPrice?.let { foodPrices.add(it) }
                    cartItems?.foodDescription?.let { foodDesc.add(it) }
                    cartItems?.foodImage?.let { foodImagesUri.add(it) }
                    cartItems?.foodQuentity?.let { qty.add(it) }
                    cartItems?.foodIngredient?.let { foodIngredient.add(it) }
                }

                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "data not match", Toast.LENGTH_LONG).show()
            }

        })

    }

    private fun setAdapter() {
        cartAdapter =
            CartAdapter(requireContext(),
                foodNames,
                foodPrices,
                foodDesc,
                foodImagesUri,
                qty,
                foodIngredient);

        binding.cardRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.cardRv.adapter = cartAdapter

    }
}