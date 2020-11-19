package com.example.du3arcanoid

import android.graphics.*

class Brick(val x : Int, val y : Int, val h: Int, val w: Int, val mPaint: Paint, var life: Int = 255) {

    fun left() = x
    fun right() = x + w
    fun top() = y
    fun bottom() = y + h

    fun draw(canvas: Canvas) {
        mPaint.color = Color.argb(life, 255, 255, 255)  // biela s priesvitnostou
        canvas.drawRect(left().toFloat(), top().toFloat(), right().toFloat(), bottom().toFloat(), mPaint)
        mPaint.color = Color.WHITE
    }

    fun getRect() = Rect(left(), top(), right(), bottom())
}