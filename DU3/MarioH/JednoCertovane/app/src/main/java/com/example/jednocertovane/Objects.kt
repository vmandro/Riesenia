package com.example.jednocertovane

import android.content.Context
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import com.example.jednocertovane.canvasobjects.*
import java.util.*
import kotlin.concurrent.schedule

class Objects(internal var context: Context) {
    val N = 4 // number of lines to create
    val maxLives = 3 //number of max lives

    var score = Score(context)

    var stopped = true

    var guestGenerator = GuestGenerator(context, N)

    var canvasObjects = emptyList<CanvasObject>().toMutableList()
    var player = Player(context, N)
    var guests = emptyList<Guest>().toMutableList()
    var cups = emptyList<Cup>().toMutableList()

    var lives = Lives(context, maxLives)

    var showIntro = true
    var showResult = false
    var intro = Intro(context)
    var result = Result(context)

    init {
        canvasObjects.add(Background(context, N))
        for(i in 0 until N) {
            canvasObjects.add(Line(context, i, N))
        }

        canvasObjects.add(player)
        canvasObjects.add(lives)

        canvasObjects.add(score)


        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            override fun run() {
                if(!stopped) {
                    val guestsToAdd = guestGenerator.generateGuests(score.score, guests.size)
                    guests.addAll(guestsToAdd)
                    canvasObjects.addAll(guestsToAdd)

                    guests.forEach { it.move() }
                    cups.forEach { it.move() }

                    checkBrokenCups()
                    checkGuests()
                }

                mainHandler.postDelayed(this, 1000 / 50)
            }
        })

    }

    @Synchronized
    fun draw(canvas: Canvas) {
        if(showIntro) {
            intro.draw(canvas)
        }
        else if(showResult) {
            result.draw(canvas)
            score.drawResultScore(canvas)
        }
        else {
            val objects = canvasObjects.toMutableList()
            objects.forEach { it.draw(canvas) }
        }
    }

    fun movePlayerTo(x: Float, y: Float, width: Int, height: Int) {
        val barrelSize = height / (N + 1)
        val i = ((y - barrelSize/2) / barrelSize).toInt()

        if(i < 0 || i >= N) return

        if(x < (width * 0.8).toInt() - barrelSize || x > (width * 0.8).toInt() + barrelSize) return

        var fill = false
        if(x > (width * 0.8).toInt()) {
            fill = true
        }
        if(player.moveTo(i, fill)) {
            Timer().schedule(500) {
                addCup(i)
            }
        }
    }

    @Synchronized
    fun addCup(i: Int, backToPlayer: Boolean = false) {
        var cup = Cup(context, i, N)
        cups.add(cup)
        canvasObjects.add(cup)
    }

    @Synchronized
    fun checkBrokenCups() {
        cups.asReversed().forEach {
            if(it.isCatched(player.i)) {
                player.catch()
            }
            else if(it.isBroken()) {
                loseLife()
                Timer().schedule(250) {
                    it.useLess = true
                }
                return
            }
        }
    }

    fun checkGuests() {
        guests.forEach {
            if(it.isOnEnd()) {
                it.mad()
                loseLife()
            }
        }

        guests.forEach {
            g ->
            cups.forEach {
                c ->
                run {
                    if (!g.isDrinking && g.cups > 0 && inSameLine(g, c) && canDrink(g, c) && c.isFull()) {
                        drink(g, c)
                    }
                }
            }
        }

        var guestsToRemove = guests.filter { it.cups == 0 }
        score.addScore(guestsToRemove.sumBy { it.score })
        canvasObjects.removeAll(guestsToRemove)
        guests.removeAll(guestsToRemove)

        var cupsToRemove = cups.filter { it.useLess }
        canvasObjects.removeAll(cupsToRemove)
        cups.removeAll(cupsToRemove)

    }

    fun drink(g: Guest, c: Cup) {
        g.drink()
        c.drink(g.cups > 1)
    }

    fun inSameLine(g: Guest, c: Cup): Boolean = g.i == c.i

    fun canDrink(g: Guest, c: Cup): Boolean {
        val distance = c.x - g.x

        return distance <= -50 && distance >= -80
    }

    fun loseLife() {
        score.subScore(100)
        lives.loseLife()
        player.sad()

        stopGame()//hru zastavi na 1 sekundu nebudu sa generovat ani guesti

        if(lives.lives > 0) {
            Timer().schedule(1000) {
                clearGame() //odstrani guestov a pohare
                continueGame()
            }
        }
        else {
            showResult = true
        }
    }

    fun clearGame() {
        canvasObjects.removeAll(guests)
        canvasObjects.removeAll(cups)

        cups.clear()
        guests.clear()
    }

    fun stopGame() {
        stopped = true
    }

    fun continueGame() {
        stopped = false
    }

    fun startAgain() {
        clearGame()
        score.resetScore()
        lives.refill()
        continueGame()
    }



}