package com.hasanali.kotlininstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hasanali.kotlininstagram.databinding.ActivityFeedBinding

class FeedActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var postArrayList: ArrayList<Post>
    private lateinit var adapter: Adapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedBinding.inflate(layoutInflater)
        val v = binding.root
        setContentView(v)

        postArrayList = arrayListOf()

        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        getData()

        // recycler
        adapter = Adapter(postArrayList)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun getData() {

        firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->
            if(error != null) {
                Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG).show()
            } else {
                // value = QuerySnapshot
                if(value != null && !(value.isEmpty)) {
                    //value.documents document listesi döndürür
                    val documents = value.documents
                    postArrayList.clear()
                    for(document in documents) {
                        val comment = document["comment"] as String
                        val date = document["date"] as Timestamp
                        val userEmail = document["userEmail"] as String
                        val url = document["downloadUrl"] as String

                        val post = Post(userEmail, comment, url)
                        postArrayList.add(post)
                    }
                    adapter.notifyDataSetChanged()
                }
            }
        }
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.insta_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.add_post) {
            val intent = Intent(this,UploadActivity::class.java)
            startActivity(intent)
        } else {
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}