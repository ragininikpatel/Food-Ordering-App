package com.bleapp.fooddeliveryapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bleapp.fooddeliveryapp.databinding.NotificationItemBinding

class NotificationAdapter(
    private var notification: ArrayList<String>,
    private var notificationImage: ArrayList<Int>
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(NotificationItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false));
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        holder.bind(position)
    }

    override fun getItemCount(): Int = notification.size

    inner class NotificationViewHolder(private val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.apply {
                notificationTv.text = notification[position]
                notificationIv.setImageResource(notificationImage[position])
            }
        }


    }


}