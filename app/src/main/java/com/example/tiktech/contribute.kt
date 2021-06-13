package com.example.tiktech

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.HashMap

class contribute : AppCompatActivity() {
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var adapter: komenadapter
    private val list = ArrayList<komenitem>()
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    val storage = Firebase.storage("gs://tiktech-cb01d.appspot.com").reference
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contribute)
        val ambil = intent
        findViewById<ImageView>(R.id.backarrow_constribute).setOnClickListener {
            val pindah = Intent(this,readmore::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            pindah.putExtra("postid",ambil.getStringExtra("postid"))
            pindah.putExtra("usernamepost",ambil.getStringExtra("usernamepost"))
            pindah.putExtra("foto",ambil.getStringExtra("foto"))
            startActivity(pindah)
            finish()
//            onBackPressed()
//            finish()
        }
        findViewById<ImageView>(R.id.imageView7).setOnClickListener {
            if (findViewById<TextInputEditText>(R.id.comment_input).text.toString().isNotEmpty()){
                var date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                var hashMapcomment = HashMap<String, Any>()
                hashMapcomment.put("text",findViewById<TextInputEditText>(R.id.comment_input).text.toString())
                hashMapcomment.put("date",date)
                hashMapcomment.put("value","true")
                database.child("data${ambil.getStringExtra("usernamepost")}").addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(snapshot: DataSnapshot) {
                        findViewById<ImageView>(R.id.imageView7).visibility = View.INVISIBLE
                        findViewById<ProgressBar>(R.id.progressBar_komen).visibility = View.VISIBLE
                        database.child("data${ambil.getStringExtra("usernamepost")}").child("activity").child("post")
                                .child(ambil.getStringExtra("postid").toString()).child("contribute")
                                .child(ambil.getStringExtra("username").toString()).child("comment").push().setValue(hashMapcomment)
                                .addOnSuccessListener {
                                    findViewById<TextInputEditText>(R.id.comment_input).setText("")
                                    findViewById<ProgressBar>(R.id.progressBar_komen).visibility = View.INVISIBLE
                                    findViewById<ImageView>(R.id.imageView7).visibility = View.VISIBLE
                                }

                    }

                })
            }
            else{
                Toast.makeText(baseContext,"fill the message",Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<SwipeRefreshLayout>(R.id.refresh_constribute).setOnRefreshListener{
            findViewById<SwipeRefreshLayout>(R.id.refresh_constribute).isRefreshing = true
            finish()
            overridePendingTransition(0,0)
            val pindah = Intent(this,contribute::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            pindah.putExtra("postid",ambil.getStringExtra("postid"))
            pindah.putExtra("usernamepost",ambil.getStringExtra("usernamepost"))
            pindah.putExtra("foto",ambil.getStringExtra("foto"))
            startActivity(pindah)
            overridePendingTransition(0,0)
            findViewById<SwipeRefreshLayout>(R.id.refresh_constribute).isRefreshing = false
        }
        recyclerViewInflater(ambil)
    }
    data class list_class(var username: String, var foto: String, var text: String, var date: String)
    val listt = mutableListOf<list_class>()
    private fun recyclerViewInflater(ambil : Intent) {
        database.child("data${ambil.getStringExtra("usernamepost")}").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("activity").child("post").child(ambil.getStringExtra("postid").toString()).hasChild("contribute")){
                    for (username_cm in snapshot.child("activity").child("post").child(ambil.getStringExtra("postid").toString())
                            .child("contribute").children){
                        var username_cmid = username_cm.key.toString()
                        database.child("data${username_cmid}").addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                database.child("data${ambil.getStringExtra("usernamepost")}").child("activity").child("post")
                                        .child(ambil.getStringExtra("postid").toString()).child("contribute")
                                        .child(username_cmid).child("fpcomment")
                                        .setValue(snapshot.child("fp").value.toString())
                            }

                        })

                        for (comment_id in snapshot.child("activity").child("post").child(ambil.getStringExtra("postid").toString())
                                .child("contribute").child(username_cmid).child("comment").children){
                            var comment_idd = comment_id.key.toString()
                            var text = snapshot.child("activity").child("post").child(ambil.getStringExtra("postid").toString())
                                    .child("contribute").child(username_cmid).child("comment").child(comment_idd).child("text").value.toString()
                            var date = snapshot.child("activity").child("post").child(ambil.getStringExtra("postid").toString())
                                    .child("contribute").child(username_cmid).child("comment").child(comment_idd).child("date").value.toString()
                            var foto = snapshot.child("activity").child("post").child(ambil.getStringExtra("postid").toString())
                                    .child("contribute").child(username_cmid).child("fpcomment").value.toString()
                            listt.add(list_class(username_cmid,foto,text,date))
                        }
                    }
                    listt.sortBy { listClass -> listClass.date }
                    for (i in 0..listt.size-1){
                        list(i,listt)
                    }
                }
            }

        })
    }
    fun list(i: Int, listt : MutableList<list_class>){
        list.add(komenitem(listt[i].username,listt[i].foto,listt[i].text))
        findViewById<RecyclerView>(R.id.rv_constribute).setHasFixedSize(true)
        findViewById<RecyclerView>(R.id.rv_constribute).layoutManager = LinearLayoutManager(this)
        val adapter = komenadapter(list)
        findViewById<RecyclerView>(R.id.rv_constribute).adapter = adapter
    }
}