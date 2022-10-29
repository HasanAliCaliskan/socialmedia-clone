package com.hasanali.kotlininstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.hasanali.kotlininstagram.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val v = binding.root
        setContentView(v)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        if(currentUser != null){
            val intent = Intent(this,FeedActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    fun signin(v: View) {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        if(email != "" && password != "") {
            auth.signInWithEmailAndPassword(email.toString(), password.toString())
                .addOnSuccessListener {
                    val intent = Intent(this,FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this,"Enter email and password.", Toast.LENGTH_SHORT).show()
        }
    }

    fun signup(v: View) {
        val email = binding.editTextEmail.text.toString()
        val password = binding.editTextPassword.text.toString()
        if(email != "" && password != "") {
            auth.createUserWithEmailAndPassword(email.toString(), password.toString())
                .addOnSuccessListener {
                    val intent = Intent(this,FeedActivity::class.java)
                    startActivity(intent)
                    finish()
                }.addOnFailureListener {
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(this,"Enter email and password.", Toast.LENGTH_SHORT).show()
        }
    }





}