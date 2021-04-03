package com.example.tiktech

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class Home : AppCompatActivity() , clicklistener{
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var adapter: feedadapter
    private val list = ArrayList<feeditem>()
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    val storage = Firebase.storage("gs://tiktech-cb01d.appspot.com").reference
    var username = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val ambil = intent
        username = ambil.getStringExtra("username").toString()
        findViewById<ImageView>(R.id.imageprofile).setOnClickListener {
            val pindah = Intent(this,profile::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        findViewById<ImageView>(R.id.imageupload).setOnClickListener {
            val pindah = Intent(this,Sharedpost::class.java)
            pindah.putExtra("home",true)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        findViewById<ImageView>(R.id.imagenotif).setOnClickListener {
            val pindah = Intent(this,notification::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        recyclerViewInflater(ambil)

    }

    private fun recyclerViewInflater(ambil : Intent) {
        database.orderByValue().addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (childmain in snapshot.children){
                    var childdata = childmain.key.toString()
//                    Log.d(ContentValues.TAG,childdata.toString());
                    var username = snapshot.child(childdata).child("user").value.toString()
                    if (snapshot.child(childdata).child("activity").hasChild("post")){
                        var name = snapshot.child(childdata).child("name").value.toString()
                        database.child(childdata).child("activity").child("post")
                                .addValueEventListener(object : ValueEventListener {
                                    override fun onCancelled(error: DatabaseError) {}
                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        var text = ""
                                        for (child in snapshot.children){
                                            var childname = child.key.toString()
//                                            Log.d(ContentValues.TAG,childname.toString());
                                            var childnametext = snapshot.child(childname).child("text").value.toString()
                                            var childnamefoto = snapshot.child(childname).child("foto").value.toString()
                                            if (childnametext.length > 20){
                                                text = "${childnametext.removeRange(20,childnametext.length)}...."
                                                list(childnamefoto,username,childname,name,text)
                                            }
                                            else{
                                                text = childnametext
                                                list(childnamefoto,username,childname,name,text)
                                            }
                                        }
                                    }
                                })
                    }
                }

            }
        })

    }
    fun list(childnamefoto : String,username : String,childname : String,name : String,text : String){
        if (childnamefoto.equals("true")){
            storage.child("post${username}")
                    .child(childname).downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                        override fun onSuccess(p0: Uri?) {
                            list.add(feeditem(p0.toString(),name,username, childname,text))
                            findViewById<RecyclerView>(R.id.recyclerView).setHasFixedSize(true)
                            findViewById<RecyclerView>(R.id.recyclerView).layoutManager = LinearLayoutManager(this@Home)
                            val adapter = feedadapter(list,this@Home)
                            findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
                        }
                    })
        }
        else{
            storage.child("default.png").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                override fun onSuccess(p0: Uri?) {
//                    Log.d(ContentValues.TAG,text);
                    list.add(feeditem(p0.toString(),name,username, childname,text))
                    findViewById<RecyclerView>(R.id.recyclerView).setHasFixedSize(true)
                    findViewById<RecyclerView>(R.id.recyclerView).layoutManager = LinearLayoutManager(this@Home)
                    val adapter = feedadapter(list,this@Home)
                    findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
                }
            })
        }
    }

    override fun onitemclick(item: feeditem, position: Int) {
        val pindah = Intent(this,readmore::class.java)
        pindah.putExtra("usernamepost",item.username)
        pindah.putExtra("username",username)
        pindah.putExtra("postid",item.postid)
        pindah.putExtra("foto", item.photo)
        startActivity(pindah)
    }
}