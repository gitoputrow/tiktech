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
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.*

class Editprofile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editprofile)
        val ambil = intent
        val database : DatabaseReference = FirebaseDatabase.getInstance().getReference()
        animation(R.id.backarrow_ep)
        animation(R.id.judul_ep)
        animation(R.id.header_ep)
        findViewById<ImageView>(R.id.backarrow_ep).setOnClickListener {
            val pindah = Intent(this,profile::class.java)
            pindah.putExtra("username",ambil.getStringExtra("username"))
            startActivity(pindah)
            finish()
        }
        findViewById<Button>(R.id.button_save).setOnClickListener {
            if ((findViewById<EditText>(R.id.name_cp).text.isEmpty()) or (findViewById<EditText>(R.id.name_cp).text.equals(" "))){
                Toast.makeText(baseContext,"Harap isi Name",Toast.LENGTH_SHORT).show()
            }
            else{
                if (findViewById<EditText>(R.id.cpass_cp).text.isEmpty()){
                    Toast.makeText(baseContext,"Harap isi Password",Toast.LENGTH_SHORT).show()
                }
                else{
                    database.child("data${ambil.getStringExtra("username")}").addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }
                        override fun onDataChange(snapshot: DataSnapshot) {
                            Log.d(ContentValues.TAG,snapshot.child("pass").value.toString());
                            if (findViewById<EditText>(R.id.cpass_cp).text.toString().equals(snapshot.child("pass").value.toString())){
                                if (findViewById<EditText>(R.id.npass_cp).text.equals(findViewById<EditText>(R.id.cpass_cp).text.toString())){
                                    Toast.makeText(baseContext,"Password tidak berubah",Toast.LENGTH_SHORT).show()
                                }
                                else{
                                    if (findViewById<EditText>(R.id.cnpass_cp).text.toString().equals(findViewById<EditText>(R.id.npass_cp).text.toString())){
//                                        var hashMapname = HashMap<String, Any>()
//                                        hashMapname.put("name",findViewById<EditText>(R.id.name_cp).text.toString())
//                                        var hashMappass = HashMap<String, Any>()
//                                        hashMappass.put("pass",findViewById<EditText>(R.id.cnpass_cp).text.toString())
//                                        database.child("data${ambil.getStringExtra("username")}").child("name").removeValue()
//                                        database.child("data${ambil.getStringExtra("username")}").child("pass").removeValue()
//                                        database.child("data${ambil.getStringExtra("username")}").updateChildren(hashMapname)
//                                        database.child("data${ambil.getStringExtra("username")}").updateChildren(hashMappass)
                                        database.child("data${ambil.getStringExtra("username")}").child("name").setValue(findViewById<EditText>(R.id.name_cp).text.toString())
                                        database.child("data${ambil.getStringExtra("username")}").child("pass").setValue(findViewById<EditText>(R.id.cnpass_cp).text.toString())
                                                .addOnSuccessListener {
                                                    findViewById<EditText>(R.id.cnpass_cp).setText("")
                                                    findViewById<EditText>(R.id.npass_cp).setText("")
                                                    findViewById<EditText>(R.id.cpass_cp).setText("")
                                                    findViewById<EditText>(R.id.name_cp).setText("")
                                                    Toast.makeText(baseContext,"Berhasil",Toast.LENGTH_SHORT).show()
                                                }
                                    }
                                    else{
                                        Toast.makeText(baseContext,"Password tidak sesuai",Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                            else{
                                Toast.makeText(baseContext,"Password salah",Toast.LENGTH_SHORT).show()
                            }
                        }

                    })
                }
            }
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