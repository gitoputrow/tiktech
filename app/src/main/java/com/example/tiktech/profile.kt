package com.example.tiktech

import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import coil.load
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.*
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.InputStream


class profile : AppCompatActivity() , clicklistener{
    var x = ""
    val storage = Firebase.storage("gs://tiktech-cb01d.appspot.com").reference
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var adapter: feedadapter
    private val list = ArrayList<feeditem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val ambil = intent
        x = ambil.getStringExtra("username").toString()
        findViewById<TextView>(R.id.usernameprofile).setText(ambil.getStringExtra("username"))
        database.child("data${ambil.getStringExtra("username")}").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                findViewById<TextView>(R.id.namaprofile).setText("${snapshot.child("name").value.toString()}")
                if (snapshot.child("profile").value.toString().equals("true")){
                    storage.child("profile${ambil.getStringExtra("username")}").downloadUrl.addOnSuccessListener(object :
                            OnSuccessListener<Uri>{
                        override fun onSuccess(p0: Uri?) {
                            database.child("data${ambil.getStringExtra("username")}").child("fp").setValue(p0.toString())
                        }

                    })
                    findViewById<CircleImageView>(R.id.fotoprofile).load(snapshot.child("fp").value.toString())
                }
                if (snapshot.child("activity").hasChild("post")){
                    findViewById<TextView>(R.id.angkapost).setText(snapshot.child("activity").child("post").childrenCount.toString())
                    database.child("data${ambil.getStringExtra("username")}").child("activity").child("post")
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                var total = 0
                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }

                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (child in snapshot.children){
                                        var childname = child.key.toString()
                                        if (snapshot.child(childname).hasChild("likes")){
                                            total = (total + snapshot.child(childname).child("likes").childrenCount).toInt()
                                        }
                                    }
                                    findViewById<TextView>(R.id.angkalikes).setText(total.toString())
                                }

                            })
                }
            }

        })

        findViewById<ImageView>(R.id.imagehome_profile).setOnClickListener {
            val pindah = Intent(this,Home::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            overridePendingTransition(0,0)
            finish()
        }
        findViewById<ImageView>(R.id.imageupload_profile).setOnClickListener {
            val pindah = Intent(this,Sharedpost::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            pindah.putExtra("profile",true)
            startActivity(pindah)
            overridePendingTransition(0,0)
            finish()
        }
        findViewById<Button>(R.id.buttonep).setOnClickListener {
            val pindah = Intent(this,Editprofile::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        findViewById<ImageView>(R.id.settingprofile).setOnClickListener {
            val pindah = Intent(this,setting_menu::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        findViewById<CircleImageView>(R.id.fotoprofile).setOnClickListener {
            val array = arrayOf("Take Picture","Select Picture","cancel")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Add Picture")
            builder.setItems(array,DialogInterface.OnClickListener { dialog, which ->
                if (array[which].equals("Take Picture")){
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent,0)
                }
                else if (array[which].equals("Select Picture")){
                    val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent,1)
                }
                else{
                    dialog.dismiss()
                }
            })
            builder.show()
        }
        recyclerViewInflater()

        findViewById<SwipeRefreshLayout>(R.id.refresh_profile).setOnRefreshListener{
            findViewById<SwipeRefreshLayout>(R.id.refresh_profile).isRefreshing = true
            finish()
            overridePendingTransition(0,0)
            val pindah = Intent(this,profile::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            overridePendingTransition(0,0)
            findViewById<SwipeRefreshLayout>(R.id.refresh_profile).isRefreshing = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0){
            if (data != null){
                if (data.extras != null) {
                    val bitmap: Bitmap = data!!.extras!!.get("data") as Bitmap
                    findViewById<CircleImageView>(R.id.fotoprofile).setImageBitmap(bitmap)
                    val bitmap1 = (findViewById<CircleImageView>(R.id.fotoprofile).drawable as BitmapDrawable).bitmap
                    val baos = ByteArrayOutputStream()
                    bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                    val data = baos.toByteArray()
                    storage.child("profile${x}")
                            .putBytes(data)
                    database.child("data${x}").child("profile").setValue("true")
                }
            }
        }
        else {
            if (data != null) {
                findViewById<CircleImageView>(R.id.fotoprofile).setImageURI(data?.data)
                val bitmap1 = (findViewById<CircleImageView>(R.id.fotoprofile).drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()
                storage.child("profile${x}")
                        .putBytes(data)
                database.child("data${x}").child("profile").setValue("true")
            }
        }
    }
    data class list_class(var foto: String, var username: String,var childname: String , var name: String, var text: String, var date: String)
    val listt = mutableListOf<list_class>()
    private fun recyclerViewInflater() {
//        Log.d(ContentValues.TAG,x.toString());
        database.child("data${x}").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("activity").hasChild("post")){
                    var name = snapshot.child("name").value.toString()
//                    Log.d(ContentValues.TAG,name.toString());
                    database.child("data${x}").child("activity").child("post").orderByChild("date")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {}
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var text = ""
                                    for (child in snapshot.children){
                                        var childname = child.key.toString()
                                        var childnametext = snapshot.child(childname).child("text").value.toString()
                                        var foto_status = snapshot.child(childname).child("foto").value.toString()
                                        var date = snapshot.child(childname).child("date").value.toString()
                                        if (foto_status.equals("true")){
                                            storage.child("post$x")
                                                    .child(childname).downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                                                        override fun onSuccess(p0: Uri?) {
                                                            database.child("data$x").child("activity")
                                                                    .child("post")
                                                                    .child(childname).child("idfoto").setValue(p0.toString())
                                                        }
                                                    })
                                        }
                                        var foto = snapshot.child(childname).child("idfoto").value.toString()
                                        if (childnametext.length > 20){
                                            text = "${childnametext.removeRange(20,childnametext.length)}...."
                                        }
                                        else{
                                            text = childnametext
                                        }
                                        listt.add(list_class(foto,x,childname,name,text, date))
                                    }
                                    listt.sortByDescending { listClass -> listClass.date }
                                    for (i in 0..listt.size-1){
                                        list(i,listt)
                                    }
                                }
                            })
                    }
                }
        })
    }

    fun list(i: Int, listt : MutableList<list_class>){
        list.add(feeditem(listt[i].foto,listt[i].name,listt[i].username, listt[i].childname,listt[i].text))
        findViewById<RecyclerView>(R.id.postprofile).setHasFixedSize(true)
        findViewById<RecyclerView>(R.id.postprofile).layoutManager = LinearLayoutManager(this@profile)
        val adapter = feedadapter(list,this@profile)
        findViewById<RecyclerView>(R.id.postprofile).adapter = adapter
    }

    override fun onitemclick(item: feeditem, position: Int) {
        val pindah = Intent(this,readmore::class.java)
        pindah.putExtra("usernamepost",item.username)
        pindah.putExtra("username",x)
        pindah.putExtra("postid",item.postid)
        pindah.putExtra("foto", item.photo)
        startActivity(pindah)
        overridePendingTransition(0,0)
        finish()
    }
}