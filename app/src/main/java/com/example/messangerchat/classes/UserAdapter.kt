package com.example.chatmessenger.classes

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.utils.widget.ImageFilterView
import androidx.recyclerview.widget.RecyclerView
import com.example.messangerchat.CorrespondenceActivity
import com.example.messangerchat.R
import com.example.messangerchat.classes.User
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_registration.*
import java.net.URL
import java.util.concurrent.Executors
import java.util.logging.Handler


class UserAdapter(val context: Context, val userList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]

        if(currentUser.profilePhotoUrl != ""){
            Picasso.get().load(currentUser.profilePhotoUrl).into(holder.urlPhotoUser)
        }
        if(currentUser.online) holder.markOnline.setBackgroundResource(R.drawable.mark_online)
        if(!currentUser.online) holder.markOnline.setBackgroundResource(R.drawable.mark_ofline)
        holder.textName.text = currentUser.username

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CorrespondenceActivity::class.java)

            intent.putExtra("username", currentUser.username)
            intent.putExtra("uid", currentUser.uid)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val urlPhotoUser = itemView.findViewById<CircleImageView>(R.id.photoUser)
        val textName = itemView.findViewById<TextView>(R.id.tvUsername)
        val markOnline = itemView.findViewById<ImageView>(R.id.markOnline)
    }
}