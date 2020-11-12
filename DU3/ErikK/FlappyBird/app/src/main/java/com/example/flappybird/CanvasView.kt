package com.example.flappybird

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.drawable.toBitmap


class CanvasView(internal var context: Context, attrs: AttributeSet) : View(context, attrs) {
    private var PRESTART = 0
    private var GAME = 1
    private var LOST = 2
    private lateinit var mainActivity: MainActivity
    private lateinit var preStartThread : Thread
    private lateinit var gameThread : Thread
    private var trubkyH = listOf<Trubka>().toMutableList()
    private var trubkyD = listOf<Trubka>().toMutableList()
    private lateinit var paint : Paint
    private var rnd = java.util.Random()
    private var levitateUp = true
    private var paused = false
    private var tapped = false
    private var canCount = true
    private var gameState = PRESTART
    private var birdPhase = 0
    private var levitatePhase = 0
    private var floorLeft = 0
    private var y = 500
    private var h = 50
    private var trubkyCount = 0
    private var timeCliced : Long = 0
    private var otvor = 20

    init {
        paint = Paint()
        mainActivity = context as MainActivity
        genereteTrubky()
        makePreStartThread()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (gameState == PRESTART) {
            gameState = GAME
            y += levitatePhase
            levitatePhase = 0
            makeGameThread()
        }else if (gameState == GAME) {
            tapped = true
            timeCliced = System.currentTimeMillis()
            if (event.action == MotionEvent.ACTION_DOWN) {
                if (event.x > 5 * width / 100 && event.x < 19 * width / 100 && event.y > 5 * height / 100 && event.y < 14 * height / 100) {
                    paused = !paused
                }
            }
        } else if (event.action == MotionEvent.ACTION_UP) {
            if (event.x > 30 * width / 100 && event.x < 70 * width / 100 && event.y > 70 * height / 100 && event.y < 82 * height / 100) {
                gameState = PRESTART
                y = 500
                trubkyCount = 0
                genereteTrubky()
                makePreStartThread()
            }
        }
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawBitMap(canvas, getBitMap(R.drawable.background), 0,0, width, height)
        val bmp : Bitmap = when (birdPhase){
            0 -> getBitMap(R.drawable.bird0)
            1 -> getBitMap(R.drawable.bird1)
            else -> getBitMap(R.drawable.bird2)
        }
        drawBitMap(canvas, bmp, 21 * width / 100,(y - h + levitatePhase) * height / 1000,36 * width / 100, (y + levitatePhase) * height / 1000)
        if (gameState == PRESTART){
            preStartDraw(canvas)
        } else if (gameState == GAME){
            gameDraw(canvas)
        } else {
            lostDraw(canvas)
        }
        drawBitMap(canvas, getBitMap(R.drawable.floor), (floorLeft) * width / 1000,82 * height / 100,(floorLeft + 1000) * width / 1000, height)
        drawBitMap(canvas, getBitMap(R.drawable.floor), (floorLeft + 1000) * width / 1000,82 * height / 100,(floorLeft + 2000) * width / 1000, height)
    }

    private fun preStartDraw(canvas: Canvas){
        drawBitMap(canvas,  getBitMap(R.drawable.get_ready), 2 * width / 10,3 * height / 10,8 * width / 10, 4 * height / 10)
        drawBitMap(canvas, getBitMap(R.drawable.tap_to_start), 3 * width / 10,4 * height / 10,7 * width / 10, 6 * height / 10)
        scoreDraw(canvas, trubkyCount, 46, 5, 54, 11, true)
    }

    private fun gameDraw(canvas: Canvas){
        trubkyDraw(canvas)
        val bmp : Bitmap = if (paused){
            getBitMap(R.drawable.resume)
        } else {
            getBitMap(R.drawable.pause)
        }
        drawBitMap(canvas, bmp,5 * width / 100, 5 * height / 100, 16 * width / 100, 11 * height / 100)
        scoreDraw(canvas, trubkyCount, 46, 5, 54, 11, true)
    }

    private fun lostDraw(canvas: Canvas){
        trubkyDraw(canvas)
        drawBitMap(canvas, getBitMap(R.drawable.game_over), 2 * width / 10,25 * height / 100,8 * width / 10, 35 * height / 100)
        drawBitMap(canvas, getBitMap(R.drawable.board), 10 * width / 100,40 * height / 100,90 * width / 100, 65 * height / 100)
        drawBitMap(canvas, getBitMap(R.drawable.play), 30 * width / 100,70 * height / 100,70 * width / 100, 82 * height / 100)
        scoreDraw(canvas, trubkyCount, 76, 48, 80, 52, false)
        if (trubkyCount >= mainActivity.gHighScore() && trubkyCount != 0){
            drawBitMap(canvas, getBitMap(R.drawable.new_high), 57 * width / 100,528 * height / 1000,68 * width / 100, 555 * height / 1000)
            mainActivity.sHighScore(trubkyCount)
        }
        scoreDraw(canvas, mainActivity.gHighScore(), 76, 57, 80, 61, false)
        val bmp: Bitmap? = when (trubkyCount) {
            in 0..9 -> null
            in 10..19 -> getBitMap(R.drawable.bronse)
            in 20..29 -> getBitMap(R.drawable.silver)
            in 30..39 -> getBitMap(R.drawable.gold)
            else  -> getBitMap(R.drawable.platinum)
        }
        if (bmp != null){
            drawBitMap(canvas, bmp, 18 * width / 100,48 * height / 100,37 * width / 100, 60 * height / 100)
        }
    }

    private fun trubkyDraw(canvas: Canvas){
        val trubkaH = getBitMap(R.drawable.top_pipe)
        val trubkaD = getBitMap(R.drawable.bottom_pipe)
        for (i in 0..3){
            drawBitMap(canvas, trubkaH, trubkyH[i].x1 * width / 1000, trubkyH[i].y1 * height / 100, trubkyH[i].x2 * width / 1000, trubkyH[i].y2 * height / 100)
            drawBitMap(canvas, trubkaD, trubkyD[i].x1 * width / 1000, trubkyD[i].y1 * height / 100, trubkyD[i].x2 * width / 1000, trubkyD[i].y2 * height / 100)
        }
    }

    private fun scoreDraw(canvas: Canvas, score : Int, x1 : Int, y1 : Int, x2 : Int, y2 : Int, center : Boolean){
        if (score == 0){
            drawBitMap(canvas, getBitMap(R.drawable.n0), x1 * width / 100, y1 * height / 100, x2 * width / 100, y2 * height / 100)
        } else {
            val n = Math.log10(score.toDouble())
            var x : Int
            if (center){
                x = (x2 + 4 * n).toInt()
            } else {
                x = x2
            }
            var cis = score
            while (cis != 0) {
                val num = cis % 10
                cis /= 10
                val bmp: Bitmap = when (num) {
                    0 -> getBitMap(R.drawable.n0)
                    1 -> getBitMap(R.drawable.n1)
                    2 -> getBitMap(R.drawable.n2)
                    3 -> getBitMap(R.drawable.n3)
                    4 -> getBitMap(R.drawable.n4)
                    5 -> getBitMap(R.drawable.n5)
                    6 -> getBitMap(R.drawable.n6)
                    7 -> getBitMap(R.drawable.n7)
                    8 -> getBitMap(R.drawable.n8)
                    else -> getBitMap(R.drawable.n9)
                }
                drawBitMap(canvas, bmp, (x - (x2 - x1)) * width / 100, y1 * height / 100, x * width / 100, y2 * height / 100)
                x -= (x2 - x1)
            }
        }
    }

    private fun makePreStartThread(){
        var ind = 0
        preStartThread = object : Thread() {
            override fun run() {
                while (gameState == PRESTART) {
                    try {
                        if (ind == 9) {
                            birdPhase = (birdPhase + 1) % 3
                        }
                        ind = (ind + 1) % 10
                        if (levitateUp) {
                            levitatePhase++
                            if (levitatePhase == 20) {
                                levitateUp = false
                            }
                        } else {
                            levitatePhase--
                            if (levitatePhase == -20) {
                                levitateUp = true
                            }
                        }
                        floorLeft -= 3
                        if (floorLeft == -999){
                            floorLeft = 0
                        }
                        sleep(10)
                        postInvalidate()
                    } catch (e: InterruptedException) {}
                }
            }
        }
        preStartThread.start()
    }

    private fun makeGameThread(){
        var ind = 0
        gameThread = object : Thread() {
            override fun run() {
                while (gameState == GAME) {
                    if (!paused) {
                        try {
                            if (ind == 9){
                                birdPhase = (birdPhase + 1) % 3
                            }
                            ind = (ind + 1) % 10
                            if (System.currentTimeMillis() - timeCliced > 100){
                                tapped = false
                            }
                            if (tapped) {
                                y = Math.max(y - 3, h)
                            } else {
                                y = Math.min(y + 3, 820)
                            }
                            floorLeft -= 3
                            if (floorLeft == -999) {
                                floorLeft = 0
                            }
                            for (i in 0..3){
                                trubkyH[i].x1 -= 3
                                trubkyH[i].x2 -= 3
                                trubkyD[i].x1 -= 3
                                trubkyD[i].x2 -= 3
                            }
                            if (trubkyD[0].x2 * width / 1000 <= 21 * width / 100 && canCount){
                                trubkyCount++
                                canCount = false
                            }
                            if (trubkyD[0].x2 < 0){
                                trubkyH.removeAt(0)
                                trubkyD.removeAt(0)
                                val kon = rnd.nextInt(43) + 10
                                trubkyH.add(Trubka(trubkyH[2].x1 + 620, kon - 60, trubkyH[2].x2 + 620, kon))
                                trubkyD.add(Trubka(trubkyD[2].x1 + 620, kon + otvor, trubkyD[2].x2 + 620, kon + otvor + 60))
                                canCount = true
                            }
                            if (y == 820 || checkCollision()) {
                                gameState = LOST
                            }
                            sleep(10)
                            postInvalidate()
                        } catch (e: InterruptedException) {}
                    }
                }
            }
        }
        gameThread.start()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun getBitMap(id : Int) : Bitmap = resources.getDrawable(id).toBitmap()

    private fun drawBitMap(canvas: Canvas, bmp : Bitmap, x1 : Int, y1 : Int, x2 : Int, y2 : Int){
        canvas.drawBitmap(bmp, null, Rect(x1, y1, x2, y2), paint)
    }

    private fun checkCollision() : Boolean{
        for (i in 0..3){
            if ((trubkyH[i].x1 * width / 1000 > 21 * width / 100 && trubkyH[i].x1 * width / 1000 < 36 * width / 100 ||
                        trubkyH[i].x2 * width / 1000 > 21 * width / 100 && trubkyH[i].x2 * width / 1000 < 36 * width / 100) &&
                (trubkyH[i].y2 * height / 100 >  (y - h) * height / 1000 || trubkyD[i].y1 * height / 100 <  y * height / 1000)){
                return true
            }
        }
        return false
    }

    private fun genereteTrubky(){
        trubkyH.clear()
        trubkyD.clear()
        for (i in 0..3){
            val kon = rnd.nextInt(43) + 10
            trubkyH.add(Trubka(1200 + i * 620, kon - 60, 1400 + i * 620, kon))
            trubkyD.add(Trubka(1200 + i * 620, kon + otvor, 1400 + i * 620, kon + otvor + 60))
        }
    }
}