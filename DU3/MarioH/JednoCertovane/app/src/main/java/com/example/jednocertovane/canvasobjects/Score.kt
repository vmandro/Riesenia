package com.example.jednocertovane.canvasobjects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.jednocertovane.R

class Score(context: Context): CanvasObject(context) {
    var score = 0

    val digits = listOf<Bitmap>(
        BitmapFactory.decodeResource(context.resources, R.drawable.digit_0),
        BitmapFactory.decodeResource(context.resources, R.drawable.digit_1),
        BitmapFactory.decodeResource(context.resources, R.drawable.digit_2),
        BitmapFactory.decodeResource(context.resources, R.drawable.digit_3),
        BitmapFactory.decodeResource(context.resources, R.drawable.digit_4),
        BitmapFactory.decodeResource(context.resources, R.drawable.digit_5),
        BitmapFactory.decodeResource(context.resources, R.drawable.digit_6),
        BitmapFactory.decodeResource(context.resources, R.drawable.digit_7),
        BitmapFactory.decodeResource(context.resources, R.drawable.digit_8),
        BitmapFactory.decodeResource(context.resources, R.drawable.digit_9)
    )

    override fun draw(canvas: Canvas) {
        val width = canvas.width
        val digitWidth = canvas.width * 0.025

        val offset = 0.3 * width

        val scoreString = score.toString()
        for(i in 0 until scoreString.length) {
            val bitmap = digits[Character.getNumericValue(scoreString[i])]
            var dst = Rect((i*digitWidth + offset).toInt(), 0, ((i+1)*digitWidth + offset).toInt(), digitWidth.toInt())
            canvas.drawBitmap(bitmap, null, dst, null)
        }
    }

    fun drawResultScore(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height

        val digitWidth = canvas.width * 0.05

        val offset = 0.42 * width

        val scoreString = score.toString()
        for(i in 0 until scoreString.length) {
            val bitmap = digits[Character.getNumericValue(scoreString[i])]
            var dst = Rect((i*digitWidth + offset).toInt(), (height*0.5).toInt(), ((i+1)*digitWidth + offset).toInt(), (height * 0.5 + digitWidth).toInt())
            canvas.drawBitmap(bitmap, null, dst, null)
        }
    }

    fun addScore(toAdd: Int) {
        score += toAdd
    }

    fun subScore(toSub: Int) {
        score -= toSub
        if(score < 0) score = 0
    }

    fun resetScore() {
        score = 0
    }
}