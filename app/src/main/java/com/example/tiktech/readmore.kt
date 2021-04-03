package com.example.tiktech

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import coil.load
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class readmore : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_readmore)
        val ambil = intent
        val database : DatabaseReference = FirebaseDatabase.getInstance().getReference()
        val storage = Firebase.storage("gs://tiktech-cb01d.appspot.com").reference
        findViewById<ImageView>(R.id.foto_readmore).load(ambil.getStringExtra("foto"))
        Log.d(ContentValues.TAG,ambil.getStringExtra("username").toString())
        database.child("data${ambil.getStringExtra("usernamepost")}").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                findViewById<TextView>(R.id.usernameprofile_readmore).setText(snapshot.child("user").value.toString())
                findViewById<TextView>(R.id.namaprofile_readmore).setText(snapshot.child("name").value.toString())
                if (snapshot.child("profile").value.toString().equals("true")){
                    storage.child("profile${ambil.getStringExtra("usernamepost")}").downloadUrl.addOnSuccessListener(object :
                        OnSuccessListener<Uri> {
                        override fun onSuccess(p0: Uri?) {
                            Glide.with(this@readmore).load(p0).into(findViewById(R.id.fotoprofile_readmore))
                        }
                    })
                }
                database.child("data${ambil.getStringExtra("usernamepost")}").child("activity").child("post")
                    .child(ambil.getStringExtra("postid").toString()).addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            findViewById<TextView>(R.id.teks_readmore).setText(snapshot.child("text").value.toString())
                            if (snapshot.hasChild("likes")){
                                findViewById<TextView>(R.id.number_like).setText(snapshot.child("likes").childrenCount.toString())
                            }
                            else{
                                findViewById<TextView>(R.id.number_like).setText("0")
                            }
                        }

                    })
            }
        })
        database.child("data${ambil.getStringExtra("username")}").child("activity").addListenerForSingleValueEvent(object :
        ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild("liked")){
                    if (snapshot.child("liked").hasChild(ambil.getStringExtra("usernamepost").toString())){
                        if (snapshot.child("liked").child(ambil.getStringExtra("usernamepost").toString())
                                        .hasChild(ambil.getStringExtra("postid").toString())){
                            findViewById<ImageView>(R.id.like).visibility = View.INVISIBLE
                            findViewById<ImageView>(R.id.liked).visibility = View.VISIBLE
                        }
                        else{
                            findViewById<ImageView>(R.id.liked).visibility = View.INVISIBLE
                            findViewById<ImageView>(R.id.like).visibility = View.VISIBLE
                        }
                    }
                }
            }
        })
        findViewById<ImageView>(R.id.like).setOnClickListener {
            var hashMaplikes = HashMap<String, Any>()
            hashMaplikes.put("value","true")
            var hashMapliked = HashMap<String, Any>()
            hashMapliked.put("value","true")
            findViewById<ImageView>(R.id.like).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.liked).visibility = View.VISIBLE
            database.child("data${ambil.getStringExtra("username")}").child("activity")
                    .child("liked").child(ambil.getStringExtra("usernamepost").toString())
                    .child(ambil.getStringExtra("postid").toString()).setValue(hashMapliked)
            database.child("data${ambil.getStringExtra("usernamepost")}").child("activity")
                    .child("post").child(ambil.getStringExtra("postid").toString()).child("likes")
                    .child(ambil.getStringExtra("username").toString()).setValue(hashMaplikes)
        }
        findViewById<ImageView>(R.id.liked).setOnClickListener {
            findViewById<ImageView>(R.id.liked).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.like).visibility = View.VISIBLE
            database.child("data${ambil.getStringExtra("username")}").child("activity")
                    .child("liked").child(ambil.getStringExtra("usernamepost").toString())
                    .child(ambil.getStringExtra("postid").toString()).removeValue()
            database.child("data${ambil.getStringExtra("usernamepost")}").child("activity")
                    .child("post").child(ambil.getStringExtra("postid").toString())
                    .child("likes").child(ambil.getStringExtra("username").toString()).removeValue()
        }
        findViewById<ImageView>(R.id.imagenotif_readmore).setOnClickListener {
            val pindah = Intent(this,notification::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        findViewById<ImageView>(R.id.imageupload_readmore).setOnClickListener {
            val pindah = Intent(this,Sharedpost::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        findViewById<ImageView>(R.id.imagehome_readmore).setOnClickListener {
            val pindah = Intent(this,Home::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        findViewById<ImageView>(R.id.imageprofile_readmore).setOnClickListener {
            val pindah = Intent(this,profile::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
    }
}