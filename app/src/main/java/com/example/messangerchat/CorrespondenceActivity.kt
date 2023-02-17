package com.example.messangerchat

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messangerchat.classes.Message
import com.example.messangerchat.classes.MessageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_correspondence.*


class CorrespondenceActivity : AppCompatActivity() {

    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var dbRef: DatabaseReference

    var sentStorage: String? = null
    var receiverStorage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_correspondence)

        dbRef = FirebaseDatabase.getInstance().reference
        val username = intent.getStringExtra("username")
        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser?.uid

        sentStorage = receiverUid + senderUid
        receiverStorage = senderUid + receiverUid

        supportActionBar?.title = username
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#331f00")))

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

        recyclerViewChat.layoutManager = LinearLayoutManager(this)
        recyclerViewChat.adapter = messageAdapter

        //logic for adding data to RecycleView
        dbRef.child("chats").child(sentStorage!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()
                    for (child in snapshot.children) {
                        val message = child.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    recyclerViewChat.scrollToPosition(messageList.size - 1) //TO DO
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}

            })

        // add messages to Firebase database
        imageViewSent.setOnClickListener {
            if(editTextMessageBox.text.toString() == "") return@setOnClickListener
            dbRef.child("chats")
                .child(sentStorage!!)
                .child("messages").push()
                .setValue(Message(editTextMessageBox.text.toString(), senderUid))
                .addOnSuccessListener {
                    dbRef.child("chats")
                        .child(receiverStorage!!)
                        .child("messages").push()
                        .setValue(Message(editTextMessageBox.text.toString(), senderUid))
                    editTextMessageBox.setText("")
                }
        }
    }
}