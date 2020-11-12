package com.example.jednocertovane.canvasobjects

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.jednocertovane.R

class Intro(context: Context): CanvasObject(context) {
    val backgroundBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.intro)
    val btnBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.intro_btn)

    override fun draw(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height

        var dst = Rect(0, 0, width, height)
        canvas.drawBitmap(
            backgroundBitmap, null, dst, null
        )

        dst = Rect((0.4*width).toInt(), (0.7*height).toInt(), (0.6*width).toInt(), (0.8*height).toInt())
        canvas.drawBitmap(
            btnBitmap, null, dst, null
        )
    }
}