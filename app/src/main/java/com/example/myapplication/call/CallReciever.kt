package com.example.myapplication.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.example.myapplication.events.CallEvent

import org.greenrobot.eventbus.EventBus



class CallReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }

        EventBus.getDefault().post(CallEvent(intent))
    }
}