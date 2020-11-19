package com.example.du3arcanoid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.properties.Delegates

class Playground(internal var context: Context, attrs: AttributeSet) : View(context, attrs) {

    companion object {
        var run = false
        var load = false
        var started = false
        var mode : String? = null
        var bricks : MutableList<Brick> = mutableListOf()
    }

    var h by Delegates.notNull<Int>()
    var w = 0

    lateinit var ball: Ball
    lateinit var player: Player
    
    var mPaint: Paint = Paint()

    init {
        mPaint.color = Color.WHITE
    }

    fun initGame() {
        if (w == 0) return

        val rows = 4
        val cols = 5
        val margin = 8
        val bWidth = (w - (cols-1) * margin) / cols
        val bHeight = 85
        bricks = mutableListOf()
        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val x = bWidth * j + margin * j
                val y = bHeight * i + margin * i
                bricks.add(Brick(x, y, bHeight, bWidth, mPaint))
            }
        }

        // init player
        player = Player(w / 2 - 100, h - 350, 350, mPaint)
        if (mode == "hard") { player.w = 100 }

        // init ball
        ball = Ball(context, w, h, mPaint)
        ball.centerGravityTo(player)

        load = true
        invalidate()
    }

    fun update() {
        ball.update(player)
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        if (!load) return

        ball.draw(canvas)
        player.draw(canvas)
        bricks.forEach { it.draw(canvas) }

        super.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!load || (run && PlaygroundActivity.paused && started)) return true

        if (event.action == MotionEvent.ACTION_UP) {
            if (!run) {
                PlaygroundActivity.myThread?.start()
            }
            if (PlaygroundActivity.paused) {
                PlaygroundActivity.paused = false
            }
            started = true
            return true
        }

        if(event.action == MotionEvent.ACTION_DOWN || event.action == MotionEvent.ACTION_MOVE){
            val x = event.x.toInt() - player.w/2
            if (0 <= x && x + player.w <= w) {
                player.x = x
                if (!started) {
                    ball.centerGravityTo(player)
                }
                invalidate()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w0: Int, h0: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w0, h0, oldw, oldh)
        w = w0
        h = h0

        initGame()
    }
}
