package com.example.jednocertovane

import android.graphics.Canvas

class GameThread(val gamePanel: GamePanel) : Thread() {
    var running = true
    val FRAME = 1000  // 1sec
    val FPS = 50  // 50 fps
    val FRAME_PERIOD = FRAME / FPS  // 50 fps

    override fun run() {
        var lastStatusTime = System.currentTimeMillis()
        var skippedInPeriod: Int
        var count = 0
        var framesInPeriod = 0
        val surfaceHolder = gamePanel.holder

        while (running) {
            var canvas: Canvas? = null
            try {
                canvas = surfaceHolder.lockCanvas()
                if (canvas == null)
                    break;
                synchronized(surfaceHolder) {
                    val startTime = System.currentTimeMillis()
                    skippedInPeriod = 0

                    gamePanel.drawObjects(canvas)
//                    gamePanel.showFPS(canvas)
                    var elapsedTime = System.currentTimeMillis() - startTime
                    framesInPeriod++
                    if (elapsedTime < FRAME_PERIOD) {
                        try {
                            Thread.sleep(FRAME_PERIOD - elapsedTime)
                        } catch (e: InterruptedException) {
                        }
                    }
                    while (elapsedTime > FRAME_PERIOD) {
                        elapsedTime -= FRAME_PERIOD.toLong()
                        skippedInPeriod++
                    }
                    if (System.currentTimeMillis() - lastStatusTime >= FRAME) {
                        gamePanel.count = ++count
                        gamePanel.frames = framesInPeriod
                        gamePanel.skipped = skippedInPeriod
                        lastStatusTime = System.currentTimeMillis()
                        framesInPeriod = 0
                    }
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas)
                }
            }
        }
    }
}