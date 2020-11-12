package com.example.jednocertovane.canvasobjects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.jednocertovane.R
import java.util.*
import kotlin.concurrent.schedule

class Guest(context: Context, val id: Int, val i: Int, val N: Int): MovingCanvasObject(context) {
    var cups = 1 //cups needed to drink
    var score = 0

    var isDrinking = false
    var drunk = false
    var isMad = false

    val guests = listOf<Bitmap>(
        BitmapFactory.decodeResource(context.resources, R.drawable.guest1),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest2),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest3),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest4),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest5)
    )

    val guestsDrinking = listOf<Bitmap>(
        BitmapFactory.decodeResource(context.resources, R.drawable.guest1_drinking),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest2_drinking),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest3_drinking),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest4_drinking),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest5_drinking)
    )

    val guestsDrunk = listOf<Bitmap>(
        BitmapFactory.decodeResource(context.resources, R.drawable.guest1),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest2),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest3),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest4_drunk),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest5)
    )

    val guestsMad = listOf<Bitmap>(
        BitmapFactory.decodeResource(context.resources, R.drawable.guest1_mad),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest2_mad),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest3_mad),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest4_mad),
        BitmapFactory.decodeResource(context.resources, R.drawable.guest5_mad)
    )

    val guestPop = BitmapFactory.decodeResource(context.resources, R.drawable.guest_pop)

    val guestsCupsNeeded = listOf(
        1,
        1,
        1,
        2,
        1
    )

    val scores = listOf(
        10,
        20,
        30,
        50,
        35
    )

    val speeds = listOf(
        2,
        4,
        25,
        4,
        2
    )

    init {
        score = scores[id]
        cups = guestsCupsNeeded[id]
        dX = speeds[id]

        if(id == 4) {
            x = 500
        }

    }

    override fun move() {
        if(id == 2 && dX > 3) {
            dX -= 1
        }
        x += dX
    }

    override fun draw(canvas: Canvas) {
        val height = canvas.height
        val width = canvas.width

        val guestHeight = height / (N + 1)

        var bitmap = guests[id]
        if(!isDrinking && drunk) bitmap = guestsDrunk[id]
        else if(isDrinking) bitmap = guestsDrinking[id]
        if(!isDrinking && cups == 0) bitmap = guestPop
        if(isMad) bitmap = guestsMad[id]


        var dst = Rect((x / 1000f * 0.55 * width + 0.1 * width).toInt(), (i* guestHeight + guestHeight/2), (x / 1000f * 0.55 * width + 2*guestHeight/4 + 0.1 * width).toInt(), (i+1)* guestHeight)
        canvas.drawBitmap(bitmap, null, dst, null)

    }

    fun isOnEnd(): Boolean {
        if(x >= length) {
            dX = 0
            return true
        }
        return false
    }

    fun mad() {
        isMad = true
    }

    fun drink() {
        dX = 0
        isDrinking = true

        Timer().schedule(1000) {
            isDrinking = false
            dX = 4
            drunk = true
            cups--
        }
    }
}