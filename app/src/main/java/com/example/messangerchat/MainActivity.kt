package com.example.messangerchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import com.example.messangerchat.ChatActivity
import com.example.messangerchat.R
import com.example.messangerchat.RegistrationActivity
import com.example.messangerchat.classes.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.usernameRegisterEditText
import java.io.Serializable


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    private val MAIN_LOG = "Main"
    var users = arrayListOf<User>(
        User("Username1", "Qwerty1", "username1@gmail.com"),
        User("Username2", "Qwerty2", "username2@gmail.com"),
        User("Username3", "Qwerty3", "username3@gmail.com"),
        User("Username4", "Qwerty4", "username4@gmail.com"),
        User("Username5", "Qwerty5", "username5@gmail.com"),
        User("Username6", "Qwerty6", "username6@gmail.com"),
        User("Username7", "Qwerty7", "username7@gmail.com")
    )
    var user: User? = null

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
        startActivity(
            Intent(this, RegistrationActivity::class.java)
                .putExtra("DataUsers", users as Serializable)
        )
    }

    fun onClickButtonSignIn(view: View) {
        val username = usernameRegisterEditText.text.toString()
        val password = passwordEditText.text.toString()
        // ---   ---
        FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password)
            .addOnCompleteListener{
                if(!it.isSuccessful) return@addOnCompleteListener // TO DO return messageShow()
                startActivity(Intent(this, ChatActivity::class.java))
                finish()
            }

//        if (User.isUserExist(username, password, users)) {

//            startActivity(Intent(this, ChatActivity::class.java)
//                .putExtra("DataUser", username))
//        } else {
//            usernameRegisterEditText.text = null
//            passwordEditText.text = null
//            messageShow(message_1)
//        }
    }

    private fun messageShow(view: View) {
        view.visibility = View.VISIBLE
        buttonSignIn.isEnabled = false
        buttonSignUp.isEnabled = false
        var count = 0
        object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                count++
                if (count == 3) {
                    message_1.animate().alpha(0.0f).duration = 3500
                }
            }

            override fun onFinish() {
                view.visibility = View.GONE
                view.alpha = 1.0f
                buttonSignIn.isEnabled = true
                buttonSignUp.isEnabled = true
            }
        }.start()
    }


}