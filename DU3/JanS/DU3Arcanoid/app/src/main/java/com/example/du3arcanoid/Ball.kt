package com.example.du3arcanoid

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaPlayer
import kotlin.math.abs

class Ball(context: Context, val w: Int, val h: Int, val mPaint: Paint) {
    var hitSound = MediaPlayer.create(context, R.raw.hit)

    var x = w/2
    var y = h/2
    var prevX = 0
    var prevY = 0
    val r = 25

    var speed = 30  // todo speed++
    var dx = 0
    var dy = -speed

    fun update(p: Player) {
        val prevDX = dx
        val prevDY = dy

        val b = hitBrick()
        if (b != null) {
            if (prevY >= b.bottom()) { // prisla zdola
                dy = -dy
            } else if (prevY <= b.top()){ // zhora
                dy = -dy
            } else if (prevX <= b.left()){ // zlava
                dx = -dx
            } else if (prevX >= b.right()){ // zprava
                dx = -dx
            }

            log("hit")
            b.life /= 2
            if (b.life < 100) {
                Playground.bricks.remove(b)
            }
        }

        else if (hitPlayer(p)) {
            val per = (x - p.x) / p.w.toFloat() // kde dopadla
            if (x < p.x+p.w/2) {    // trafila lavu stranu
                dx = (-dy * 0.5 - per/2).toInt()
            } else {   // trafila pravu stranu
                dx = (-dy * (1 - per*2)).toInt()
            }
            dy = -dy
        }

        else {
            // kontrola steny todo
            if (left() <= 0) dx = -dx
            if (top() <= 0) dy = -dy
            if (right() >= w) dx = -dx
            if (bottom() >= h) {
                Playground.run = false
                PlaygroundActivity.resultTxt = "YOU LOSE"
                PlaygroundActivity.detailTxt = "Ball fell out"
                return
            }
        }

        if (PlaygroundActivity.sounds && abs(prevDX-dx) != 0 || abs(prevDY-dy) != 0) {
            hitSound.start() // ak sa zmenil smer a su zapnute zvuky, zahraj zvuk
        }

        prevX = x
        prevY = y

        x += dx
        y += dy
    }

    // todo test
//    fun hitPlayer(p: Player) = abs(bottom() - p.y) <= abs(dy) && p.x <= x && x <= p.x + p.w
    fun hitPlayer(p: Player) = p.y <= bottom() && bottom() <= p.y + p.h &&
                                p.x <= x && x <= p.x + p.w

    fun hitBrick() = Playground.bricks.find { checkCollision(it) }

    fun checkCollision(b: Brick): Boolean { // todo

        return b.getRect().contains(left(), top()) ||
               b.getRect().contains(right(), top()) ||
               b.getRect().contains(left(), bottom()) ||
               b.getRect().contains(right(), bottom())


//        x < block.x + block.size &&
//        x + size > block.x &&
//        y < block.y + block.size &&
//        y + size > block.y


//            if(abs(x-r - b.x+b.w) <= abs(dx) && b.y <= y && y <= b.y+b.h) {
//                dx = -dx;
//                log("pravo")
//                Playground.run = false
//                return true
//            }

//            if(abs(top() - b.bottom()) <= abs(dy) && b.left() <= x && x <= b.right()) {
//            if(b.top() <= top() && top() <= b.bottom() &&
//                    b.left() <= x && x <= b.right()) {
//                dy = -dy;
//                log("dole")
//                return true
//            }

//            if(abs(right() - b.left()) <= abs(dx) && b.top() <= y && y <= b.bottom()) {
//                dx = -dx;
//                log("lavo");
//                return true
//            }

//            if(y+r >= b.y && b.x <= x && x <= b.x+b.w) {
//            if (abs(bottom() - b.top()) <= abs(dy) && b.left() <= x && x <= b.right()) {
//            if (bottom() >= b.top() && bottom() <= b.bottom() && b.left() <= x && x <= b.right()) {
//                dy = -dy;
//                log("hore");
//                return true
//            }
//        return false
    }

    fun left() = x - r
    fun right() = x + r
    fun top() = y - r
    fun bottom() = y + r

    fun centerGravityTo(p: Player) {
        x = p.x + p.w/2
        y = p.y - r - 200
    }

    fun draw(canvas: Canvas) {
        canvas.drawCircle(x.toFloat(), y.toFloat(), r.toFloat(), mPaint)
    }
}