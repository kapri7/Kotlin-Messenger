package com.example.myapplication.views

import com.bumptech.glide.Glide
import com.example.myapplication.R
import com.example.myapplication.models.User
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatFromItem(val text: String,val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text

        //load image
        val uri = user.photoImageUrl
        val targetImageView = viewHolder.itemView.imageView_chat_from_row
        //Picasso.get().load(uri).into(targetImageView)
        Glide.with(viewHolder.itemView).load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

class ChatToItem(val text: String, val user: User) : Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text

        //load image
        val uri = user.photoImageUrl
        val targetImageView = viewHolder.itemView.imageView_chat_to_row
        //Picasso.get().load(uri).into(targetImageView)
        Glide.with(viewHolder.itemView).load(uri).into(targetImageView)
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}