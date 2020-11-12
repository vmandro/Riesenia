package com.example.jednocertovane.canvasobjects

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.jednocertovane.R

class Background(context: Context, val N: Int) : CanvasObject(context) {
    val floorBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.floor)
    val wallBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.wall)
    val rightWallBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.right_wall)
    val leftWallBitmap = BitmapFactory.decodeResource(context.resources, R.drawable.left_wall)

    override fun draw(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height

        val barrelSize = height / (N + 1)

        var dst = Rect(0, barrelSize/2, width, height)
        canvas.drawBitmap(
            floorBitmap, null, dst, null
        )

        dst = Rect((0.08*width).toInt(), 0, (0.96*width).toInt(), barrelSize)
        canvas.drawBitmap(
            wallBitmap, null, dst, null
        )

        dst = Rect((0.88*width).toInt(), 0, width, height)
        canvas.drawBitmap(
            rightWallBitmap, null, dst, null
        )

        dst = Rect(0, 0, (0.12*width).toInt(), height)
        canvas.drawBitmap(
            leftWallBitmap, null, dst, null
        )
    }
}