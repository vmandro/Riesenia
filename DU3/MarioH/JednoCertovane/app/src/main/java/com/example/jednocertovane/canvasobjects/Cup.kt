package com.example.jednocertovane.canvasobjects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.jednocertovane.R
import java.util.*
import kotlin.concurrent.schedule

class Cup(context: Context, val i: Int, val N: Int): MovingCanvasObject(context) {
    var state = 0 //0 - plny, 1 - prazdny, 2 - rozbity
    var width = 0
    var height = 0

    var drinking = false
    var useLess = false

    var cups = listOf<Bitmap>(
        BitmapFactory.decodeResource(context.resources, R.drawable.cup_full),
        BitmapFactory.decodeResource(context.resources, R.drawable.cup_empty),
        BitmapFactory.decodeResource(context.resources, R.drawable.cup_broken)
    )

    init {
        x = length
        dX = -12
    }

    override fun draw(canvas: Canvas) {
        if(drinking) return

        val height = canvas.height
        val width = canvas.width
        this.width = canvas.width
        this.height = canvas.height

        val cupHeight = height / (N + 1)
        val dst = Rect((x / 1000f * 0.65 * width + 0.1 * width ).toInt(), (i* cupHeight + 3*cupHeight/4), (x / 1000f * 0.65 * width + 0.1 * width + 1*cupHeight/4).toInt(), ((i+1)* cupHeight + cupHeight/8))
        canvas.drawBitmap(cups[state], null, dst, null)
    }

    override fun move() {
        x += dX
    }

    fun isBroken(): Boolean {
        if(x <= 0 || (x >= length && state == 1)) {
            state = 2
            dX = 0
            return true
        }
        return false
    }

    fun isCatched(playerI: Int): Boolean {
        if(x >= length && state == 1 && playerI == i) {
            useLess = true
            return true
        }
        return false
    }

    fun drink(goBack: Boolean) {
        drinking = true
        dX = 0
        if(goBack) {
            state = 1
            Timer().schedule(1000) {
                drinking = false
                dX = 12
            } //za 1000 ms ho posli naspat - nastavit rychlost a potom este osetrovat ci ho chytil na konci
        }
        else {
            useLess = true
        }
    }

    fun isFull(): Boolean = state == 0
}