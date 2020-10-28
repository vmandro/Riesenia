package com.example.viazaniesnurok

import android.content.Context
import android.view.MotionEvent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import kotlin.math.pow
import kotlin.math.sqrt


class CanvasView(internal var context: Context, attrs: AttributeSet) : View(context, attrs) {
    //pocet snurok na 1 strane
    private val N = 4
    //medzera medzi snurkami na vysku(na sirku sa to pocita 1. rad do 1/3 sirky a 2. rad do 2/3 sirky canvasu)
    private val hMargin = 200f

    private var circles = emptyList<Circle>().toMutableList()
    private var connectedCircles = emptyList<Circle>().toMutableList()
    private var savedConnections = emptyList<MutableList<Circle>>().toMutableList()


    init {
        for (i in -N .. N) {
            if(i == 0) continue

            val c = Circle(i, hMargin)
            circles.add(c)
        }

    }

    override protected fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for (c in circles) {
            c.draw(canvas)
        }

        val paint = Paint()

        paint.setColor(Color.BLACK)
        paint.strokeWidth = 30f

        var length = 0f
        for (i in 0 until connectedCircles.size - 1) {
            length += sqrt((connectedCircles[i].x - connectedCircles[i+1].x).pow(2) + (connectedCircles[i].y - connectedCircles[i+1].y).pow(2))
            canvas.drawLine(connectedCircles[i].x, connectedCircles[i].y, connectedCircles[i+1].x, connectedCircles[i+1].y,paint)
        }

        if (connectedCircles.size == circles.size) {
            canvas.drawLine(connectedCircles[0].x, connectedCircles[0].y, connectedCircles[connectedCircles.lastIndex].x, connectedCircles[connectedCircles.lastIndex].y,paint)
            length += sqrt((connectedCircles[0].x - connectedCircles[0].x).pow(2) + (connectedCircles[connectedCircles.lastIndex].y - connectedCircles[connectedCircles.lastIndex].y).pow(2))

            for(c in circles) {
                if(c.isFree()) {
                    connectedCircles.add(c)
                    break
                }
            }
            var infoText = rootView.findViewById(R.id.infoText) as TextView
            if(isInSaved()) {
                infoText.setText("Already in saved")
            }
            else {
                infoText.setText("Not in saved")
            }
        }

        updateCrossCounts()

        var lengthText = rootView.findViewById(R.id.lengthText) as TextView
        lengthText.setText("Length: $length")
    }

    fun isInSaved(): Boolean {
        //vytvorenie 1 listu
        var index1 = 0
        for(c in connectedCircles) {
            //hladame dierku s cislom 1, predpokladam ze mame nejake dierky zobrazene, ze N > 0
            if(c.N == 1) {
                break
            }
            index1++
        }
        var list1 = emptyList<Circle>().toMutableList()
        for(i in index1 until connectedCircles.size) {
            list1.add(connectedCircles[i])
        }
        for(i in 0 until index1) {
            list1.add(connectedCircles[i])
        }

        for (savedC in savedConnections) {
            //vytvorenie 2 listu
            var index1 = 0
            for(c in savedC) {
                if(c.N == 1) {
                    break
                }
                index1++
            }
            var list2 = emptyList<Circle>().toMutableList()
            for(i in index1 until savedC.size) {
                list2.add(savedC[i])
            }
            for(i in 0 until index1) {
                list2.add(savedC[i])
            }

            var list3  = emptyList<Circle>().toMutableList()
            for(i in index1 downTo 0) {
                list3.add(savedC[i])
            }
            for(i in savedC.size-1 downTo index1+1) {
                list3.add(savedC[i])
            }

            for(i in 0 until list1.size) {
                if(list1[i].N != list2[i].N)
                    break
                if(i == list1.size-1) return true
            }

            for(i in 0 until list1.size) {
                if(list1[i].N != list3[i].N)
                    break
                if(i == list1.size-1) return true
            }

        }

        return false
    }

    fun onOtherSide(c1: Circle, c2: Circle) : Boolean {
        if((c1.N < 0 && c2.N > 0) || (c1.N > 0 && c2.N < 0))
            return true
        return false
    }


    fun updateCrossCounts() {
        var count0 = 0
        var count1 = 0
        var count2 = 0

        for (i in 0 until connectedCircles.size) {
            var count = 0
            if(i == 0) {

                if(i+1 < connectedCircles.size && onOtherSide(connectedCircles[i], connectedCircles[i+1])) {
                    count++
                }

                //ak su vsetky spojene
                if(connectedCircles.size == circles.size && onOtherSide(connectedCircles[i], connectedCircles[connectedCircles.lastIndex])) {
                    count++
                }


            } else if(i == circles.size - 1) {
                if(onOtherSide(connectedCircles[i], connectedCircles[0])) {
                    count++
                }
                if(onOtherSide(connectedCircles[i], connectedCircles[i-1])) {
                    count++
                }

            } else {
                if(i+1 < connectedCircles.size && onOtherSide(connectedCircles[i], connectedCircles[i+1])) {
                    count++
                }
                if(onOtherSide(connectedCircles[i], connectedCircles[i-1])) {
                    count++
                }
            }

            when(count) {
                0 -> count0++
                1 -> count1++
                2 -> count2++
            }


        }

        var crossesText = rootView.findViewById(R.id.crossesText) as TextView
        crossesText.setText("0-cross: $count0\n1-cross: $count1\n2-cross: $count2")

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                for (c in circles) {
                    if(c.isIn(x, y) && c.isFree()) {
                        connectedCircles.add(c)
                        c.connect()
                        invalidate();
                    }
                }
            }
        }
        return true
    }

    fun clearCanvas() {
        circles.clear()
        for (i in -N .. N) {
            if(i == 0) continue

            val c = Circle(i, hMargin)
            circles.add(c)
        }

        savedConnections.add(connectedCircles)
        connectedCircles = emptyList<Circle>().toMutableList()

        var infoText = rootView.findViewById(R.id.infoText) as TextView
        infoText.setText("")

        invalidate()
    }
}