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
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.io.InputStream


class profile : AppCompatActivity() , clicklistener{
    val storage = Firebase.storage("gs://tiktech-cb01d.appspot.com").reference
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private var layoutManager: RecyclerView.LayoutManager? = null
    private lateinit var adapter: feedadapter
    private val list = ArrayList<feeditem>()
    var x = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val ambil = intent
        x = ambil.getStringExtra("username").toString()
        findViewById<TextView>(R.id.usernameprofile).setText(ambil.getStringExtra("username"))

        database.child("data${ambil.getStringExtra("username")}").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                findViewById<TextView>(R.id.namaprofile).setText("${snapshot.child("name").value.toString()}")
                if (snapshot.child("profile").value.toString().equals("true")){
                    storage.child("profile${ambil.getStringExtra("username")}").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                        override fun onSuccess(p0: Uri?) {
                            Glide.with(this@profile).load(p0).into(findViewById(R.id.fotoprofile))
                            Log.d(ContentValues.TAG,p0.toString());
                        }
                    })
                }
                if (snapshot.child("activity").hasChild("post")){
                    findViewById<TextView>(R.id.angkapost).setText(snapshot.child("activity").child("post").childrenCount.toString())
                    database.child("data${ambil.getStringExtra("username")}").child("activity").child("post")
                            .addValueEventListener(object : ValueEventListener{
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
            finish()
        }
        findViewById<ImageView>(R.id.imageupload_profile).setOnClickListener {
            val pindah = Intent(this,Sharedpost::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            pindah.putExtra("profile",true)
            startActivity(pindah)
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
                    database.child("data${ambil.getStringExtra("username")}").child("profile").setValue("true")
                }
                else if (array[which].equals("Select Picture")){
                    val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent,1)
                    database.child("data${ambil.getStringExtra("username")}").child("profile").setValue("true")
                }
                else{
                    dialog.dismiss()
                }
            })
            builder.show()
        }
        recyclerViewInflater(ambil)
    }

    override fun onitemclick(item: feeditem, position: Int) {
        val pindah = Intent(this,readmore::class.java)
        pindah.putExtra("usernamepost",item.username)
        pindah.putExtra("username",x)
        pindah.putExtra("postid",item.postid)
        pindah.putExtra("foto", item.photo)
        finish()
        startActivity(pindah)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0){
            val bitmap :Bitmap = data!!.extras!!.get("data") as Bitmap
            findViewById<CircleImageView>(R.id.fotoprofile).setImageBitmap(bitmap)
        }
        else{
//            var bitmap = (findViewById<CircleImageView>(R.id.fotoprofile).drawable as BitmapDrawable).bitmap
            findViewById<CircleImageView>(R.id.fotoprofile).setImageURI(data?.data)
        }
        val bitmap1 = (findViewById<CircleImageView>(R.id.fotoprofile).drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        storage.child("profile${x}")
                .putBytes(data)
    }
    private fun recyclerViewInflater(ambil : Intent) {
        database.child("data${ambil.getStringExtra("username")}").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child("activity").hasChild("post")){
                    var name = snapshot.child("name").value.toString()
                    database.child("data${ambil.getStringExtra("username")}").child("activity").child("post")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(error: DatabaseError) {}
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    var text = ""
                                    for (child in snapshot.children){
                                        var childname = child.key.toString()
                                        var childnametext = snapshot.child(childname).child("text").value.toString()
                                        var childnamefoto = snapshot.child(childname).child("foto").value.toString()
                                        if (childnametext.length > 20){
                                            text = "${childnametext.removeRange(20,childnametext.length)}...."
                                            list(childnamefoto,ambil,childname,name,text)
                                        }
                                        else{
                                            text = childnametext
                                            list(childnamefoto,ambil,childname,name,text)
                                        }
                                    }
                                }

                            })
                }
            }

        })
    }
    fun list(childnamefoto : String,ambil: Intent,childname : String,name : String,text : String){
        if (childnamefoto.equals("true")){
            storage.child("post${ambil.getStringExtra("username")}")
                    .child(childname).downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                        override fun onSuccess(p0: Uri?) {
                            list.add(feeditem(p0.toString(),name,ambil.getStringExtra("username"), childname,text))
                            findViewById<RecyclerView>(R.id.postprofile).setHasFixedSize(true)
                            findViewById<RecyclerView>(R.id.postprofile).layoutManager = LinearLayoutManager(this@profile)
                            val adapter = feedadapter(list,this@profile)
                            findViewById<RecyclerView>(R.id.postprofile).adapter = adapter
                        }
                    })
        }
        else{
            storage.child("default.png").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                override fun onSuccess(p0: Uri?) {
                    Log.d(ContentValues.TAG,text)
                    list.add(feeditem(p0.toString(),name,ambil.getStringExtra("username"), childname,text))
                    findViewById<RecyclerView>(R.id.postprofile).setHasFixedSize(true)
                    findViewById<RecyclerView>(R.id.postprofile).layoutManager = LinearLayoutManager(this@profile)
                    val adapter = feedadapter(list,this@profile)
                    findViewById<RecyclerView>(R.id.postprofile).adapter = adapter
                }
            })
        }
    }
}