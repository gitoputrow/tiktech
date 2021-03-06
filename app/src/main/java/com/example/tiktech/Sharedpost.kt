package com.example.tiktech

import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import de.hdodenhof.circleimageview.CircleImageView
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class Sharedpost : AppCompatActivity() {
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    val storage = Firebase.storage("gs://tiktech-cb01d.appspot.com").reference
    var x = ""
    var foto = false
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sharedpost)
        val ambil = intent
        x = ambil.getStringExtra("username").toString()

        findViewById<EditText>(R.id.nulis_sp).doOnTextChanged { text, start, before, count ->
            if (findViewById<EditText>(R.id.nulis_sp).text.isEmpty()){
                findViewById<TextView>(R.id.hint).setVisibility(View.VISIBLE)
            }
            else{
                findViewById<TextView>(R.id.hint).setVisibility(View.INVISIBLE)
            }
        }
        findViewById<EditText>(R.id.nulis_spImage).doOnTextChanged { text, start, before, count ->
            if (findViewById<EditText>(R.id.nulis_spImage).text.isEmpty()){
                findViewById<TextView>(R.id.hintImage).setVisibility(View.VISIBLE)
            }
            else{
                findViewById<TextView>(R.id.hintImage).setVisibility(View.INVISIBLE)
            }
        }
        findViewById<ImageView>(R.id.backarrow_sp).setOnClickListener{
            if(ambil.getBooleanExtra("profile",false)){
                val pindah = Intent(this,profile::class.java)
                pindah.putExtra("username",ambil.getStringExtra("username"))
                startActivity(pindah)
                finish()
            }
            else{
                val pindah = Intent(this,Home::class.java)
                pindah.putExtra("username",ambil.getStringExtra("username"))
                startActivity(pindah)
                finish()
            }
//            onBackPressed()
        }
        findViewById<ImageView>(R.id.camera_sp).setOnClickListener{
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent,0)
        }
        findViewById<ImageView>(R.id.picture_sp).setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent,2)
        }
        findViewById<Button>(R.id.button_sp).setOnClickListener {
            findViewById<Button>(R.id.button_sp).visibility = View.INVISIBLE
            findViewById<ProgressBar>(R.id.progressBar_sp).visibility = View.VISIBLE
            var id = database.push().key.toString()
            var date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            var text = ""
            if (findViewById<EditText>(R.id.nulis_sp).visibility == View.VISIBLE){text = findViewById<EditText>(R.id.nulis_sp).text.toString()}
            else{text = findViewById<EditText>(R.id.nulis_spImage).text.toString()}
            var hashMapisi = HashMap<String, Any>()
            hashMapisi.put("text",text)
            hashMapisi.put("foto",foto.toString())
            hashMapisi.put("date",date.toString())
            database.child("data${ambil.getStringExtra("username")}").child("activity").addListenerForSingleValueEvent(object :
                    ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (foto == true){
                        val bitmap1 = (findViewById<AppCompatImageView>(R.id.imagesp).drawable as BitmapDrawable).bitmap
                        val baos = ByteArrayOutputStream()
                        bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                        val data = baos.toByteArray()
                        storage.child("post${ambil.getStringExtra("username")}")
                                .child(id)
                                .putBytes(data).addOnCompleteListener {
                                    if (it.isSuccessful){
                                        storage.child("post${ambil.getStringExtra("username")}")
                                                .child(id).downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri>{
                                                    override fun onSuccess(p0: Uri?) {
                                                        hashMapisi.put("idfoto",p0.toString())
                                                        database.child("data${ambil.getStringExtra("username")}").child("activity")
                                                                .child("post")
                                                                .child(id)
                                                                .setValue(hashMapisi)
                                                                .addOnSuccessListener {
                                                                    if(ambil.getBooleanExtra("profile",false)){
                                                                        val pindah = Intent(this@Sharedpost,profile::class.java)
                                                                        pindah.putExtra("username",ambil.getStringExtra("username"))
                                                                        startActivity(pindah)
                                                                        finish()
                                                                    }
                                                                    else{
                                                                        val pindah = Intent(this@Sharedpost,Home::class.java)
                                                                        pindah.putExtra("username",ambil.getStringExtra("username"))
                                                                        startActivity(pindah)
                                                                        finish()
                                                                    }
                                                                }
                                                    }

                                                })
                                    }
                                }
                        }
                    else{
                        storage.child("default.png").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                            override fun onSuccess(p0: Uri?) {
                                hashMapisi.put("idfoto",p0.toString())
                                database.child("data${ambil.getStringExtra("username")}").child("activity")
                                        .child("post")
                                        .push()
                                        .setValue(hashMapisi)
                                        .addOnSuccessListener {
                                            if(ambil.getBooleanExtra("profile",false)){
                                                val pindah = Intent(this@Sharedpost,profile::class.java)
                                                pindah.putExtra("username",ambil.getStringExtra("username"))
                                                startActivity(pindah)
                                                finish()
                                            }
                                            else{
                                                val pindah = Intent(this@Sharedpost,Home::class.java)
                                                pindah.putExtra("username",ambil.getStringExtra("username"))
                                                startActivity(pindah)
                                                finish()
                                            }
                                        }
                            }
                        })
                    }
                }
            })
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0){
            if (data != null) {
                if (data.extras != null){
                    val bitmap : Bitmap = data!!.extras!!.get("data") as Bitmap
                    findViewById<AppCompatImageView>(R.id.imagesp).setImageBitmap(bitmap)
                    findViewById<AppCompatImageView>(R.id.imagesp).visibility = View.VISIBLE
                    findViewById<EditText>(R.id.nulis_sp).visibility = View.INVISIBLE
                    findViewById<TextView>(R.id.hint).visibility = View.INVISIBLE
                }
            }
        }
        else{
            if (data != null){
                findViewById<AppCompatImageView>(R.id.imagesp).setImageURI(data?.data)
                findViewById<AppCompatImageView>(R.id.imagesp).visibility = View.VISIBLE
                findViewById<EditText>(R.id.nulis_sp).visibility = View.INVISIBLE
                findViewById<TextView>(R.id.hint).visibility = View.INVISIBLE
            }
        }
        if (findViewById<AppCompatImageView>(R.id.imagesp).visibility == View.VISIBLE){
            if (findViewById<EditText>(R.id.nulis_sp).text.isNotEmpty()){
                findViewById<EditText>(R.id.nulis_spImage).visibility = View.VISIBLE
                findViewById<EditText>(R.id.nulis_spImage).text = findViewById<EditText>(R.id.nulis_sp).text
            }
            else {
                findViewById<TextView>(R.id.hintImage).visibility = View.VISIBLE
                findViewById<EditText>(R.id.nulis_spImage).visibility = View.VISIBLE
            }
            foto = true
        }
    }
}