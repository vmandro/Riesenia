package com.example.jednocertovane.canvasobjects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.jednocertovane.R

class Lives(context: Context, val maxLives: Int): CanvasObject(context) {
    var lives = maxLives
    val hearts = listOf<Bitmap>(
        BitmapFactory.decodeResource(context.resources, R.drawable.heart_full),
        BitmapFactory.decodeResource(context.resources, R.drawable.heart_empty)
    )

    override fun draw(canvas: Canvas) {
        val width = canvas.width
        val heartWidth = canvas.width * 0.15 / maxLives

        for(i in 0 until maxLives) {

            val heartBitmap = if (i < lives) hearts[0] else hearts[1]

            var dst = Rect((i*heartWidth+(width * 0.1)).toInt(), 0, ((i+1)*heartWidth+(width * 0.1)).toInt(), heartWidth.toInt())
            canvas.drawBitmap(heartBitmap, null, dst, null)
        }
    }

    fun loseLife() {
        if(lives > 0) {
            lives--;
        }
    }

    fun refill() {
        lives = maxLives
    }
}