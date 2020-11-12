package com.example.jednocertovane.canvasobjects

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.jednocertovane.R

class Result(context: Context): CanvasObject(context) {
    val backgroundBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.result)
    val btnBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.result_btn)

    override fun draw(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height

        var dst = Rect((0.25*width).toInt(), (0.25*height).toInt(), (0.75*width).toInt(), (0.75*height).toInt())
        canvas.drawBitmap(
            backgroundBitmap, null, dst, null
        )

        dst = Rect((0.4*width).toInt(), (0.7*height).toInt(), (0.6*width).toInt(), (0.8*height).toInt())
        canvas.drawBitmap(
            btnBitmap, null, dst, null
        )
    }
}