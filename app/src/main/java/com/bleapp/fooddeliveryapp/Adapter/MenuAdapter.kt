package com.bleapp.fooddeliveryapp.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bleapp.fooddeliveryapp.Activity.DetailsActivity
import com.bleapp.fooddeliveryapp.Model.MenuItem
import com.bleapp.fooddeliveryapp.databinding.MenuItemBinding
import com.bumptech.glide.Glide

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val requireContext: Context,
) :
    RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val itemClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        return MenuViewHolder(MenuItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false));
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {

        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsActivity(position)
                }

            }
        }

        private fun openDetailsActivity(position: Int) {
            val menuItem = menuItems[position]
            val intent = Intent(requireContext, DetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredient", menuItem.foodIngredient)
                putExtra("MenuItemPrice", menuItem.foodPrice)
            }
            requireContext.startActivity(intent)
        }

        fun bind(position: Int) {
            binding.apply {
                val menuItem = menuItems[position]

                menuFoodName.text = menuItem.foodName
                menuPrice.text = menuItem.foodPrice

                val uri = Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(menuImage)


            }

        }
    }

    interface OnClickListener {
        fun onItemClick(position: Int)
    }


}







