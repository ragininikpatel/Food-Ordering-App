package com.bleapp.adminwaveoffood.Adapter

import android.content.Context
import android.net.Uri
import android.os.Message
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bleapp.adminwaveoffood.databinding.OrderDetailItemBinding
import com.bumptech.glide.Glide

class OrderDetailAdapter(
    private val context: Context,
    private val foodNames: ArrayList<String>,
    private val foodImages: ArrayList<String>,
    private val foodQnty: ArrayList<Int>,
    private val foodPrices: ArrayList<String>,
) : RecyclerView.Adapter<OrderDetailAdapter.OrderDetailsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderDetailsViewHolder {
        val binding =
            OrderDetailItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderDetailsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderDetailsViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = foodNames.size

    inner class OrderDetailsViewHolder(private val binding: OrderDetailItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var isAccepted = false
        fun bind(position: Int) {
            binding.apply {
                foodName.text = foodNames[position]
                foodQty.text = foodQnty[position].toString()

                var uriString = foodImages[position]
                var uri = Uri.parse(uriString)
                Glide.with(context).load(uri).into(foodImage)
                foodPrice.text = foodPrices[position]

            }


        }


    }

}