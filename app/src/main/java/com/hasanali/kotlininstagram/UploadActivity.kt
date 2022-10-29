package com.hasanali.kotlininstagram

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.hasanali.kotlininstagram.databinding.ActivityUploadBinding
import java.util.*

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionResultLauncher: ActivityResultLauncher<String>
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        val v = binding.root
        setContentView(v)

        registerLaunchers()
        auth = FirebaseAuth.getInstance()
        firestore = Firebase.firestore
        storage = Firebase.storage
    }

    fun upload(v: View) {

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"
        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if(selectedImageUri != null) {
            // upload uri -> storage
            imageReference.putFile(selectedImageUri!!)
                .addOnSuccessListener {
                    //download url from storage
                    imageReference.downloadUrl.addOnSuccessListener {
                        val downloadUrl = it.toString()

                        //upload values to firestore
                        val postMap = hashMapOf<String, Any>()
                        postMap["downloadUrl"] = downloadUrl
                        postMap["userEmail"] = auth.currentUser?.email.toString()
                        postMap["comment"] = binding.editTextComment.text.toString()
                        postMap["date"] = Timestamp.now()

                        firestore.collection("Posts").add(postMap)
                            .addOnSuccessListener {
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(this,it.localizedMessage,Toast.LENGTH_SHORT).show()
                }
        }
    }

    fun selectImage(v: View) {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(v, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give permission", View.OnClickListener {
                        permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }).show()
            } else {
                permissionResultLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intent)
        }
    }

    private fun registerLaunchers() {
        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == RESULT_OK) {
                val intent = it.data
                if(intent != null) {
                    selectedImageUri = intent.data
                    selectedImageUri.let {
                        binding.imageView.setImageURI(selectedImageUri)
                    }
                }
            }
        }
        permissionResultLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if(it) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intent)
            } else {
                Toast.makeText(this,"Permission needed", Toast.LENGTH_SHORT).show()
            }
        }
    }




}