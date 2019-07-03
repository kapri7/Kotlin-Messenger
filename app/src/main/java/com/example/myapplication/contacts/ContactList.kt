package com.example.myapplication.contacts

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.myapplication.R
import com.example.myapplication.messages.UserItem
import com.example.myapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*

class ContactList : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title = "Contacts"

        fetchUsers()
    }

    companion object {
        val UID = "UID"
        val USER_KEY = "USER_KEY"
        val PHOTO_URL = "PHOTO_URL"
        val PHONE = "PHONE"
        val DESCRIPTION = "DESCRIPTION"
        val SIPID= "SIPID"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()
                p0.children.forEach {
                    Log.d("NewMessage", it.toString())
                    val user = it.getValue(User::class.java)
                    if (user != null && user.uid!=FirebaseAuth.getInstance().uid) {
                        adapter.add(UserItem(user))
                    }
                }
                adapter.setOnItemClickListener { item, view ->

                    val userItem = item as UserItem
                    val intent = Intent(view.context, ContactActivity::class.java)
                    intent.putExtra(USER_KEY, userItem.user.username)
                    intent.putExtra(UID, userItem.user.uid)
                    intent.putExtra(PHOTO_URL, userItem.user.photoImageUrl)
                    intent.putExtra(PHONE, userItem.user.phone)
                    intent.putExtra(DESCRIPTION, userItem.user.description)
                    intent.putExtra(SIPID, userItem.user.sipId)
                    startActivity(intent)
                }
                recyclerview_newmessage.adapter = adapter
                recyclerview_newmessage.addItemDecoration(
                    DividerItemDecoration(
                        this@ContactList,
                        DividerItemDecoration.VERTICAL
                    )
                )
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}