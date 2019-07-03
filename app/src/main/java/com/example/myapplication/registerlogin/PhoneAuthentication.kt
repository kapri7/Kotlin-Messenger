package com.example.myapplication.registerlogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.messages.NavigationActivity
import com.example.myapplication.R
import com.example.myapplication.models.User
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_phone_authentication.*
import java.util.concurrent.TimeUnit


class PhoneAuthentication : AppCompatActivity() {

    lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    lateinit var mAuth: FirebaseAuth
    var verificationId = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_authentication)
        mAuth = FirebaseAuth.getInstance()
        veriBtn.setOnClickListener { view: View? ->
            progress.visibility = View.VISIBLE
            verify()
        }
        authBtn.setOnClickListener { view: View? ->
            progress.visibility = View.VISIBLE
            authenticate()
        }
    }


    private fun verificationCallbacks() {
        mCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                progress.visibility = View.INVISIBLE
                signIn(credential)
            }

            override fun onVerificationFailed(p0: FirebaseException?) {
                Toast.makeText(this@PhoneAuthentication, "${p0?.message}", Toast.LENGTH_SHORT).show()
                Log.d("Error", "${p0?.message}")
                progress.visibility = View.INVISIBLE
            }

            override fun onCodeSent(verfication: String?, p1: PhoneAuthProvider.ForceResendingToken?) {
                super.onCodeSent(verfication, p1)
                verificationId = verfication.toString()
                progress.visibility = View.INVISIBLE
            }

        }
    }

    private fun verify() {

        verificationCallbacks()

        val phnNo = "+38" + phnNoTxt.text.toString()

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
            phnNo,
            60,
            TimeUnit.SECONDS,
            this,
            mCallbacks
        )
    }

    private fun signIn(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener { task: Task<AuthResult> ->
                if (task.isSuccessful) {
                    toast("Logged in Successfully :)")
                    val uid = FirebaseAuth.getInstance().uid ?: ""
                    val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
                    val user = User(
                        uid,
                        "default",
                        phnNoTxt.text.toString(),
                        "default", "", "", "", ""
                    )
                    ref.addListenerForSingleValueEvent(object : ValueEventListener {

                        override fun onCancelled(p0: DatabaseError) {

                        }

                        override fun onDataChange(p0: DataSnapshot) {
                            val currentUser = p0.getValue(User::class.java)
                            if (currentUser == null) {
                                Log.d("Error", "New!")
                                ref.setValue(user)
                                    .addOnSuccessListener {
                                        Log.d("RegisterActivity", "Finally we saved the user to Firebase Database")
                                    }
                                    .addOnFailureListener {
                                        Log.d(
                                            "RegisterActivity",
                                            "We didn`t save the user to Firebase Database${it.message}"
                                        )
                                    }
                            } else Log.d("Error", " Not New!")
                        }
                    })

                    startActivity(Intent(this, NavigationActivity::class.java))
                    finish()
                }
            }
            .addOnFailureListener { progress.visibility = View.INVISIBLE }
    }

    private fun authenticate() {

        val verifiNo = verifiTxt.text.toString()
        if (verifiNo == "") {
            progress.visibility = View.INVISIBLE
            return
        }
        try {
            val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, verifiNo)
            signIn(credential)
        } catch (ee: Exception) {
            Log.d("Error", "${ee.message}")
            progress.visibility = View.INVISIBLE
        }


    }

    private fun toast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
    }


}