package com.example.tiktech

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
            overridePendingTransition(0,0)
            finish()
        }
        findViewById<ImageView>(R.id.imageupload).setOnClickListener {
            val pindah = Intent(this,Sharedpost::class.java)
            pindah.putExtra("home",true)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            overridePendingTransition(0,0)
            finish()
        }
        findViewById<ImageView>(R.id.imagenotif).setOnClickListener {
            val pindah = Intent(this,notification::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            overridePendingTransition(0,0)
            finish()
        }
        findViewById<SwipeRefreshLayout>(R.id.refresh).setOnRefreshListener {
            findViewById<SwipeRefreshLayout>(R.id.refresh).isRefreshing = true
            finish()
            overridePendingTransition(0,0)
            val pindah = Intent(this,Home::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            overridePendingTransition(0,0)
            findViewById<SwipeRefreshLayout>(R.id.refresh).isRefreshing = false
        }
        recyclerViewInflater()
    }
    data class list_class(var foto: String, var username: String,var childname: String , var name: String, var text: String, var date: String)
    val listt = mutableListOf<list_class>()
    private fun recyclerViewInflater() {
        database.orderByKey().addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                for (childmain in snapshot.children){
                    var childdata = childmain.key.toString()
                    var username = snapshot.child(childdata).child("user").value.toString()
                    if (snapshot.child(childdata).child("activity").hasChild("post")){
                        var name = snapshot.child(childdata).child("name").value.toString()
                        var text = ""
                        for (child in snapshot.child(childdata).child("activity").child("post").children){
                            var childname = child.key.toString()
                            Log.d(ContentValues.TAG,childdata.toString());
                            Log.d(ContentValues.TAG,username.toString());
                            var childnametext = snapshot.child(childdata).child("activity").child("post").child(childname).child("text").value.toString()
                            var foto_status = snapshot.child(childdata).child("activity").child("post").child(childname).child("foto").value.toString()
                            var date = snapshot.child(childdata).child("activity").child("post").child(childname).child("date").value.toString()
//                            if (foto_status.equals("true")){
//                                storage.child("post$username")
//                                        .child(childname).downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
//                                            override fun onSuccess(p0: Uri?) {
//                                                database.child(childdata).child("activity")
//                                                        .child("post")
//                                                        .child(childname).child("idfoto").setValue(p0.toString())
//                                            }
//                                        })
//                            }
                            var foto = snapshot.child(childdata).child("activity").child("post").child(childname).child("idfoto").value.toString()
                            if (childnametext.length > 20){
                                text = "${childnametext.removeRange(20,childnametext.length)}...."
                            }
                            else{
                                text = childnametext
                            }
                            listt.add(list_class(foto,username,childname,name,text,date))
                        }
                    }
                }
                listt.sortByDescending { listClass -> listClass.date }
                Log.d(ContentValues.TAG,listt.toString());
                for (i in 0..listt.size-1){
                    list(i,listt)
                }

            }
        })

    }
    fun list(i: Int, listt : MutableList<list_class>){
        list.add(feeditem(listt[i].foto,listt[i].name,listt[i].username, listt[i].childname,listt[i].text))
        findViewById<RecyclerView>(R.id.recyclerView).setHasFixedSize(true)
        findViewById<RecyclerView>(R.id.recyclerView).layoutManager = LinearLayoutManager(this@Home)
        val adapter = feedadapter(list,this@Home)
        findViewById<RecyclerView>(R.id.recyclerView).adapter = adapter
    }


    override fun onitemclick(item: feeditem, position: Int) {
        val pindah = Intent(this,readmore::class.java)
        pindah.putExtra("usernamepost",item.username)
        pindah.putExtra("username",username)
        pindah.putExtra("postid",item.postid)
        pindah.putExtra("foto", item.photo)
        startActivity(pindah)
        overridePendingTransition(0,0)
        finish()
    }
}