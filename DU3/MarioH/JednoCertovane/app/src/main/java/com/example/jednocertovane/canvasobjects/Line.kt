package com.example.jednocertovane.canvasobjects

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.jednocertovane.R

class Line(context: Context, val i: Int, val N: Int) : CanvasObject(context) {

    val barrelBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.barrel)
    val tableBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.table)

    override fun draw(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height

        val barrelSize = height / (N + 1)

        var dst = Rect((width * 0.8).toInt(), (i* barrelSize + barrelSize/2), (width * 0.8).toInt() + barrelSize, ((i+1) * barrelSize + barrelSize/2))
        canvas.drawBitmap(
            barrelBitmap, null, dst, null
        )

        dst = Rect((width * 0.1).toInt(), (i* barrelSize + barrelSize), (width * 0.82).toInt() - barrelSize, ((i+1) * barrelSize + barrelSize/2))
        canvas.drawBitmap(
            tableBitmap, null, dst, null
        )
    }

}