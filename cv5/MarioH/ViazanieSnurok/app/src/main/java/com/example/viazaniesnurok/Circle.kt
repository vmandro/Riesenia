package com.example.viazaniesnurok

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import kotlin.math.pow
import kotlin.math.sqrt

class Circle(val N: Int, val hMargin: Float) {
    var x = 0f
    var y = 0f
    private var isFree = true

    fun draw(canvas: Canvas) {
        val paint = Paint()
        val paint2 = Paint()

        paint.setColor(Color.GREEN)
        paint2.setColor(Color.WHITE)

        if (N < 0) {
            x = canvas.width / 3f
            y = -1* N * hMargin
        }
        else {
            x = 2 * canvas.width / 3f
            y = N * hMargin
        }

        canvas.drawCircle(x , y, 80f, paint)
        canvas.drawCircle(x , y, 45f, paint2)

    }

    fun isIn(nx: Float, ny: Float) : Boolean {
        if(sqrt((nx - x).pow(2) + (ny - y).pow(2)) <= 80f) {
            return true

        }
        return false
    }

    fun isFree(): Boolean {
        return isFree
    }

    fun connect() {
        isFree = false
    }

}