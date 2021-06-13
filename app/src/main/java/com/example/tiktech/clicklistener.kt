package com.example.tiktech

import android.widget.TextView
import java.text.FieldPosition

interface clicklistener {
    fun onitemclick(item: feeditem, position: Int)
//    fun deleteitem(item: feeditem, position: Int)
}