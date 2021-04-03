package com.example.tiktech

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class homescreen_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.homescreen)
        findViewById<Button>(R.id.loginyuk).setOnClickListener{
            val login = Intent(this,Login_activty::class.java)
            startActivity(login)
        }
        findViewById<TextView>(R.id.menuregist).setOnClickListener {
            val regist = Intent(this,Register_Activity::class.java)
            startActivity(regist)
        }
        animation(R.id.imageView2)
        animation(R.id.textView)
        animation(R.id.loginyuk)
        animation(R.id.imageView2)
        animation(R.id.textView2)
        animation(R.id.menuregist)
        animation(R.id.imageView3)
        animation1(R.id.imageView)
    }
    fun animation(x: Int){
        val anim1 : ObjectAnimator = ObjectAnimator.ofFloat(findViewById(x), View.ALPHA,0f,1f)
        val anim2 : ObjectAnimator = ObjectAnimator.ofFloat(findViewById(x), View.TRANSLATION_X,200f,0f)
        val anim : AnimatorSet = AnimatorSet()
        anim.playTogether(anim1,anim2)
        anim.duration = 1500
        anim.startDelay = 1000
        anim.start()
    }
    fun animation1(x: Int){
        val anim1 : ObjectAnimator = ObjectAnimator.ofFloat(findViewById(x), View.ALPHA,0f,1f)
        val anim2 : ObjectAnimator = ObjectAnimator.ofFloat(findViewById(x), View.TRANSLATION_Y,-200f,0f)
        val anim : AnimatorSet = AnimatorSet()
        anim.playTogether(anim1,anim2)
        anim.duration = 1500
        anim.start()
    }
}