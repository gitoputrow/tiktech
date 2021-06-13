package com.example.tiktech

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage

class Register_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = FirebaseDatabase.getInstance().getReference()
        val storage = Firebase.storage("gs://tiktech-cb01d.appspot.com").reference
        setContentView(R.layout.activity_register_)
        hideshow(findViewById(R.id.hidepass),findViewById(R.id.showpass),findViewById(R.id.passwordinput))
        hideshow(findViewById(R.id.hideconpass),findViewById(R.id.showconpass),findViewById(R.id.confirmpasswordinput))
        animation(R.id.judul,0)
        animation(R.id.textView5,200)
        animation(R.id.nameinput,200)
        animation(R.id.textusername,400)
        animation(R.id.usernameinput,400)
        animation(R.id.textemail,600)
        animation(R.id.emailinput,600)
        animation(R.id.textpassword,800)
        animation(R.id.passwordinput,800)
        animation(R.id.hidepass,800)
        animation(R.id.textconfirmpassword,1000)
        animation(R.id.confirmpasswordinput,1000)
        animation(R.id.hideconpass,1000)
        animation(R.id.masuk,1100)

        findViewById<Button>(R.id.masuk).setOnClickListener(){
            if ((findViewById<EditText>(R.id.confirmpasswordinput).text.isEmpty()) or
                    (findViewById<EditText>(R.id.passwordinput).text.isEmpty()) or
                    (findViewById<EditText>(R.id.nameinput).text.isEmpty()) or
                    (findViewById<EditText>(R.id.usernameinput).text.isEmpty()) or
                    (findViewById<EditText>(R.id.emailinput).text.isEmpty())){
                Toast.makeText(baseContext,"Fill all the data",Toast.LENGTH_SHORT).show()
            }
            else {
                if (findViewById<EditText>(R.id.usernameinput).text.toString().indexOf(" ") >=0 ){
                    Toast.makeText(baseContext, "Username can't contain spaces",Toast.LENGTH_SHORT).show()
                }
                else{
                    if (findViewById<EditText>(R.id.confirmpasswordinput).text.toString().equals(findViewById<EditText>(R.id.passwordinput).text.toString())) {
                        database.addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.hasChild("data${findViewById<EditText>(R.id.usernameinput).text}")) {
                                    Toast.makeText(baseContext, "Username Already Taken", Toast.LENGTH_SHORT).show()
                                } else {
                                    masuk(database,storage,findViewById(R.id.masuk),findViewById(R.id.progressBar_regist))

                                }
                            }

                        })
                    }
                    else {
                        Toast.makeText(baseContext, "Password doesn't match", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
    fun hideshow(hide :ImageView,show :ImageView,edittext :EditText){
        hide.setOnClickListener {
            edittext.transformationMethod = HideReturnsTransformationMethod.getInstance()
            show.visibility = View.VISIBLE
            hide.visibility = View.INVISIBLE
        }
        show.setOnClickListener {
            edittext.transformationMethod = PasswordTransformationMethod.getInstance()
            hide.visibility = View.VISIBLE
            show.visibility = View.INVISIBLE
        }
    }
    fun animation(x: Int,delay :Long){
        val anim1 :ObjectAnimator = ObjectAnimator.ofFloat(findViewById(x),View.ALPHA,0f,1f)
        val anim2 :ObjectAnimator = ObjectAnimator.ofFloat(findViewById(x),View.TRANSLATION_Y,200f,0f)
        val anim :AnimatorSet = AnimatorSet()
        anim.playTogether(anim1,anim2)
        anim.duration = 1200
        anim.startDelay = delay
        anim.start()
    }
    fun masuk(database : DatabaseReference,storage: StorageReference,button: Button,loading: ProgressBar){
        button.visibility = View.INVISIBLE
        loading.visibility = View.VISIBLE
        storage.child("profile2.png").downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri>{
            override fun onSuccess(p0: Uri?) {
                database.child("data${findViewById<EditText>(R.id.usernameinput).text}")
                        .setValue(Regist(findViewById<EditText>(R.id.usernameinput).text.toString(),
                                findViewById<EditText>(R.id.passwordinput).text.toString(),
                                findViewById<EditText>(R.id.nameinput).text.toString(),
                                findViewById<EditText>(R.id.emailinput).text.toString(),"false",p0.toString()))
                        .addOnSuccessListener {
                            activty(database)
                            loading.visibility = View.INVISIBLE
                            Toast.makeText(baseContext,"Successful",Toast.LENGTH_SHORT).show()
                            val pindah = Intent(this@Register_Activity,Login_activty::class.java)
                            startActivity(pindah)
                            finish()
                        }
            }

        })

    }
    fun activty(database : DatabaseReference){
        var hashMaplike = HashMap<String, Any>()
        hashMaplike.put("like","0")
        hashMaplike.put("constribution","0")
        database.child("data${findViewById<EditText>(R.id.usernameinput).text}")
                .child("activity")
                .setValue(hashMaplike)
    }
}