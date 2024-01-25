package com.bleapp.fooddeliveryapp.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bleapp.fooddeliveryapp.Model.UserModel
import com.bleapp.fooddeliveryapp.R
import com.bleapp.fooddeliveryapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        setUserData()

        binding.btnEditProfile.setOnClickListener {
            binding.apply {

                etName.isEnabled = false
                etEmail.isEnabled = false
                etAddress.isEnabled = false
                etPhone.isEnabled = false

                etName.isEnabled = !etName.isEnabled
                etEmail.isEnabled = !etEmail.isEnabled
                etAddress.isEnabled = !etAddress.isEnabled
                etPhone.isEnabled = !etPhone.isEnabled
            }
        }

        binding.btnSaveInfo.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val address = binding.etAddress.text.toString()
            val phone = binding.etPhone.text.toString()

            updateUserData(name, email, address, phone)
        }
        return binding.root


    }

    private fun updateUserData(name: String, email: String, address: String, phone: String) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userReference = database.getReference("user").child(userId)

            val userData = hashMapOf(
                "name" to name,
                "address" to address,
                "email" to email,
                "phone" to phone,
            )

            userReference.setValue(userData).addOnSuccessListener {
                Toast.makeText(context, "profile updated successfully..", Toast.LENGTH_LONG).show()
            }.addOnFailureListener {
                Toast.makeText(context, "profile updated failed..", Toast.LENGTH_LONG).show()

            }
        }
    }


    private fun setUserData() {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val userReference = database.getReference("user").child(userId)

            userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userProfile = snapshot.getValue(UserModel::class.java)
                        if (userProfile != null) {
                            binding.etName.setText(userProfile.name)
                            binding.etAddress.setText(userProfile.address)
                            binding.etEmail.setText(userProfile.email)
                            binding.etPhone.setText(userProfile.phone)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
        }
    }

}