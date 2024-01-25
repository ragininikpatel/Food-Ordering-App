package com.bleapp.adminwaveoffood.Activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bleapp.adminwaveoffood.Model.UserModel
import com.bleapp.adminwaveoffood.R
import com.bleapp.adminwaveoffood.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var database: DatabaseReference
    private var userName: String? = null
    private var nameOfRestaurant: String? = null

    private lateinit var googleSignInClient: GoogleSignInClient

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        //google
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //init
        auth = Firebase.auth

        //init db
        database = Firebase.database.reference


        binding.btnLogin.setOnClickListener {

            email = binding.etEmail.text.toString().trim()
            password = binding.etPw.text.toString().trim()

            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please Fill all details", Toast.LENGTH_LONG).show()
            } else {
                createUserAccount(email, password)
            }
        }

        binding.googleBtn.setOnClickListener {
            val signIntent: Intent = googleSignInClient.signInIntent
            launcher.launch(signIntent)
        }
        binding.txtSignup.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun createUserAccount(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = auth.currentUser
                Toast.makeText(this, "Login Successfully.", Toast.LENGTH_LONG).show()

                updateUI(user)
            } else {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user: FirebaseUser? = auth.currentUser
                            Toast.makeText(this,
                                "Create User & Login Successfully.",
                                Toast.LENGTH_LONG).show()
                            saveUserData()
                            updateUI(user)
                        } else {
                            Toast.makeText(this, "Authentication failed.", Toast.LENGTH_LONG).show()
                            Log.d("create acc failed", task.exception.toString())
                        }
                    }
            }

        }
    }

    private fun saveUserData() {
        email = binding.etEmail.text.toString().trim()
        password = binding.etPw.text.toString().trim()

        val user = UserModel(userName, nameOfRestaurant, email, password)
        val userId: String? = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            database.child("user").child(it).setValue(user)
        }
    }


    //launcher for google
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                if (task.isSuccessful) {
                    val account: GoogleSignInAccount = task.result
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            Toast.makeText(this,
                                "Successfully signin with google.",
                                Toast.LENGTH_LONG).show()

                            updateUI(authTask.result?.user)
                            finish()

                        } else {
                            Toast.makeText(this,
                                "google signin failed.",
                                Toast.LENGTH_LONG).show()

                        }
                    }
                } else {
                    Toast.makeText(this,
                        "google signin failed.",
                        Toast.LENGTH_LONG).show()

                }
            }
        }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}