package com.bleapp.adminwaveoffood.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bleapp.adminwaveoffood.Adapter.PendingOrderAdapter
import com.bleapp.adminwaveoffood.Model.OrderDetails
import com.bleapp.adminwaveoffood.databinding.ActivityPendingOrderBinding
import com.google.firebase.database.*

class PendingOrderActivity : AppCompatActivity(), PendingOrderAdapter.OnItemClicked {
    private lateinit var binding: ActivityPendingOrderBinding
    private var listOfName: MutableList<String> = mutableListOf()
    private var listOfTotalPrice: MutableList<String> = mutableListOf()
    private var listOfImageFirstFoodOrder: MutableList<String> = mutableListOf()
    private var listOfOrderItem: ArrayList<OrderDetails> = arrayListOf()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseOrderDetails: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPendingOrderBinding.inflate(layoutInflater)

        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        database = FirebaseDatabase.getInstance()

        databaseOrderDetails = database.reference.child("OrderDetails")

        getOrderDetails()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun getOrderDetails() {
        databaseOrderDetails.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnapshot in snapshot.children) {
                    val orderDetails = orderSnapshot.getValue(OrderDetails::class.java)
                    orderDetails?.let {
                        listOfOrderItem.add(it)
                    }
                }
                addDataToListForRecyclerView()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun addDataToListForRecyclerView() {
        for (orderItem in listOfOrderItem) {
            //add data to list for populating the rv
            orderItem.userName?.let {
                listOfName.add(it)
                orderItem.totalPrice?.let {
                    listOfTotalPrice.add(it)
                    orderItem.foodImages?.filterNot { it.isEmpty() }?.forEach {
                        listOfImageFirstFoodOrder.add(it)
                    }
                }
                setAdapter()
            }
        }
    }

    private fun setAdapter() {
        binding.rvPendingOrder.layoutManager = LinearLayoutManager(this)
        val adapter =
            PendingOrderAdapter(this, listOfName, listOfTotalPrice, listOfImageFirstFoodOrder, this)
        binding.rvPendingOrder.adapter = adapter
    }

    override fun onItemClickListener(position: Int) {
        val intent = Intent(this, OrderDetailsActivity::class.java)
        val userOrderDetails = listOfOrderItem[position]
        intent.putExtra("userOrderDetails", userOrderDetails)
        startActivity(intent)
    }

    override fun onItemAcceptClickListener(position: Int) {
        //handle item acceptence and update db
        val childItemPushKey = listOfOrderItem[position].itemPushKey
        val clickItemOrderReference = childItemPushKey?.let {
            database.reference.child("OrderDetails").child(it)
        }
        clickItemOrderReference?.child("orderAccepted")?.setValue(true)
        updateOrderAcceptedStatus(position)
    }

    private fun updateOrderAcceptedStatus(position: Int) {
        val userIdOfClickedItem = listOfOrderItem[position].userUid
        val pushKeyOfClickedItem = listOfOrderItem[position].itemPushKey
        val buyHistoryReference = database.reference.child("user").child(userIdOfClickedItem!!).child("BuyHistory").child(pushKeyOfClickedItem!!)
        buyHistoryReference.child("orderAccepted").setValue(true)
        databaseOrderDetails.child(pushKeyOfClickedItem).child("orderAccepted").setValue(true)
    }

    override fun onItemDispatchClickListener(position: Int) {
        val dispatchItemPushKey = listOfOrderItem[position].itemPushKey
        val dispatchItemOrderReference = database.reference.child("CompletedOrder").child(dispatchItemPushKey!!)
        dispatchItemOrderReference.setValue(listOfOrderItem[position])
            .addOnSuccessListener {
                this.deleteThisItemFromOrderDetails(dispatchItemPushKey)
            }
    }

    override fun deleteThisItemFromOrderDetails(dispatchItemPushKey: String) {
        val orderDetailsItemsReferences = database.reference.child("OrderDetails").child(dispatchItemPushKey)
        orderDetailsItemsReferences.removeValue()
            .addOnSuccessListener {
                Toast.makeText(this,"Order is Dispatched",Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener{
                Toast.makeText(this,"Order is not Dispatched",Toast.LENGTH_LONG).show()

            }
    }
}