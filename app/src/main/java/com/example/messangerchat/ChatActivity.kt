package com.example.messangerchat

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatmessenger.classes.UserAdapter
import com.example.messangerchat.classes.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*


class ChatActivity : AppCompatActivity() {

    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter
    private lateinit var userAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        userList = ArrayList()
        adapter = UserAdapter(this, userList)
        userAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#331f00")))
        dbRef.child("user")
            .child(userAuth.uid.toString())
            .child("username")
            .get().addOnSuccessListener {
                supportActionBar?.title = "Your name: ${it.value}"
            }

        dbRef.child("user")
            .child(userAuth.uid.toString())
            .child("online")
            .setValue(true)

        rvUser.layoutManager = LinearLayoutManager(this)
        rvUser.adapter = adapter

        dbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (child in snapshot.children) {
                    val currentUser = child.getValue(User::class.java)
                    if (userAuth.currentUser?.uid != currentUser?.uid) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onDestroy() {
        dbRef.child("user")
            .child(userAuth.uid.toString())
            .child("online")
            .setValue(false)

        adapter.notifyDataSetChanged()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout) {
            finish()
            return true
        }
        return true
    }

}