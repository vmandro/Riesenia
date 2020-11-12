package com.example.breakball

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import androidx.core.content.res.ResourcesCompat

class Square(val gameX: Int, val gameY: Int, ch: Char, val squareSize: Int, resources: Resources, ctx: Context) {
    val color: Int
    var touchesToDestroy: Int?

    var left: Int
    var right: Int
    var top: Int
    var bottom: Int

    init {
        color = setColor(resources, ctx, ch)
        touchesToDestroy = setTouchesToDestroy(resources, ctx, ch)

        left = gameX * squareSize
        right = (gameX + 1) * squareSize
        top = gameY * squareSize
        bottom = (gameY + 1) * squareSize
    }

    fun setTouchesToDestroy(resources: Resources, ctx: Context, ch: Char): Int? {
        if(ch.equals('U')) {                                                                                     // Unbreakable
            return null
        }
        val touchesToDestroyId = resources.getIdentifier("touches_to_destroy_$ch", "integer", ctx.getPackageName())    // Touches to break the Square
        return resources.getInteger(touchesToDestroyId)
    }

    fun setColor(resources: Resources, ctx: Context, ch: Char): Int {
        val colorId = resources.getIdentifier("color_$ch", "color", ctx.getPackageName())    // Touches to break the Square
        return ResourcesCompat.getColor(resources, colorId, null)
    }

    fun draw(canvas: Canvas) {
        val shape = ShapeDrawable(RectShape())
        var left = gameX * squareSize
        val top = gameY * squareSize
        val right = (gameX + 1) * squareSize
        var bottom = (gameY + 1) * squareSize
        shape.setBounds(left, top, right, bottom)
        shape.paint.color = color
        shape.draw(canvas)
        // upravit do canvas.drawRect()

        val paint = Paint()
        paint.color = Color.BLACK
        paint.textAlign = Paint.Align.CENTER
        paint.textSize = (squareSize / 2).toFloat()
        left += (squareSize / 2)
        bottom -= (squareSize / 3)
        canvas.drawText(touchesToDestroy?.toString() ?: "", left.toFloat(), bottom.toFloat(), paint)
    }

    fun touchFromLeft(ball: Ball) : Boolean {
        return touch(ball.rightSide().toInt(), ball.getY().toInt())
        //return ball.rightSide().toInt() in left..right && ball.getY().toInt() in top..bottom
    }

    fun touchFromRight(ball: Ball) : Boolean {
        return touch(ball.leftSide().toInt(), ball.getY().toInt())
        //return ball.leftSide().toInt() in left..right && ball.getY().toInt() in top..bottom
    }

    fun touchFromTop(ball: Ball) : Boolean {
        return touch(ball.getX().toInt(), ball.bottomSide().toInt())
        //return ball.getX().toInt() in left..right && ball.bottomSide().toInt() in top..bottom
    }

    fun touchFromBottom(ball: Ball) : Boolean {
        return touch(ball.getX().toInt(), ball.topSide().toInt())
        //return ball.getX().toInt() in left..right && ball.topSide().toInt() in top..bottom
    }

    fun touch(x: Int, y: Int) : Boolean {
        var delta = squareSize / 6
        delta = 0
        return x in (left - delta)..(right + delta) && y in (top - delta)..(bottom + delta)
    }

    fun minusFromTouchesToDestroy() {
        touchesToDestroy = touchesToDestroy?.minus(1)   // Ako inak to bezpecne zapisat?
    }

    fun canBeDestroy() : Boolean {
        return touchesToDestroy != null && touchesToDestroy == 0
    }
}