package com.example.myapplication.models

class ChatMessage(val id: String, val text: String, val fromid: String, val toid: String, val timestamp: Long){
    constructor():this("","","","",-1)
}