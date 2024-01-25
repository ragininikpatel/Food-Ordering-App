package com.bleapp.adminwaveoffood.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bleapp.adminwaveoffood.Model.UserModel
import com.bleapp.adminwaveoffood.R
import com.bleapp.adminwaveoffood.databinding.ActivityAdminProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AdminProfileActivity : AppCompatActivity() {
    private val binding: ActivityAdminProfileBinding by lazy {
        ActivityAdminProfileBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var adminReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        adminReference = database.reference.child("user")

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.etName.isEnabled = false
        binding.etAddress.isEnabled = false
        binding.etEmail.isEnabled = false
        binding.etPhone.isEnabled = false
        binding.etPassword.isEnabled = false
        binding.saveBtnInfo.isEnabled = false

        var isEnable = false
        binding.editProfile.setOnClickListener {
            isEnable = !isEnable
            binding.etName.isEnabled = isEnable
            binding.etAddress.isEnabled = isEnable
            binding.etEmail.isEnabled = isEnable
            binding.etPhone.isEnabled = isEnable
            binding.etPassword.isEnabled = isEnable

            if (isEnable) {
                binding.etName.requestFocus()
            }

            binding.saveBtnInfo.isEnabled = isEnable

            retrieveUserData()
        }

        binding.saveBtnInfo.setOnClickListener {
            updateUserData()
        }
    }

    private fun updateUserData() {
        var updateName = binding.etName.text.toString()
        var updateEmail = binding.etEmail.text.toString()
        var updatePassword = binding.etPassword.text.toString()
        var updatePhone = binding.etPhone.text.toString()
        var updateAddress = binding.etAddress.text.toString()

        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)
            userReference.child("name").setValue(updateName)
            userReference.child("email").setValue(updateEmail)
            userReference.child("password").setValue(updatePassword)
            userReference.child("phone").setValue(updatePhone)
            userReference.child("address").setValue(updateAddress)
            Toast.makeText(this, "Profile Updated Successfully..", Toast.LENGTH_LONG).show()
            auth.currentUser?.updateEmail(updateEmail)
            auth.currentUser?.updatePassword(updatePassword)
        } else {
            Toast.makeText(this, "Profile Updated Failed..", Toast.LENGTH_LONG).show()

        }


    }


    private fun retrieveUserData() {
        val currentUserUid = auth.currentUser?.uid
        if (currentUserUid != null) {
            val userReference = adminReference.child(currentUserUid)
            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var ownerName = snapshot.child("name").getValue()
                        var email = snapshot.child("email").getValue()
                        var password = snapshot.child("password").getValue()
                        var address = snapshot.child("address").getValue()
                        var phone = snapshot.child("phone").getValue()

                        Log.d("my tag", "OnDataChanged:$ownerName,")
                        setDataToTextView(ownerName, email, password, address, phone)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
        }

    }

    private fun setDataToTextView(
        ownerName: Any?,
        email: Any?,
        password: Any?,
        address: Any?,
        phone: Any?,
    ) {
        binding.etName.setText(ownerName.toString())
        binding.etEmail.setText(email.toString())
        binding.etPassword.setText(password.toString())
        binding.etPhone.setText(phone.toString())
        binding.etAddress.setText(address.toString())

    }
}