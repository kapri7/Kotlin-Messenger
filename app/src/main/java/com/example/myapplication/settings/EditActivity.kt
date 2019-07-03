package com.example.myapplication.settings

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.messages.NavigationActivity
import com.example.myapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class EditActivity : AppCompatActivity() {

    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        supportActionBar?.title = "Edit"

        fetchCurrentUser()
        select_image_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }

    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                NavigationActivity.currentUser = p0.getValue(User::class.java)
                Log.d("Settings", "Current user ${NavigationActivity.currentUser?.username}")
                enter_name.setText(NavigationActivity.currentUser?.username.toString())
                enter_description.setText(NavigationActivity.currentUser?.description.toString())
                enter_id.setText(NavigationActivity.currentUser?.sipId.toString())
                enter_domain.setText(NavigationActivity.currentUser?.domain.toString())
                enter_password.setText(NavigationActivity.currentUser?.password.toString())
                val drawable: Drawable? = getDrawable(R.drawable.contact)
                selected_image_view.setImageDrawable(drawable)
                if (NavigationActivity.currentUser?.photoImageUrl != "default")
                //Picasso.get().load(NavigationActivity.currentUser?.photoImageUrl).into(selected_image_view)
                    Glide.with(this@EditActivity).load(NavigationActivity.currentUser?.photoImageUrl).into(
                        selected_image_view
                    )
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            //proceed and check what the selected image was...
            Log.d("RegisterActivity", "Photo was selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            selected_image_view.setImageBitmap(bitmap)

            select_image_button.alpha = 0f
            uploadImageToFirebaseStorage()
        }
    }

    var photoUrl: String? = ""

    private fun uploadImageToFirebaseStorage() {
        if (selectedPhotoUri == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Successfully uploaded image: ${it.metadata?.path}")

                ref.downloadUrl.addOnSuccessListener {
                    Log.d("RegisterActivity", "FileLocation: $it")
                    //changeCurrentUser(it.toString())

                    photoUrl = it.toString()
                }
            }
            .addOnFailureListener {
                //do some logging here
            }

    }

    private fun changeCurrentUser() {//todo:error with changing photo
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        if (photoUrl != "") {
            ref.child("photoImageUrl").setValue(photoUrl)
            Log.d("Photo", "${photoUrl.toString()}")
        }
        var tmp = enter_name.text.toString()
        if (tmp != "")
            ref.child("username").setValue(tmp)
        tmp = enter_id.text.toString()
        if (tmp != "")
            ref.child("sipId").setValue(tmp)
        tmp = enter_domain.text.toString()
        if (tmp != "")
            ref.child("domain").setValue(tmp)
        tmp = enter_description.text.toString()

        if (tmp != "")
            ref.child("description").setValue(tmp)
        tmp = enter_password.text.toString()
        if (tmp != "")
            ref.child("password").setValue(tmp)
        intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
        finish()

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.button_save -> {
                changeCurrentUser()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_save, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
