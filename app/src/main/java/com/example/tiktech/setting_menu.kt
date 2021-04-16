package com.example.tiktech

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class setting_menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        val ambil = intent
        val database : DatabaseReference = FirebaseDatabase.getInstance().getReference()
        val storage = Firebase.storage("gs://tiktech-cb01d.appspot.com").reference
        animation(R.id.header_set)
        animation(R.id.judul_set)
        animation(R.id.backarrow_set)
        findViewById<ImageView>(R.id.backarrow_set).setOnClickListener {
            val pindah = Intent(this,profile::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            Log.d(ContentValues.TAG,storage.child("post${ambil.getStringExtra("username")}").path);
            finish()
        }
        findViewById<Button>(R.id.button_logout).setOnClickListener {
            val pindah = Intent(this,Login_activty::class.java)
            startActivity(pindah)
            finish()
        }
        findViewById<Button>(R.id.button_delete).setOnClickListener {
            database.child("data${ambil.getStringExtra("username")}").child("activity").addListenerForSingleValueEvent(object :
                    ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild("post")){
                        for (child in snapshot.child("post").children){
                            var childname = child.key.toString()
                            if (snapshot.child("post").child(childname).child("foto").value.toString().equals("true")){
                                storage.child("post${ambil.getStringExtra("username")}/${childname}").delete()
                            }
                        }
                    }
                }

            })
            database.child("data${ambil.getStringExtra("username")}").removeValue()
            storage.child("profile${ambil.getStringExtra("username")}").delete()
            database.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    for (child in snapshot.children){
                        var childuser = child.key.toString()
                        if (snapshot.child(childuser).child("activity").hasChild("liked")){
                            if (snapshot.child(childuser).child("activity").child("liked")
                                            .hasChild(ambil.getStringExtra("username").toString())){
                                database.child(childuser).child("activity").child("liked")
                                        .child(ambil.getStringExtra("username").toString()).removeValue()
                            }
                        }
                        if (snapshot.child(childuser).child("activity").hasChild("post")){
                            database.child(childuser).child("activity").child("post").addListenerForSingleValueEvent(object :
                            ValueEventListener{
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (childpost in snapshot.children){
                                        var childpostname = childpost.key.toString()
                                        if (snapshot.child(childpostname).hasChild("likes")){
                                            if (snapshot.child(childpostname).child("likes").hasChild(ambil.getStringExtra("username").toString())){
                                                database.child(childuser).child("activity").child("post").child(childpostname)
                                                        .child("likes").child(ambil.getStringExtra("username").toString()).removeValue()
                                            }
                                        }
                                        if (snapshot.child(childpostname).hasChild("constribute")){
                                            if (snapshot.child(childpostname).child("constribute").hasChild(ambil.getStringExtra("username").toString())){
                                                database.child(childuser).child("activity").child("post").child(childpostname)
                                                        .child("constribute").child(ambil.getStringExtra("username").toString()).removeValue()
                                            }
                                        }
                                    }
                                }

                            })
                        }
                    }
                }

            })
            val pindah = Intent(this,Login_activty::class.java)
            startActivity(pindah)
            finish()
        }
    }
    fun animation(x: Int){
        val anim1 : ObjectAnimator = ObjectAnimator.ofFloat(findViewById(x), View.ALPHA,0f,1f)
        val anim2 : ObjectAnimator = ObjectAnimator.ofFloat(findViewById(x), View.TRANSLATION_Y,-200f,0f)
        val anim : AnimatorSet = AnimatorSet()
        anim.playTogether(anim1,anim2)
        anim.duration = 1500
        anim.start()
    }
}