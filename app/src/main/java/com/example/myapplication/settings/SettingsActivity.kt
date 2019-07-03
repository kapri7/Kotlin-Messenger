package com.example.myapplication.settings

import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.contacts.ContactList
import com.example.myapplication.messages.NavigationActivity
import com.example.myapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        supportActionBar?.title = "Settings"

        fetchCurrentUser()
    }
    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                NavigationActivity.currentUser = p0.getValue(User::class.java)
                Log.d("Settings", "Current user ${NavigationActivity.currentUser?.username}")
                settings_name.text = NavigationActivity.currentUser?.username.toString()
                settings_number.text = NavigationActivity.currentUser?.phone.toString()
                settings_description.text=NavigationActivity.currentUser?.description.toString()
                settings_sipID.text=NavigationActivity.currentUser?.sipId.toString()
                settings_domain.text=NavigationActivity.currentUser?.domain.toString()
                settings_password.text=NavigationActivity.currentUser?.password.toString()
                val drawable: Drawable? = getDrawable(R.drawable.contact)
                settings_photo.setImageDrawable(drawable)
                if (NavigationActivity.currentUser?.photoImageUrl != "default")
                  //  Picasso.get().load(NavigationActivity.currentUser?.photoImageUrl).into(settings_photo)
                    Glide.with(this@SettingsActivity).load(NavigationActivity.currentUser?.photoImageUrl).into(settings_photo)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_edit, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.button_edit) {
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }


}
