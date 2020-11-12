package com.example.jednocertovane.canvasobjects

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import com.example.jednocertovane.R
import java.util.*
import kotlin.concurrent.schedule

class Player(context: Context, val N: Int): CanvasObject(context) {
    var i = 0 // v ktorom riadku stoji
    var state = 0 // 0 - stoji, 1 - capuje, 2 - berie pohar, 3 - smutny

    val players = listOf<Bitmap>(
        BitmapFactory.decodeResource(context.resources, R.drawable.player),
        BitmapFactory.decodeResource(context.resources, R.drawable.player2),
        BitmapFactory.decodeResource(context.resources, R.drawable.player4),
        BitmapFactory.decodeResource(context.resources, R.drawable.player3)
    )

    override fun draw(canvas: Canvas) {
        val width = canvas.width
        val height = canvas.height

        val playerHeight = height / (N + 1)


        var dst = Rect()
        if(state == 0) {
            dst = Rect((width * 0.8).toInt() - playerHeight / 2, (i* playerHeight + playerHeight/2), (width * 0.8).toInt(), ((i+1) * playerHeight + playerHeight/2))
        }
        else {
            dst = Rect((width * 0.8).toInt() - playerHeight / 2, (i* playerHeight + playerHeight/2), (width * 0.8).toInt()+playerHeight/9, ((i+1) * playerHeight + playerHeight/2))
        }
        canvas.drawBitmap(
            players[state], null, dst, null
        )

    }

    fun moveTo(toI: Int, fill: Boolean): Boolean {
        if(!isBusy() && i < N && i >= 0) {
            i = toI

            state = if(fill) 1
            else 0

            if(isBusy()) {
                Timer().schedule(500) {
                    state = 0
                }
                return true
            }
        }
        return false
    }

    fun catch() {
        state = 2
        Timer().schedule(500) {
            if(state == 2) {
                state = 0
            }
        }

    }

    fun sad() {
        state = 3
        Timer().schedule(1500) {
            if(state == 3) {
                state = 0
            }
        }
    }

    fun isBusy(): Boolean = state != 0 && state != 2
}