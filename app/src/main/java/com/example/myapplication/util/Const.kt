package com.example.myapplication.util

class Const {
    enum class SipRegistration {
        STARTED, FINISHED, ERROR
    }

    enum class SipCall {
        STARTED, ANSWERED, ENDED
    }

    companion object {
        const val KEY_NAME = "key_name"
        const val KEY_STATUS = "key_status"
        const val KEY_ERROR_CODE = "key_error_code"
        const val KEY_IS_INCOMING = "key_is_incoming"

        const val ACTION_INCOMING_CALL = "com.example.myapplication.INCOMING_CALL"
        const val ACTION_DATA_TO_ACTIVITY_EXCHANGE = "com.example.myapplication.DATA_TO_ACTIVITY_EXCHANGE"
        const val ACTION_DATA_TO_SERVICE_EXCHANGE = "com.example.myapplication.DATA_TO_SERVICE_EXCHANGE"

        const val SIP_URL = "195.214.214.253"
        const val SIP_LOGIN = "5955019"
        const val SIP_PASSWORD = "f7SnaRiL"

        const val CALL_TIMEOUT = 30
    }
}