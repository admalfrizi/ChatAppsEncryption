package com.aplikasi.chatappstrials.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.aplikasi.chatappstrials.databinding.CustomPopupGalleryBinding
import com.aplikasi.chatappstrials.databinding.EditDataBinding
import com.aplikasi.chatappstrials.utils.Constants
import com.aplikasi.chatappstrials.utils.FirebaseNotifService
import com.aplikasi.chatappstrials.utils.PhotoPickerAvailabilityChecker
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.util.*

class EditData : AppCompatActivity() {

    private lateinit var binding: EditDataBinding
    private lateinit var mDbRef: DatabaseReference
    private lateinit var storef: StorageReference
    private var ImageUri : Uri? = null
    private lateinit var requestImagePermission: ActivityResultLauncher<PickVisualMediaRequest>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EditDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        mDbRef = FirebaseDatabase.getInstance(Constants.FIREBASE_DB_URL).reference
        val uidRef = mDbRef.child("users").child(uid)
        storef = FirebaseStorage.getInstance().reference

        uidRef.get().addOnCompleteListener { task ->
            if(task.isSuccessful){
                val snapshot = task.result
                val name = snapshot?.child("name")?.getValue(String::class.java)
                val email = snapshot?.child("email")?.getValue(String::class.java)
                val img = snapshot?.child("img_profile")?.getValue(String::class.java)

                Glide.with(this).load(img).into(binding.imageProfile)
                binding.userEdt.setText(name)
                binding.emailEdt.setText(email)

            } else {
                Log.d("TAG", task.exception!!.message!!)
            }
        }

        requestImagePermission = registerForActivityResult(ActivityResultContracts.PickVisualMedia()){ uri ->
            ImageUri = uri
        }

        binding.btnBack.setOnClickListener {
            super.onBackPressed()
        }

        binding.btnChange.setOnClickListener {
            updateData()
        }

        binding.imageProfile.setOnClickListener {

            selectImage()
        }

        binding.updateImgBtn.setOnClickListener {
            updateImage()
        }
    }

    private fun updateImage() {
        binding.ld.visibility = View.VISIBLE
        val id = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = storef.child("images/$id")

        if(ImageUri == null) {
            binding.ld.visibility = View.GONE
            Toast.makeText(this, "Gambar Anda Kosong", Toast.LENGTH_SHORT).show()
        } else {
            ref.putFile(ImageUri!!).continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val uri = task.result
                    addImageToDb(uri.toString())
                    binding.ld.visibility = View.GONE
                    Toast.makeText(this, "Update Gambar Profile Berhasil", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener{ exception ->
                Log.d("TAG", "Error : $exception")
            }
        }

    }

    private fun selectImage() {

        requestImagePermission.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
           val pickPhoto = Intent(
                if (PhotoPickerAvailabilityChecker.isPhotoPickerAvailable()){
                    Intent(MediaStore.ACTION_PICK_IMAGES)
                } else {
                    Intent(Intent.ACTION_GET_CONTENT)
                }
            ).apply {
                type = "image/*"
            }
            startActivityForResult(Intent.createChooser(pickPhoto, "Select Image"), 100)

        } else {
            val intent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
                putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
            }

            startActivityForResult(Intent.createChooser(intent, "Select Image"), 100)
        }
    }

    private fun updateData() {
        binding.ld.visibility = View.VISIBLE

        val id = FirebaseAuth.getInstance().currentUser!!.uid
        val name = binding.userEdt.text.toString()
        val email = binding.emailEdt.text.toString()


        val editData = mapOf(
            "name" to name,
            "email" to email,
        )

        binding.ld.visibility = View.GONE
        mDbRef.child("users").child(id).updateChildren(editData)
        FirebaseAuth.getInstance().currentUser?.updateEmail(email)
        Toast.makeText(this, "Update Data Berhasil", Toast.LENGTH_SHORT).show()
        super.onBackPressed()
    }

    private fun addImageToDb(uri: String) {
        val id = FirebaseAuth.getInstance().currentUser!!.uid
        val addImg = mapOf(
            "img_profile" to uri,
        )
        mDbRef.child("users").child(id).updateChildren(addImg)
    }


    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100 && resultCode == RESULT_OK){
            ImageUri = data?.data!!
            Glide.with(this).load(ImageUri).into(binding.imageProfile)
        }
    }
}