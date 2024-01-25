package com.bleapp.adminwaveoffood.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bleapp.adminwaveoffood.Adapter.MenuItemAdapter
import com.bleapp.adminwaveoffood.Model.AllMenu
import com.bleapp.adminwaveoffood.R
import com.bleapp.adminwaveoffood.databinding.ActivityAllItemBinding
import com.google.firebase.database.*

class AllItemActivity : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var database: FirebaseDatabase
    private var menuItems: ArrayList<AllMenu> = ArrayList()

    private val binding: ActivityAllItemBinding by lazy {
        ActivityAllItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        databaseReference = FirebaseDatabase.getInstance().reference
        retriveMenuItem()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun retriveMenuItem() {
        database = FirebaseDatabase.getInstance()
        val foodRef: DatabaseReference = database.reference.child("menu")

        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()

                for (foodSnapshot: DataSnapshot in snapshot.children) {
                    val menuItem: AllMenu? = foodSnapshot.getValue(AllMenu::class.java)
                    menuItem?.let {
                        menuItems.add(it)
                    }
                }
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("databaseerror", "Error: ${error.message}")
            }
        })
    }

    private fun setAdapter() {
        val adapter =
            MenuItemAdapter(this@AllItemActivity, menuItems, databaseReference) { position ->
                deleteMenuItems(position)
            }
        binding.menuRV.layoutManager = LinearLayoutManager(this)
        binding.menuRV.adapter = adapter
    }

    private fun deleteMenuItems(position: Int) {
        val menuItemToDelete = menuItems[position]
        val menuItemKey = menuItemToDelete.key
        val foodMenuReference = database.reference.child("menu").child(menuItemKey!!)
        foodMenuReference.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                menuItems.removeAt(position)
                binding.menuRV.adapter?.notifyItemRemoved(position)
            } else {
                Toast.makeText(this, "Item not deleted", Toast.LENGTH_LONG).show()
            }
        }
    }
}