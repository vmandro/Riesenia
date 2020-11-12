package com.example.jednocertovane

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.preference.PreferenceManager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView

class GamePanel(context : Context, attrs: AttributeSet) : SurfaceView(context, attrs), SurfaceHolder.Callback {
    var count = 0
    var frames = 0
    var skipped = 0

    lateinit  var thread : GameThread

    var objs = Objects(context)


    init {
        // adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this)
        val settings = // context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            PreferenceManager.getDefaultSharedPreferences(context)

        setFocusable(true)
        thread = GameThread(this)
    }

    fun drawObjects(canvas: Canvas) {
        val rect_paint = Paint()
        rect_paint.style = Paint.Style.FILL
        rect_paint.color = Color.rgb(0, 0, 0)
        rect_paint.alpha = 0x80 // optional
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), rect_paint)

        objs.draw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if(objs.showIntro && btnStartClicked(x, y)) { objs.showIntro = false; objs.continueGame() }
                else if(objs.showResult && btnStartClicked(x, y)) { objs.showResult = false; objs.startAgain() }
                else { objs.movePlayerTo(x, y, width, height) }
            }
        }

        return true
    }


    fun btnStartClicked(x: Float, y: Float): Boolean {
        return x >= 0.4*width && x <= 0.6*width && y >= 0.7*height && y <= 0.8*height
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        thread.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true;
        thread.running = false;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (e :InterruptedException) {
            }
        }
    }

    fun showFPS(canvas: Canvas) {
        if (canvas != null) {
            val paint = Paint();
            paint.setARGB(255, 255, 255, 255)
            paint.setColor(Color.RED)
            paint.setTextSize(36F)
            canvas.drawText(
                "Time(s): $count, FPS: $frames, skipped: $skipped",
                5 * this.getWidth() / 8F - 30, 30F, paint)
        }
    }
}