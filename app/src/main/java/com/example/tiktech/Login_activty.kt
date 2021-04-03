package com.example.tiktech

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.method.TransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.database.*

class Login_activty : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val database :DatabaseReference = FirebaseDatabase.getInstance().getReference()
        animation()
        findViewById<TextView>(R.id.menuregist2).setOnClickListener {
            val regist = Intent(this,Register_Activity::class.java)
            startActivity(regist)
        }
        findViewById<ImageView>(R.id.hide).setOnClickListener {
            findViewById<EditText>(R.id.password).transformationMethod = HideReturnsTransformationMethod.getInstance()
            findViewById<ImageView>(R.id.hide).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.show).visibility = View.VISIBLE
        }
        findViewById<ImageView>(R.id.show).setOnClickListener {
            findViewById<EditText>(R.id.password).transformationMethod = PasswordTransformationMethod.getInstance()
            findViewById<ImageView>(R.id.show).visibility = View.INVISIBLE
            findViewById<ImageView>(R.id.hide).visibility = View.VISIBLE
        }
        findViewById<Button>(R.id.loginyuk2).setOnClickListener{
            database.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChild("data${findViewById<EditText>(R.id.username).text}")){
                        val pass = snapshot.child("data${findViewById<EditText>(R.id.username).text}").child("pass").value.toString()
                        val username = snapshot.child("data${findViewById<EditText>(R.id.username).text}").child("user").value.toString()
                        var cek = Login(username,pass,findViewById<EditText>(R.id.username).text.toString(),findViewById<EditText>(R.id.password).text.toString()).getStatus()
//                        Log.d(ContentValues.TAG,cek.toString());
                        if( cek == true){
                            val home = Intent(this@Login_activty,Home::class.java)
                            home.putExtra("username",findViewById<EditText>(R.id.username).text.toString())
                            startActivity(home)
                        }
                        else{
                            Toast.makeText(baseContext,"Password Salah",Toast.LENGTH_SHORT).show()
                        }
                    }
                    else{
                        Toast.makeText(baseContext,"Username tidak ditemukan",Toast.LENGTH_SHORT).show()
                    }
                }

            })

        }
    }


    fun animation(){
        val anim1:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.imageView4),View.ALPHA,0f,1f)
        val anim2:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.imageView4),View.TRANSLATION_X,-200f,0f)
        val animm1 :AnimatorSet = AnimatorSet()
        animm1.playTogether(anim1,anim2)
        animm1.duration = 1200
        animm1.start()
        val anim3:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.textuser),View.ALPHA,0f,1f)
        val anim4:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.textuser),View.TRANSLATION_Y,200f,0f)
        val anim5:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.username),View.ALPHA,0f,1f)
        val anim6:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.username),View.TRANSLATION_Y,200f,0f)
        val animm2 :AnimatorSet = AnimatorSet()
        animm2.duration = 1200
        animm2.startDelay = 600
        animm2.playTogether(anim3,anim4,anim5,anim6)
        animm2.start()
        val anim7:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.textpass),View.ALPHA,0f,1f)
        val anim8:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.textpass),View.TRANSLATION_Y,200f,0f)
        val anim9:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.password),View.ALPHA,0f,1f)
        val anim10:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.password),View.TRANSLATION_Y,200f,0f)
        val anim11:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.hide),View.ALPHA,0f,1f)
        val anim12:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.hide),View.TRANSLATION_Y,200f,0f)
        val animm3 :AnimatorSet = AnimatorSet()
        animm3.duration = 1200
        animm3.startDelay = 1200
        animm3.playTogether(anim7,anim8,anim9,anim10,anim11,anim12)
        animm3.start()
        val anim13:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.loginyuk2),View.ALPHA,0f,1f)
        val anim14:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.textView4),View.ALPHA,0f,1f)
        val anim15:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.menuregist2),View.ALPHA,0f,1f)
        val animm4 :AnimatorSet = AnimatorSet()
        animm4.duration = 1200
        animm4.startDelay = 1800
        animm4.playTogether(anim13,anim14,anim15)
        animm4.start()
        val anim16:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.imageView5),View.ALPHA,0f,1f)
        val anim17:ObjectAnimator = ObjectAnimator.ofFloat(findViewById(R.id.imageView5),View.TRANSLATION_X,-200f,0f)
        val animm5 :AnimatorSet = AnimatorSet()
        animm5.duration = 1200
        animm5.startDelay = 2400
        animm5.playTogether(anim16,anim17)
        animm5.start()
    }
}
