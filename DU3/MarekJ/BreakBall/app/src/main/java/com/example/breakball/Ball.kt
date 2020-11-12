package com.example.breakball

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import androidx.core.content.res.ResourcesCompat

class Ball(X: Int, Y: Int, R: Int, resources: Resources, ctx: Context) {
    val color: Int
    private var x: Float
    private var y: Float
    private val radius: Float
    private var vector: Vector

    init {
        color = setColor(resources, ctx)
        x = X.toFloat()
        y = Y.toFloat()
        radius = R.toFloat()
        vector = Vector(0F, 0F)
    }

    fun setColor(resources: Resources, ctx: Context) : Int {
        val colorId = resources.getIdentifier("ball_color", "color", ctx.getPackageName())
        return ResourcesCompat.getColor(resources, colorId, null)
    }

    fun draw(canvas: Canvas) {
        val paint = Paint()
        paint.color = color
        canvas.drawCircle(x, y, radius, paint)
    }

    fun move() {
        setX(x + vector.getX())
        setY(y + vector.getY())
    }

    fun setVector(v: Vector) {
        vector = v
    }

    fun getVector(): Vector {
        return vector
    }

    fun leftSide() : Float {
        return x - radius
    }

    fun rightSide() : Float {
        return x + radius
    }

    fun topSide() : Float {
        return y - radius
    }

    fun bottomSide() : Float {
        return y + radius
    }

    fun getX() : Float {
        return x
    }

    fun getY() : Float {
        return y
    }

    fun setX(newX: Float) {
        x = newX
    }

    fun setY(newY: Float) {
        y = newY
    }
}