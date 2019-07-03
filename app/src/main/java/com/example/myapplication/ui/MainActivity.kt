package com.example.myapplication.ui

import android.content.pm.PackageManager
import android.Manifest
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.example.myapplication.R
import com.example.myapplication.events.CallEvent
import com.example.myapplication.call.CallService
import com.example.myapplication.call.DataReceiver
import com.example.myapplication.messages.NewMessageActivity
import com.example.myapplication.util.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe


class MainActivity : AppCompatActivity() {
    private var mService: CallService? = null
    private var mReceiver: DataReceiver? = null
    private val mConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mService = (service as CallService.CallBinder).getService()
        }

        override fun onServiceDisconnected(name: ComponentName?) {}
    }

    companion object {
        private const val PERMISSIONS_BEFORE_REGISTER: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        EventBus.getDefault().register(this)
       // setContentView(R.layout.activity_call)

        checkCallPermissions()



    }


    override fun onDestroy() {
        super.onDestroy()
        if (mService != null) {
            unbindService(mConnection)
        }

        EventBus.getDefault().unregister(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == PERMISSIONS_BEFORE_REGISTER) {
            var isPermissionGranted = true
            if (grantResults.isNotEmpty()) {
                for (result in grantResults) {
                    isPermissionGranted = isPermissionGranted && (result == PackageManager.PERMISSION_GRANTED)
                }
            } else {
                isPermissionGranted = false
            }

            tryToRegister(isPermissionGranted)
        }
    }

    // This method will get registration status from the service.
    // Also you need to unregister the receiver here because it isn't needed anymore.
    fun receiveRegisterState(status: Const.SipRegistration, errorCode: Int) {
        when (status) {
            Const.SipRegistration.STARTED -> toast(getString(R.string.sip_registration_started))
            Const.SipRegistration.FINISHED -> {
                unregisterReceiver()
                toast(getString(R.string.sip_registration_finished, Const.SIP_LOGIN))
                makeCall()
            }
            Const.SipRegistration.ERROR -> {
                unregisterReceiver()
                toast(getString(R.string.sip_registration_error, errorCode))
            }
        }
    }

    @Subscribe // Catches event of incoming call.
    // Then tries to take call and show call activity.
    fun onIncomingCallEvent(event: CallEvent) {
        val nameText = mService?.takeAudioCall(event.intent)
        val isStarted = nameText?.isNotEmpty() ?: false

        if (isStarted) {
            CallActivity.create(this, nameText!!, true)
        } else {
            toast(getString(R.string.sip_call_error))
        }
    }

    private fun checkCallPermissions() {
        val permissions = ArrayList<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.USE_SIP) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.USE_SIP)
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissions.add(Manifest.permission.RECORD_AUDIO)
        }

        if (permissions.size > 0) {
            ActivityCompat.requestPermissions(
                this, permissions.toTypedArray(), PERMISSIONS_BEFORE_REGISTER
            )
        } else {
            tryToRegister(true)
        }
    }

    // Register user if all permissions was granted.
    // We'll do it via service so let's bind it and register the receiver.
    private fun tryToRegister(isPermissionGranted: Boolean) {
        if (isPermissionGranted) {
            val filter = IntentFilter(Const.ACTION_DATA_TO_ACTIVITY_EXCHANGE)

            unregisterReceiver()
            mReceiver = DataReceiver()
            registerReceiver(mReceiver, filter)
            bindService(Intent(this, CallService::class.java), mConnection, Context.BIND_AUTO_CREATE)
        } else {
            finish()
        }
    }

    private fun unregisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver)
            mReceiver = null
        }
    }

    // Tries to make call and show call activity.
    private fun makeCall() {
        val nameText = intent.getStringExtra(NewMessageActivity.USER_KEY)
        Log.d("SIP", "${intent.getStringExtra(NewMessageActivity.USER_KEY)}")
        val sipAddress = "sip:$nameText@${Const.SIP_URL}"
        val isStarted = mService?.makeAudioCall(sipAddress) ?: false

        if (isStarted) {
            CallActivity.create(this, nameText, false)
        } else {
            toast(getString(R.string.sip_call_error))
        }
    }
}
