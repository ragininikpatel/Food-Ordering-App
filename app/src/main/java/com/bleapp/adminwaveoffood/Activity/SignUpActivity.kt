package com.bleapp.adminwaveoffood.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import com.bleapp.adminwaveoffood.Model.UserModel
import com.bleapp.adminwaveoffood.R
import com.bleapp.adminwaveoffood.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var userName: String
    private lateinit var nameOfRestaurant: String
    private lateinit var database: DatabaseReference


    private val binding: ActivitySignUpBinding by lazy {
        ActivitySignUpBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        //authentication
        auth = Firebase.auth

        //init
        database = Firebase.database.reference

        val locationList: Array<String> = arrayOf("Jaipur", "Odisha", "Bundi", "Sikar");
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(this, android.R.layout.simple_list_item_1, locationList);
        val autoCompleteTextView: AutoCompleteTextView = binding.listOfLocation
        autoCompleteTextView.setAdapter(adapter)

        binding.btnSignup.setOnClickListener {

            userName = binding.etName.text.toString().trim()
            nameOfRestaurant = binding.etRestName.text.toString().trim()
            email = binding.etEmail.text.toString().trim()
            password = binding.etPw.text.toString().trim()

            if (userName.isBlank() || nameOfRestaurant.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this,"Please Fill all details", Toast.LENGTH_LONG).show()
            }else{
                createAccount(email,password)
            }

        }

        binding.txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{task->
            if(task.isSuccessful){
                Toast.makeText(this,"Account Created Successfully..", Toast.LENGTH_LONG).show()
                saveUserData()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,"Account Creation Failed..", Toast.LENGTH_LONG).show()
                Log.d("failed create acc", task.exception.toString())
            }
        }
    }

    private fun saveUserData() {
        userName = binding.etName.text.toString().trim()
        nameOfRestaurant = binding.etRestName.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPw.text.toString().trim()

        val user = UserModel(userName,nameOfRestaurant,email, password)
        val userId: String = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("user").child(userId).setValue(user)
    }
}