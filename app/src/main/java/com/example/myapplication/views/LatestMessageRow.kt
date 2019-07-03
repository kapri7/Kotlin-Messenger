package com.example.myapplication.views

import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.models.ChatMessage
import com.example.myapplication.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage) : Item<ViewHolder>() {
    var chatPartnerUser: User? = null
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.message_textView_latest_messages.text = chatMessage.text

        val chatPartnerId: String
        if (chatMessage.fromid == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatMessage.toid
        } else {
            chatPartnerId = chatMessage.fromid
        }

        val ref = FirebaseDatabase.getInstance().getReference("users/$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                chatPartnerUser = p0.getValue(User::class.java)
                if (chatPartnerId == FirebaseAuth.getInstance().uid)
                    viewHolder.itemView.usename_textview_latest_message.text = "Saved Messages"
                else
                    viewHolder.itemView.usename_textview_latest_message.text = chatPartnerUser?.username

                val targetImageView = viewHolder.itemView.imageView_latest_message

                if (chatPartnerUser?.photoImageUrl != "default")
                // Picasso.get().load(chatPartnerUser?.photoImageUrl).into(targetImageView)
                    Glide.with(viewHolder.itemView).load(chatPartnerUser?.photoImageUrl).into(targetImageView)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }

    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }
}