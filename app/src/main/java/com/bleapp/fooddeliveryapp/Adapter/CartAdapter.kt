package com.bleapp.fooddeliveryapp.Adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bleapp.fooddeliveryapp.databinding.CartItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartAdapter(
    private val context: Context,
    private val cardItems: MutableList<String>,
    private val cardItemPrices: MutableList<String>,
    private var cartDescription: MutableList<String>,
    private val cardImages: MutableList<String>,
    private var cartQty: MutableList<Int>,
    private var cartIngredient: MutableList<String>,

    ) :
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    //instance firebase
    private val auth = FirebaseAuth.getInstance()

    init {
        val database = FirebaseDatabase.getInstance()
        val userId: String = auth.currentUser?.uid ?: ""
        val cartItemNumber: Int = cardItems.size

        itemQntitites = IntArray(cartItemNumber) { 1 }
        cartItemsReference = database.reference.child("user").child(userId).child("CartItems")
    }

    companion object {
        private var itemQntitites: IntArray = intArrayOf()
        private lateinit var cartItemsReference: DatabaseReference
    }

    //  private val itemQty = IntArray(cardItems.size) { 1 }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(CartItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false));
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {

        holder.bind(position)
    }

    override fun getItemCount(): Int = cardItems.size
    fun getUpdatedQty(): MutableList<Int> {
        val itemQty = mutableListOf<Int>()
        itemQty.addAll(cartQty)
        return itemQty
    }


    inner class CartViewHolder(private val binding: CartItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {
                val qty = itemQntitites[position]
                cartFoodName.text = cardItems[position]
                cartItemPrice.text = cardItemPrices[position]

                //load image using glide
                val uriString: String = cardImages[position]
                val uri = Uri.parse(uriString)
                Glide.with(context).load(uri)
                    /* .listener(object : RequestListener<Drawable> {
                     override fun onLoadFailed(
                         e: GlideException?,
                         model: Any?,
                         target: Target<Drawable>,
                         isFirstResource: Boolean,
                     ): Boolean {
                         Log.d("Glide", "onLoadFailed: Image Loading Failed");
                         return false;
                     }

                     override fun onResourceReady(
                         resource: Drawable,
                         model: Any,
                         target: Target<Drawable>?,
                         dataSource: DataSource,
                         isFirstResource: Boolean,
                     ): Boolean {
                         Log.d("Glide", "onLoadFailed: Image Loading Success");
                         return false;
                     }

                 })*/
                    .into(cartImage)

                cardItemQty.text = qty.toString()

                minusbutton.setOnClickListener {
                    decreaseQty(position)
                }
                plusbutton.setOnClickListener {
                    increaseQty(position)
                }
                deletebutton.setOnClickListener {
                    val pos = adapterPosition
                    if (pos != RecyclerView.NO_POSITION) {
                        deleteItem(pos)
                    }
                }
            }
        }

        fun decreaseQty(position: Int) {
            if (itemQntitites[position] > 1) {
                itemQntitites[position]--
                cartQty[position] = itemQntitites[position]
                binding.cardItemQty.text = itemQntitites[position].toString()
            }
        }

        fun increaseQty(position: Int) {
            if (itemQntitites[position] < 10) {
                itemQntitites[position]++
                cartQty[position] = itemQntitites[position]
                binding.cardItemQty.text = itemQntitites[position].toString()
            }
        }

        fun deleteItem(position: Int) {
            val positionRetrieve = position
            getUniqueKeyAtPosition(positionRetrieve) { uniqueKey ->
                if (uniqueKey != null) {
                    removeItem(position, uniqueKey)
                }
            }
        }

        private fun removeItem(position: Int, uniqueKey: String) {
            if (uniqueKey != null) {
                cartItemsReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    cardItems.removeAt(position)
                    cardImages.removeAt(position)
                    cartDescription.removeAt(position)
                    cartQty.removeAt(position)
                    cardItemPrices.removeAt(position)
                    cartIngredient.removeAt(position)

                    Toast.makeText(context, "item deleted", Toast.LENGTH_LONG).show()

                    //update item qty
                    itemQntitites =
                        itemQntitites.filterIndexed { index, i -> index != position }.toIntArray()
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, cardItems.size)
                }.addOnSuccessListener {
                    Toast.makeText(context, "failed to delete", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String?) -> Unit) {
        cartItemsReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniqueKey: String? = null

                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if (index == positionRetrieve) {
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }

                onComplete(uniqueKey)
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}

