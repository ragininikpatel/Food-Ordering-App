package com.bleapp.adminwaveoffood.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bleapp.adminwaveoffood.Adapter.DeliveryAdapter
import com.bleapp.adminwaveoffood.Model.OrderDetails
import com.bleapp.adminwaveoffood.R
import com.bleapp.adminwaveoffood.databinding.ActivityOutForDeliveryBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OutForDeliveryActivity : AppCompatActivity() {
    private val binding:ActivityOutForDeliveryBinding by lazy {
        ActivityOutForDeliveryBinding.inflate(layoutInflater)
    }

    private lateinit var database: FirebaseDatabase
    private var listOfCompleteOrderList:ArrayList<OrderDetails> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        retriveCompletedOrderDetails()

        binding.backButton.setOnClickListener{
            finish()
        }
    }

    private fun retriveCompletedOrderDetails() {
        database  = FirebaseDatabase.getInstance()
        val completeOrderReference = database.reference.child("CompletedOrder").orderByChild("currentTime")
        completeOrderReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                //clear the list before populating it new data
                listOfCompleteOrderList.clear()
                for (orderSnapshot in snapshot.children){
                    val completeOrder = orderSnapshot.getValue(OrderDetails::class.java)
                    completeOrder?.let {
                        listOfCompleteOrderList.add(it)
                    }
                }

                listOfCompleteOrderList.reverse()

                setDataInRecyclerView()

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun setDataInRecyclerView() {
        val customerName = mutableListOf<String>()
        val moneyStatus = mutableListOf<Boolean>()

        for(order in listOfCompleteOrderList){
            order.userName?.let {
                customerName.add(it)
            }
            moneyStatus.add(order.paymentReceived)
        }

        val adapter = DeliveryAdapter(customerName,moneyStatus)
        binding.rvDelivery.layoutManager = LinearLayoutManager(this)
        binding.rvDelivery.adapter = adapter

    }
}