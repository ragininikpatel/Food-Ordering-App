package com.bleapp.adminwaveoffood.Activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bleapp.adminwaveoffood.Model.AllMenu
import com.bleapp.adminwaveoffood.databinding.ActivityAddItemBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask

class AddItemActivity : AppCompatActivity() {

    //food item details
    private lateinit var foodName: String
    private lateinit var foodPrice: String
    private lateinit var foodDescription: String
    private lateinit var foodIngredient: String
    private var foodImageUri: Uri? = null

    //firebase
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase


    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        //init firebase ref
        auth = FirebaseAuth.getInstance()

        //init firebase database instance
        database = FirebaseDatabase.getInstance()

        binding.AddItemButton.setOnClickListener {
            foodName = binding.etName.text.toString().trim()
            foodPrice = binding.etPrice.text.toString().trim()
            foodDescription = binding.description.text.toString().trim()
            foodIngredient = binding.etIngredient.text.toString().trim()

            if (!(foodName.isBlank() || foodPrice.isBlank() || foodDescription.isBlank() || foodIngredient.isBlank())) {
                uploadData()
                Toast.makeText(this, "Item Add Successfully", Toast.LENGTH_LONG).show()
                finish()
            } else {
                Toast.makeText(this, "Please Fill all details", Toast.LENGTH_LONG).show()
            }


        }
        binding.txtSelectImg.setOnClickListener {
            pickImage.launch("image/*")
        }

        binding.backButton.setOnClickListener {
            finish()
        }

    }

    private fun uploadData() {
        //get menu ref
        val menuRef: DatabaseReference = database.getReference("menu")

        //generate unique key for menu
        val newItemKey: String? = menuRef.push().key

        if (foodImageUri != null) {
            val storageRef: StorageReference = FirebaseStorage.getInstance().reference
            val imageRef: StorageReference = storageRef.child("menu_iamges/${newItemKey}.jpg")
            val uploadTask: UploadTask = imageRef.putFile(foodImageUri!!)

            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->

                    //create a new menu item
                    val newItem = AllMenu(
                        newItemKey,
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodDescription = foodDescription,
                        foodIngredient = foodIngredient,
                        foodImage = downloadUrl.toString()
                    )

                    newItemKey?.let { key ->
                        menuRef.child(key).setValue(newItem).addOnSuccessListener {
                            Toast.makeText(this, "data uploaded successfully..", Toast.LENGTH_LONG)
                                .show()
                        }
                            .addOnFailureListener {
                                Toast.makeText(this, "data uploaded failed..", Toast.LENGTH_LONG)
                                    .show()

                            }
                    }
                }

            }
                .addOnFailureListener {
                    Toast.makeText(this, "image uploaded failed..", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this, "Please select an image..", Toast.LENGTH_LONG).show()
        }

    }

    val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            binding.selectedImg.setImageURI(uri)
            foodImageUri = uri
        }
    }


}