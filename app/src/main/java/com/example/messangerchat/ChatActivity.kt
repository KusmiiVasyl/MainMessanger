package com.example.messangerchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        rvUser.layoutManager = LinearLayoutManager(this)
        rvUser.adapter = adapter

        dbRef.child("user").addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (child in snapshot.children) {
                    val currentUser = child.getValue(User::class.java)
                    if(userAuth.currentUser?.uid != currentUser?.uid){
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.logout){
            userAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return true
        }
        return true
    }
}