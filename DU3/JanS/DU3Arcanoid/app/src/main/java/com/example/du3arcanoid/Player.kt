package com.example.du3arcanoid

import android.graphics.*

class Player(var x : Int, var y : Int, var w: Int, val mPaint: Paint, var h: Int = 30) {

    fun left() = x
    fun right() = x + w
    fun top() = y
    fun bottom() = y + h

    fun draw(canvas: Canvas) {
        canvas.drawRect(left().toFloat(), top().toFloat(), right().toFloat(), bottom().toFloat(), mPaint)
    }
}

