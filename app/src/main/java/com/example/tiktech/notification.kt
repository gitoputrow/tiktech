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
            overridePendingTransition(0,0)
            finish()
        }

        findViewById<ImageView>(R.id.imageupload_notif).setOnClickListener {
            val pindah = Intent(this,Sharedpost::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            pindah.putExtra("notif",true)
            startActivity(pindah)
            overridePendingTransition(0,0)
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
            overridePendingTransition(0,0)
            finish()
        }
        recyclerViewInflater(ambil)
    }
    data class list_class(var foto: String, var username: String,var childname: String , var name: String, var text: String,var photopost :String, var date: String)
    val listt = mutableListOf<list_class>()
    private fun recyclerViewInflater(ambil: Intent?) {
        database.child("data${user}").child("activity").child("post").addListenerForSingleValueEvent(object :
            ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children){
                    var childname = child.key.toString()
                    var photopost = snapshot.child(childname).child("idfoto").value.toString()
                    if (snapshot.child(childname).hasChild("likes")){
                        for (i in snapshot.child(childname).child("likes").children){
                            var userr = i.key.toString()
                            Log.d(ContentValues.TAG,userr);
                            if (snapshot.child(childname).child("likes").child(userr).child("value").value.toString().equals("true")){
                                var text = " liked on your post"
                                var date = snapshot.child(childname).child("likes").child("user").child("date").value.toString()
                                listt.add(list_class(snapshot.child(childname).child("likes").child(userr).child("fpuser").value.toString()
                                        ,user,childname, snapshot.child(childname).child("likes").child(userr).child("nameuser").value.toString(),
                                        text,photopost,date))
                            }
                        }
                    }
                }
                listt.sortByDescending { listClass -> listClass.date }
                for (i in 0..listt.size-1){
                    list(i,listt)
                }
            }

        })
    }

    fun list(i :Int,listt :MutableList<list_class>){
        list.add(notifitem(listt[i].foto,listt[i].text,listt[i].username,listt[i].childname,listt[i].photopost))
        findViewById<RecyclerView>(R.id.recyclerView_notif).setHasFixedSize(true)
        findViewById<RecyclerView>(R.id.recyclerView_notif).layoutManager = LinearLayoutManager(this@notification)
        val adapter = notifadapter(list,this@notification)
        findViewById<RecyclerView>(R.id.recyclerView_notif).adapter = adapter
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