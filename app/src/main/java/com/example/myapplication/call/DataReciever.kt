package com.example.myapplication.call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

import com.example.myapplication.ui.CallActivity
import com.example.myapplication.ui.MainActivity
import com.example.myapplication.util.Const


class DataReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }

        val hasErrorCode = intent.extras?.containsKey(Const.KEY_ERROR_CODE) ?: false
        val hasStatus = intent.extras?.containsKey(Const.KEY_STATUS) ?: false
        val isRegistration = hasStatus && hasErrorCode
        val isCall = hasStatus && !hasErrorCode

        if (context is MainActivity && isRegistration) {
            val status = intent.getSerializableExtra(Const.KEY_STATUS) as Const.SipRegistration
            val errorCode = intent.getIntExtra(Const.KEY_ERROR_CODE, 0)
            context.receiveRegisterState(status, errorCode)
        } else if (isCall) {
            val status = intent.getSerializableExtra(Const.KEY_STATUS) as Const.SipCall

            if (context is CallActivity) {
                context.updateCallStatus(status)
            } else if (context is CallService) {
                context.doAction(status)
            }
        }
    }
}