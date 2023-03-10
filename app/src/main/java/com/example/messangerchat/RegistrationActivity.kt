package com.example.messangerchat

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.MediaStore
import android.view.View
import com.example.messangerchat.classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.usernameRegisterEditText
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
class RegistrationActivity : AppCompatActivity() {
    private lateinit var dbRef: DatabaseReference
    private lateinit var users: ArrayList<User>
    private val REGISTER_LOG = "Registration"
    var selectPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.hide()

        dbRef = FirebaseDatabase.getInstance().reference
        users = ArrayList()

        dbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val currentUser = child.getValue(User::class.java)
                    users.add(currentUser!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })

        // for focus (change background fields)
        usernameRegisterEditText.setOnFocusChangeListener { _, _ ->
            usernameRegisterInputLayout.setBoxBackgroundColorResource(R.color.light_grey)
        }
        passwordRegisterEditText.setOnFocusChangeListener { _, _ ->
            passwordRegisterInputLayout.setBoxBackgroundColorResource(R.color.light_grey)
        }
        passwordRegisterConfirmEditText.setOnFocusChangeListener { _, _ ->
            passwordRegisterConfirmInputLayout.setBoxBackgroundColorResource(R.color.light_grey)
        }
        emailRegisterConfirmEditText.setOnFocusChangeListener { _, _ ->
            emailRegisterInputLayout.setBoxBackgroundColorResource(R.color.light_grey)
        }
    }

    fun onClickButtonRegister(view: View) {
        val username = usernameRegisterEditText.text.toString()
        val password = passwordRegisterEditText.text.toString()
        val confirmPassword = passwordRegisterConfirmEditText.text.toString()
        val email = emailRegisterConfirmEditText.text.toString()

        if (checkFieldsOnNullOrEmpty()) return
        val user = User(username, password, email)
        when (User.registerUser(user, confirmPassword, users)) {
            1 -> {
                clearFields()
                showErrorMessage(getString(R.string.textMessage5))
            }
            2 -> {
                clearFields()
                showErrorMessage(getString(R.string.textMessage2))
            }
            3 -> {
                clearFields()
                showErrorMessage(getString(R.string.textMessage3))
            }
            4 -> {
                clearFields()
                showErrorMessage(getString(R.string.textMessage4))
            }
            5 -> {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (!it.isSuccessful) return@addOnCompleteListener
                        addUserToDatabase(
                            it.result.user!!.uid,
                            username,
                            password,
                            email
                        )
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
            }
        }
    }

    fun onClickButtonSelectPhoto(view: View) {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }

    // for select photo startActivityForResult
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            // process if we choose photo
            selectPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            btn_select_photo.setImageDrawable(null)
            btn_select_photo.setBackgroundDrawable(bitmapDrawable)
            data.data = null
        }
    }

    private fun addUserToDatabase(
        uid: String,
        username: String,
        password: String,
        email: String
    ) {
        if (selectPhotoUri == null) {
            dbRef.child("user")
                .child(uid)
                .setValue(User(uid, username, password, email, ""))
            return
        }
        val filename = UUID.randomUUID().toString()
        val refStorage = FirebaseStorage.getInstance().getReference("/images/$filename")
        refStorage.putFile(selectPhotoUri!!)
            .addOnSuccessListener {
                refStorage.downloadUrl.addOnSuccessListener {
                    dbRef.child("user").child(uid)
                        .setValue(User(uid, username, password, email, it.toString()))
                }
            }
    }

    private fun checkFieldsOnNullOrEmpty(): Boolean {
        var isFieldNullOrEmpty = false
        if (usernameRegisterEditText.text.isNullOrEmpty()) {
            usernameRegisterInputLayout.setBoxBackgroundColorResource(R.color.light_red)
            isFieldNullOrEmpty = true
        }
        if (passwordRegisterEditText.text.isNullOrEmpty()) {
            passwordRegisterInputLayout.setBoxBackgroundColorResource(R.color.light_red)
            isFieldNullOrEmpty = true
        }
        if (passwordRegisterConfirmEditText.text.isNullOrEmpty()) {
            passwordRegisterConfirmInputLayout.setBoxBackgroundColorResource(R.color.light_red)
            isFieldNullOrEmpty = true
        }
        if (emailRegisterConfirmEditText.text.isNullOrEmpty()) {
            emailRegisterInputLayout.setBoxBackgroundColorResource(R.color.light_red)
            isFieldNullOrEmpty = true
        }
        return isFieldNullOrEmpty
    }

    private fun clearFields() {
        usernameRegisterEditText.text = null
        passwordRegisterEditText.text = null
        passwordRegisterConfirmEditText.text = null
        emailRegisterConfirmEditText.text = null
    }

    private fun showErrorMessage(message: String) {
        btn_select_photo.visibility = View.GONE
        buttonRegister.isEnabled = false
        message_2.text = message
        message_2.visibility = View.VISIBLE
        var count = 0
        object : CountDownTimer(4000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                count++
                if (count == 3) {
                    message_2.animate().alpha(0.0f).duration = 1000
                }
            }

            override fun onFinish() {
                message_2.visibility = View.GONE
                message_2.alpha = 1.0f
                btn_select_photo.visibility = View.VISIBLE
                buttonRegister.isEnabled = true
            }
        }.start()
    }
}