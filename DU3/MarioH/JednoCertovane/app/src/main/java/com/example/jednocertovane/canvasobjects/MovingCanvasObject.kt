package com.example.jednocertovane.canvasobjects

import android.content.Context
import com.example.jednocertovane.canvasobjects.CanvasObject

open abstract class MovingCanvasObject(context: Context): CanvasObject(context) {
    var x = 0
    var dX = 4
    val length = 1000 // length of line

    abstract fun move()
}