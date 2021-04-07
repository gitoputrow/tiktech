package com.example.tiktech

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class notification : AppCompatActivity(), clicklistener_notif {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var adapter: notifadapter
    private val list = ArrayList<notifitem>()
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    val storage = Firebase.storage("gs://tiktech-cb01d.appspot.com").reference
    var user = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        val ambil = intent
        user = ambil.getStringExtra("username").toString()
        findViewById<ImageView>(R.id.imageprofile_notif).setOnClickListener {
            val pindah = Intent(this,profile::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }

        findViewById<ImageView>(R.id.imageupload_notif).setOnClickListener {
            val pindah = Intent(this,Sharedpost::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            pindah.putExtra("notif",true)
            startActivity(pindah)
            finish()
        }
        findViewById<ImageView>(R.id.backarrow_notif).setOnClickListener {
            val pindah = Intent(this,Home::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        findViewById<ImageView>(R.id.imagehome_notif).setOnClickListener {
            val pindah = Intent(this,Home::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        recyclerViewInflater(ambil)
    }

    private fun recyclerViewInflater(ambil: Intent?) {
        database.child("data${user}").child("activity").child("post").addListenerForSingleValueEvent(object :
            ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children){
                    var childname = child.key.toString()
                    if (snapshot.child(childname).hasChild("likes")){
                        for (user in snapshot.child(childname).child("likes").children){
                            var user = user.key.toString()
                            Log.d(ContentValues.TAG,user);
                            if (snapshot.child(childname).child("likes").child(user).child("value").value.toString().equals("true")){
                                var text = " liked on your post"
                                list(user,text,childname,snapshot.child(childname).child("foto").value.toString())
                            }
                        }
                    }
                }
            }

        })
    }
    fun list(userliked : String,text : String,postid : String,fotopost : String){
        database.child("data${userliked}").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("profile").value.toString().equals("true")){
                    storage.child("profile${userliked}").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri>{
                        override fun onSuccess(p0: Uri?) {
                            list_post(fotopost,postid,text,userliked,p0.toString())
                        }

                    })
                }
                else{
                    storage.child("profile.png").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri>{
                        override fun onSuccess(p0: Uri?) {
                            list_post(fotopost,postid,text,userliked,p0.toString())
                        }

                    })
                }
            }
        })
    }
    fun list_post(fotopost: String,postid: String,text: String,userliked: String,fotoprofile : String){
        if (fotopost.equals("true")){
            storage.child("post${user}").child(postid).downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri>{
                override fun onSuccess(p0: Uri?) {
                    list.add(notifitem(fotoprofile,text,userliked,postid,p0.toString()))
                    findViewById<RecyclerView>(R.id.recyclerView_notif).setHasFixedSize(true)
                    findViewById<RecyclerView>(R.id.recyclerView_notif).layoutManager = LinearLayoutManager(this@notification)
                    val adapter = notifadapter(list,this@notification)
                    findViewById<RecyclerView>(R.id.recyclerView_notif).adapter = adapter
                }
            })
        }
        else{
            storage.child("default.png").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri>{
                override fun onSuccess(p0: Uri?) {
                    list.add(notifitem(fotoprofile,text,userliked,postid,p0.toString()))
                    findViewById<RecyclerView>(R.id.recyclerView_notif).setHasFixedSize(true)
                    findViewById<RecyclerView>(R.id.recyclerView_notif).layoutManager = LinearLayoutManager(this@notification)
                    val adapter = notifadapter(list,this@notification)
                    findViewById<RecyclerView>(R.id.recyclerView_notif).adapter = adapter
                }
            })
        }
    }

    override fun onnotifclick(item: notifitem, position: Int) {
        database.child("data${user}").child("activity").child("post")
                .child(item.postid.toString()).child("likes").child(item.username.toString()).child("value").setValue("false")
        val pindah = Intent(this,readmore::class.java)
        pindah.putExtra("username",user)
        pindah.putExtra("usernamepost",user)
        pindah.putExtra("postid",item.postid)
        pindah.putExtra("foto", item.photopost)
        startActivity(pindah)
        finish()
    }
}