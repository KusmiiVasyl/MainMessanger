package com.example.messangerchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.usernameRegisterEditText


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
    }

    override fun onStop() {
        usernameRegisterEditText.text = null
        passwordEditText.text = null
        super.onStop()
    }

    fun onClickButtonSignUp(view: View) {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }

    fun onClickButtonSignIn(view: View) {
        val username = usernameRegisterEditText.text.toString()
        val password = passwordEditText.text.toString()

        if (username.isEmpty() || password.isEmpty()) return
        FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) {
                    usernameRegisterEditText.text = null
                    passwordEditText.text = null
                    messageShow(message_1)
                    return@addOnCompleteListener
                }
                startActivity(Intent(this, ChatActivity::class.java))
                finish()
            }
    }

    private fun messageShow(view: View) {
        icon_chat.visibility = View.GONE
        view.visibility = View.VISIBLE
        buttonSignIn.isEnabled = false
        buttonSignUp.isEnabled = false
        var count = 0
        object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                count++
                if (count == 2) {
                    message_1.animate().alpha(0.0f).duration = 2500
                }
            }

            override fun onFinish() {
                view.visibility = View.GONE
                view.alpha = 1.0f
                buttonSignIn.isEnabled = true
                buttonSignUp.isEnabled = true
                icon_chat.visibility = View.VISIBLE
            }
        }.start()
    }
}