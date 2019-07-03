package com.example.myapplication.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class User(
    val uid: String,
    var username: String,
    val phone: String,
    var photoImageUrl: String,
    var sipId: String,
    var domain: String,
    var password: String,
    var description: String
) : Parcelable {
    constructor() : this("", "", "", "", "", "", "","")
}