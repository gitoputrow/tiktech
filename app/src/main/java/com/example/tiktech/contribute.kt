package com.example.tiktech

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class contribute : AppCompatActivity() {
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
        }
    }
}