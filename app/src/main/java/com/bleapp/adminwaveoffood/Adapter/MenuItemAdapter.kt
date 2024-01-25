package com.bleapp.adminwaveoffood.Adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bleapp.adminwaveoffood.Model.AllMenu
import com.bleapp.adminwaveoffood.databinding.AllItemBinding
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference

class MenuItemAdapter(
    private val context: Context,
    private val menuList: ArrayList<AllMenu>,
    databaseReference: DatabaseReference,
    private val onDeleteClickListener: (position: Int) -> Unit,
) : RecyclerView.Adapter<MenuItemAdapter.AddItemViewHolder>() {

    private val itemQty = IntArray(menuList.size) { 1 }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddItemViewHolder {
        val binding = AllItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddItemViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuList.size

    inner class AddItemViewHolder(private val binding: AllItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val qty = itemQty[position]
                val menuItem: AllMenu = menuList[position]
                val uriString: String? = menuItem.foodImage
                val uri = Uri.parse(uriString)

                foodNameTextView.text = menuItem.foodName
                priceTextView.text = menuItem.foodPrice
                Glide.with(context).load(uri).into(foodImageView)
                quantityTextView.text = qty.toString()

                minusButton.setOnClickListener {
                    decreaseQty(position)
                }
                plusButton.setOnClickListener {
                    increaseQty(position)
                }
                deleteButton.setOnClickListener {
                    onDeleteClickListener(position)
                }

            }


        }

        fun decreaseQty(position: Int) {
            if (itemQty[position] > 1) {
                itemQty[position]--
                binding.quantityTextView.text = itemQty[position].toString()
            }
        }

        fun increaseQty(position: Int) {
            if (itemQty[position] < 10) {
                itemQty[position]++
                binding.quantityTextView.text = itemQty[position].toString()
            }
        }

        fun deleteItem(position: Int) {
            menuList.removeAt(position)
            menuList.removeAt(position)
            menuList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, menuList.size)
        }

    }

}