package com.example.breakball

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat

class Level(val ctx: Context, val pxWidth: Int, val pxHeight: Int, val level: Int) : View(ctx) {
    var SPEED = 2
    lateinit var extraCanvas: Canvas
    lateinit var extraBitmap: Bitmap
    val backgroundColor : Int

    var levelWidthInSquares: Int
    var levelHeightInSquares: Int
    var squareSize: Int

    var firstBall: Ball?
    var littleDotColor: Int // For helping dot
    var firstBallX: Int = 0
    var levelBottomY: Int = 0

    var areBallsInMotion: Boolean
    var frameCounter: Int
    var countOfBalls: Int

    var squares : ArrayList<Square>
    var balls : ArrayList<Ball>
    var ballsInMotion : ArrayList<Ball>

    var numberOfTouches: Int
    var moves: Int

    init {
        backgroundColor = ResourcesCompat.getColor(resources, R.color.levelBackground, null)
        littleDotColor = ResourcesCompat.getColor(resources, R.color.little_dot_color, null)

        // For level settings
        levelWidthInSquares = loadInteger("level_width_$level")
        levelHeightInSquares = loadInteger("level_height_$level")
        squareSize = pxWidth / levelWidthInSquares
        firstBall = null
        firstBallX = pxWidth / 2
        levelBottomY = levelHeightInSquares * squareSize

        // For game logic
        areBallsInMotion = false
        frameCounter = 0
        countOfBalls = loadInteger("count_of_balls_level_$level")

        // For drawing
        squares = arrayListOf()
        balls = arrayListOf()
        ballsInMotion = arrayListOf()

        // For game score
        numberOfTouches = 0
        moves = 0

        loadLevel(level)
        initBalls()
    }

    fun loadInteger(name: String): Int {
        val id = resources.getIdentifier(name, "integer", ctx.getPackageName())
        return resources.getInteger(id)
    }


    public override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        println("ON_SIZE")
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if(::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(backgroundColor)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if(!areBallsInMotion) {
            // Set direction of movement for balls
            val vX = (event.x - firstBallX) / squareSize
            val evY = if((event.y - levelBottomY) < -10) (event.y - levelBottomY) else -10F      // Chcem sa vyhnut kliku pod urovnou gulicky
            val vY = (evY / squareSize)

            println("CLICK X = " + event.x + ", Y = " + event.y)

            setVectorToBalls(vX, vY)
            setNewXOriginForBalls()
            var ball = getBallFromBalls()!!
            firstBall = ball
            ballsInMotion.add(ball)
            areBallsInMotion = true
            moves++
        }

        return super.onTouchEvent(event)
    }

    public override fun onDraw(canvas: Canvas) {
        //println("ON_DRAW")
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null)

        // Game Logic
        if(frameCounter % SPEED == 0) {
            if (areBallsInMotion) {
                //println("Balls are in motion! Moving = " + ballsInMotion.size + ", not = " + balls.size)
                addBallToMotion()
                ballsMoveAndColision()

                areBallsInMotion = updateAreBallsInMotion()
            }
            frameCounter = 0
        }
        frameCounter++   // Slower movement

        for(s in squares) {
            s.draw(canvas)
        }

        for(b in ballsInMotion) {
            b.draw(canvas)
        }

        // Little Dot
        val paint = Paint()
        paint.color = littleDotColor
        canvas.drawCircle(firstBallX.toFloat(), levelBottomY.toFloat(), (squareSize / 10).toFloat(), paint)

        this.background = BitmapDrawable(resources, extraBitmap)
    }

    fun loadLevel(level: Int) {
        val levelId = resources.getIdentifier("level_$level", "array", ctx.getPackageName())
        val map = resources.getStringArray(levelId)
        var y = 0
        for(line in map) {
            var x = 0
            for(ch in line) {
                if(ch != '_') {
                    squares.add(Square(x, y, ch, squareSize, resources, ctx))
                }
                x++
            }
            y++
        }
    }

    fun initBalls() {
        val ballRadius = squareSize / 6
        for(i in 0 until countOfBalls) {
            balls.add(Ball(firstBallX, levelBottomY, ballRadius, resources, ctx))
        }
    }

    fun getBallFromBalls() : Ball? {
        if(balls.any()) {
            return balls.removeAt(0)
        }
        return null
    }

    fun addBallToMotion() {
        val ball = getBallFromBalls()
        if(ball != null) {
            ballsInMotion.add(ball)
        }
    }

    fun ballsMoveAndColision() {
        var ballsToStopMove = arrayListOf<Ball>()
        var squaresToRemove = arrayListOf<Square>()
        for(ball in ballsInMotion) {
            ball.move()

            // Left or Right side of screen
            if(ball.leftSide() <= 0 || ball.rightSide() >= pxWidth) {
                ball.getVector().invertX()
            }

            // Top side of screen
            if(ball.topSide() <= 0) {
                ball.getVector().invertY()
            }

            // Bottom side of screen
            // Stop move
            if(ball.bottomSide() >= (levelBottomY + squareSize)) {      //
                ball.setVector(Vector(0F, 0F))
                ballsToStopMove.add(ball)

                if(ball.equals(firstBall)) {
                    firstBallX = ball.getX().toInt()
                    firstBall = null
                }
            }

            for(square in squares) {
                var touch = false

                // Looks ugly and stupid, but helps to avoid glitches in ball movement
                if(touch.not() && square.touchFromLeft(ball)) {
                    ball.getVector().invertX()
                    touch = true
                }
                if(touch.not() && square.touchFromRight(ball)) {
                    ball.getVector().invertX()
                    touch = true
                }
                if(touch.not() && square.touchFromTop(ball)) {
                    ball.getVector().invertY()
                    touch = true
                }
                if(touch.not() && square.touchFromBottom(ball)) {
                    ball.getVector().invertY()
                    touch = true
                }

                if(touch) {
                    numberOfTouches++
                    square.minusFromTouchesToDestroy()
                    if(square.canBeDestroy()) {
                        squaresToRemove.add(square)
                    }
                }
            }
            squares.removeAll(squaresToRemove)
        }
        ballsInMotion.removeAll(ballsToStopMove)
        balls.addAll(ballsToStopMove)
    }

    fun setVectorToBalls(vX: Float, vY: Float) {
        for(b in balls) {
            val v = Vector(vX * 3, vY * 3)
            b.setVector(v)
        }
    }

    fun updateAreBallsInMotion() : Boolean {
        return ballsInMotion.isEmpty().not()
    }

    fun setNewXOriginForBalls() {
        for(ball in balls) {
            ball.setX(firstBallX.toFloat())
            ball.setY(levelBottomY.toFloat())
        }
    }

    fun resetBalls() {
        balls.addAll(ballsInMotion)
        ballsInMotion.clear()

        areBallsInMotion = false
    }

    fun isLevelClear() : Boolean {
        if(squares.isEmpty()) return true

        for(square in squares) {
            if(square.canBeDestroy().not()) return false
        }
        return true
    }
}
