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
    data class list_class(var foto: String, var username_post: String,var postid: String ,  var text: String,var photopost :String, var date: String,var username_lc :String, var commemid :String)
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
                                database.child("data${userr}").addListenerForSingleValueEvent(object : ValueEventListener{
                                    override fun onCancelled(error: DatabaseError) {

                                    }

                                    override fun onDataChange(snapshot: DataSnapshot) {
                                        database.child("data${user}").child("activity").child("post")
                                                .child(childname).child("likes").child(userr).child("fp")
                                                .setValue(snapshot.child("fp").value.toString())

                                    }
                                })
                                var fp =  snapshot.child(childname).child("likes").child(userr).child("fp").value.toString()
                                var text = " liked on your post"
                                var date = snapshot.child(childname).child("likes").child(userr).child("date").value.toString()
                                listt.add(list_class(fp,user,childname,text,photopost,date,userr,""))
                            }
                        }
                    }
                    if (snapshot.child(childname).hasChild("constribute")){
                        for (userr in snapshot.child(childname).child("constribute").children){
                            var user_name = userr.key.toString()
                            var fp = snapshot.child(childname).child("constribute").child(user_name).child("fp").value.toString()
                            for (comment in  snapshot.child(childname).child("constribute").child(user_name).child("comment").children){
                                var comment_id = comment.key.toString()
                                if (snapshot.child(childname).child("constribute").child(user_name).child("comment").child(comment_id).child("value").value.toString().equals("true")){
                                    var date = snapshot.child(childname).child("constribute").child(user_name).child("comment").child(comment_id).child("date").value.toString()
                                    var text = snapshot.child(childname).child("constribute").child(user_name).child("comment").child(comment_id).child("text").value.toString()
                                    if (text.length > 4){
                                        text = " comment '${text.removeRange(4,text.length)}...' on your post"
                                    }
                                    else{
                                        text = " comment '$text' on your post"
                                    }
                                    listt.add(list_class(fp,user,childname,text,photopost,date,user_name,comment_id))
                                }


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
        list.add(notifitem(listt[i].foto,listt[i].text,listt[i].username_post,listt[i].postid,listt[i].photopost,listt[i].username_lc,listt[i].commemid))
        findViewById<RecyclerView>(R.id.recyclerView_notif).setHasFixedSize(true)
        findViewById<RecyclerView>(R.id.recyclerView_notif).layoutManager = LinearLayoutManager(this@notification)
        val adapter = notifadapter(list,this@notification)
        findViewById<RecyclerView>(R.id.recyclerView_notif).adapter = adapter
    }

    override fun onnotifclick(item: notifitem, position: Int) {
        database.child("data${user}").child("activity").child("post")
                .child(item.postid.toString()).child("likes").child(item.username.toString()).child("value").setValue("false")
        database.child("data${user}").child("activity").child("post").child(item.postid.toString())
                .child("constribute").child(item.username.toString()).child("comment")
                .child(item.commentid.toString()).child("value").setValue("false")
        val pindah = Intent(this,readmore::class.java)
        pindah.putExtra("username",user)
        pindah.putExtra("usernamepost",user)
        pindah.putExtra("postid",item.postid)
        pindah.putExtra("foto", item.photopost)
        startActivity(pindah)
        finish()
    }
}