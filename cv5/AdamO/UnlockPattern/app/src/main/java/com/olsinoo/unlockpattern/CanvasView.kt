package com.olsinoo.unlockpattern

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.canvas_view.*
import kotlinx.android.synthetic.main.canvas_view.view.*
import kotlin.math.abs

// number of dots per row and column
var N = 3
// list of drawn lines
val listOfLines = mutableListOf<List<Float>>()
// current X,Y
var myX = 0f
var myY = 0f
// X,Y of the beginning of line
var startX = 0f
var startY = 0f
// true if i'm currently drawing pattern (I started on a dot), else false
var drawing = false
// stores the row and col of used dots
var visitedDots = mutableListOf<Pair<Int, Int>>()

class CanvasView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    // Paints
    private val innerCircPaint = Paint()
    private val outerCircPaint = Paint()
    private val paint = Paint()
    // diameter of outer circle of one dot
    private var circSize = (width - (width / (N - 1))) / N
    // size of outer circle and inner circle of dots
    private var outerCircSize = circSize / 2
    private var innerCircSize = circSize / 8
    // padding from the top border
    private var topPadding = (height / (N+1)/2).toFloat()

    override fun onDraw(canvas: Canvas) {
        innerCircPaint.color = Color.WHITE
        innerCircPaint.strokeWidth = 5f
        innerCircPaint.style = Paint.Style.FILL
        outerCircPaint.color = Color.WHITE
        outerCircPaint.strokeWidth = 1f
        outerCircPaint.style = Paint.Style.STROKE
//        var row = 0f
//        var col = 0f

        circSize = (width - (width / (N - 1))) / N
        outerCircSize = circSize / 2
        innerCircSize = circSize / 8
        topPadding = (height / (N + 1) / 2).toFloat()
        super.onDraw(canvas)
        for (i in 0 until N) {
            for (j in 0 until N) {
                canvas.drawCircle(
                    (width * (j + 1) / (N + 1)).toFloat(),
                    topPadding + (width * (i + 1) / (N + 1)).toFloat(),
                    innerCircSize.toFloat(),
                    innerCircPaint
                )
                canvas.drawCircle(
                    (width * (j + 1) / (N + 1)).toFloat(),
                    topPadding + (width * (i + 1) / (N + 1)).toFloat(),
                    outerCircSize.toFloat(),
                    outerCircPaint
                )
            }
        }
        if (!drawing && !isPatternCorrect()) {
            paint.color = Color.argb(120, 255,0,0)
        } else {
            paint.color = Color.argb(120, 255, 255, 255)
        }
        paint.strokeWidth = 20F
        // draw line when touching screen
        if (drawing) {
            if (listOfLines.size == 0) {
                canvas.drawLine(startX, startY, myX, myY, paint)
            } else {
                canvas.drawLine(
                    listOfLines[listOfLines.size - 1][2],
                    listOfLines[listOfLines.size - 1][3],
                    myX,
                    myY,
                    paint
                )
            }
        }

        // draw correct lines
        for (i in 0 until listOfLines.size) {
            canvas.drawLine(listOfLines[i][0],listOfLines[i][1],listOfLines[i][2], listOfLines[i][3], paint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "---- START ----")
                listOfLines.clear()
                visitedDots.clear()
                startX = event.x
                startY = event.y
//                Log.d(TAG, "TOUCH DOWN : ${isOnDot(eventX = event.x, eventY = event.y)}")
                if (isOnDot(eventX = event.x, eventY = event.y)) {
                    Log.d(TAG, "draw DOWN: $drawing")
                    drawing = true
                }
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {
                if (drawing) {
                    val x = startX
                    val y = startY
                    if (isOnDot(myX, myY)) {
                        listOfLines.add(listOf(x, y, myX, myY))
                    } else {
                        myX = event.x
                        myY = event.y
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                Log.d(TAG, "---- END ----")
                drawing = false
                invalidate()
            }
        }
//        Log.d(TAG, "draw: $drawing")
        return true
    }

    private fun isOnDot(eventX : Float, eventY : Float) : Boolean {
        // sorry for this part
        // it's not... nice looking...

        for (i in 0 until N) {
            for (j in 0 until N) {
                if ((eventX >= ((width * (j + 1)/(N + 1)).toFloat() - outerCircSize.toFloat())) &&
                    (eventX <= ((width * (j + 1)/(N + 1)).toFloat() + outerCircSize.toFloat())) &&
                    (eventY >= (topPadding + (width * (i + 1)/(N + 1)).toFloat() - outerCircSize.toFloat())) &&
                    (eventY <= (topPadding + (width * (i + 1)/(N + 1)).toFloat() + outerCircSize.toFloat()))) {
                    if (startX != (width * (j + 1)/(N + 1)).toFloat() ||
                        startY != (topPadding + (width * (i + 1)/(N + 1)).toFloat())) {
                        if (!visitedDots.contains(Pair(i,j))) {
                            myX = (width * (j + 1) / (N + 1)).toFloat()
                            myY = topPadding + (width * (i + 1) / (N + 1)).toFloat()
                            startX = (width * (j + 1) / (N + 1)).toFloat()
                            startY = topPadding + (width * (i + 1) / (N + 1)).toFloat()
                            Log.d(TAG, "visited: $visitedDots")
                            if (visitedDots.size >= 1) {
//                                val dotBefore = visitedDots[visitedDots.size - 1]
//                                if (dotBefore.first == i) {
//                                    val nums =
//                                        if (j > dotBefore.second) { Pair(dotBefore.second, j) }
//                                        else { Pair(j, dotBefore.second) }
//                                    Log.d(TAG, "row $nums")
//                                    for (col in nums.first + 1 until nums.second) {
//                                        Log.d(TAG, "adding col: $col $j")
//                                        visitedDots.add(Pair(i, col))
//                                    }
//                                } else if (dotBefore.second == j) {
//                                    val nums =
//                                        if (i > dotBefore.first) { Pair(dotBefore.first, i) }
//                                        else { Pair(i, dotBefore.first) }
//                                    Log.d(TAG, "col $nums")
//                                    for (row in nums.first + 1 until nums.second) {
//                                        Log.d(TAG, "adding row: $row $j")
//                                        visitedDots.add(Pair(row, j))
//                                    }
//                                }
                                val dotBefore = visitedDots[visitedDots.size - 1]
                                val colNums =
                                    if (j > dotBefore.second) { Pair(dotBefore.second, j) }
                                    else { Pair(j, dotBefore.second) }
                                val rowNums =
                                    if (i > dotBefore.first) { Pair(dotBefore.first, i) }
                                    else { Pair(i, dotBefore.first) }
                                Log.d(TAG, "$i !|! $j")
                                for (row in rowNums.first..rowNums.second) {
                                    for (col in colNums.first..colNums.second) {
                                        Log.d(TAG, "$row | $col")
                                        if (dotBefore.first == i) {
                                            if (row == i && col != j) {
                                                Log.d(TAG, "adding col")
                                                visitedDots.add(Pair(row, col))
                                            }
                                        } else if (dotBefore.second == j) {
                                            if (col == j && row != i) {
                                                Log.d(TAG, "adding row")
                                                visitedDots.add(Pair(row, col))
                                            }
                                        } else {
                                            if (row != i && row != dotBefore.first &&
                                                col != j && col != dotBefore.second &&
                                                (abs(i - dotBefore.first) == abs(j - dotBefore.second))) {
                                                if (i == j && dotBefore.first == dotBefore.second) {
                                                    if (row == col) { visitedDots.add(Pair(row, col))
                                                    }
                                                } else {
//                                                    Log.d(TAG, "adding rowcol")
                                                    visitedDots.add(Pair(row, col))
                                                }
                                            }
                                        }
                                    }
                                }

                            }
                            visitedDots.add(Pair(i, j))
                            Log.d(TAG, "visitedAdded: $visitedDots")
                            visitedDots = visitedDots.toSet().toMutableList()
                            Log.d(TAG, "visitedAdded: $visitedDots")
                            return true
                        }
                    }
                } } }
        return false
    }
}

// pattern is correct when it has at least 4 connected dots
fun isPatternCorrect() : Boolean {
    return visitedDots.toSet().size >= 4
}
