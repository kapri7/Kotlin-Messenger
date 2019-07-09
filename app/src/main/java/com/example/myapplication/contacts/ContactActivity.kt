package com.example.myapplication.contacts


import android.app.Dialog
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_contact.*
import android.util.Log
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.messages.ChatLogActivity
import com.example.myapplication.messages.NewMessageActivity
import com.example.myapplication.models.User
import com.example.myapplication.ui.MainActivity


class ContactActivity : AppCompatActivity() {

    private val TAG = "ContactActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact)
        supportActionBar?.title = "Contact info"

        getIntentData()



        call_contact_button.setOnClickListener {
                val user: User? = User(
                    intent.getStringExtra(ContactList.UID),
                    intent.getStringExtra(ContactList.USER_KEY),
                    "",
                    intent.getStringExtra(ContactList.PHOTO_URL),
                    intent.getStringExtra(ContactList.SIPID),
                    "",
                    "",
                    ""
                )

                intent = Intent(this, MainActivity::class.java)
                intent.putExtra(NewMessageActivity.USER_KEY, user?.sipId)
                startActivity(intent)
                finish()
        }

    }


    private fun getIntentData() {
        val drawable: Drawable? = getDrawable(R.drawable.contact)
        big_contact_photo.setImageDrawable(drawable)

        if (intent.getStringExtra(ContactList.PHOTO_URL) != "default")
           // Picasso.get().load(intent.getStringExtra(ContactList.PHOTO_URL)).into(big_contact_photo)
            Glide.with(this).load(intent.getStringExtra(ContactList.PHOTO_URL)).into(big_contact_photo)

        big_contact_name.text = intent.getStringExtra(ContactList.USER_KEY)
        big_contact_number.text = intent.getStringExtra(ContactList.PHONE)
        contact_description.text = intent.getStringExtra(ContactList.DESCRIPTION)
    }


}
