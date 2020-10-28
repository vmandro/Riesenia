package com.example.snurky

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.graphics.drawable.toBitmap
import kotlinx.android.synthetic.main.activity_main.view.*


class CanvasView(internal var context: Context, attrs: AttributeSet) : View(context, attrs) {
    private lateinit var paint : Paint
    private lateinit var path : MutableList<Int>
    private lateinit var snurky : MutableList<Line>
    private lateinit var holes : MutableList<MutableList<Node>>
    private lateinit var saved : MutableList<MutableList<Int>>
    private var last : Node? = null
    private var first : Node? = null
    private var SIZE = 5
    private var length : Double = 0.0
    private var cross0 = 0
    private var cross1 = 0
    private var cross2 = 0
    private var complete = false

    init {
        path = listOf<Int>().toMutableList()
        snurky = listOf<Line>().toMutableList()
        saved = listOf<MutableList<Int>>().toMutableList()
        paint = Paint()
        paint.setColor(Color.BLACK)
        paint.setStrokeWidth(20f)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        holes = listOf<MutableList<Node>>().toMutableList()
        val size = width / 10
        val x = width / 3
        var y = height / 5 + size
        val step = ((5 * height / 6) - height / 4) / SIZE
        for (h in 0 until SIZE){
            var line = listOf<Node>().toMutableList()
            for (w in 1 until 3){
                canvas.drawBitmap(resources.getDrawable(R.drawable.circle).toBitmap(), null, Rect(x * w - size / 2, y, x * w + size / 2, y + size), paint)
                val id = (h + 1) * if (w == 1) 1 else -1
                val node = Node(id, x * w, y + size / 2, size)
                line.add(node)
            }
            y += step
            holes.add(line)
        }

        for (i in 0 until snurky.size){
            canvas.drawLine(snurky[i].x1, snurky[i].y1, snurky[i].x2, snurky[i].y2, paint)
        }

        Log.d("GAME", cross0.toString() + " " + cross1.toString() + " " + cross2.toString() + " " + length.toString())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            for (h in 0 until SIZE) {
                for (w in 0 until 2) {
                    if (distance(holes[h][w].x, holes[h][w].y, event.x.toInt(), event.y.toInt(), holes[h][w].radius)){
                        if (first == null) {
                            path.add(holes[h][w].id)
                            first = holes[h][w]
                            last = holes[h][w]
                        } else if (!path.contains(holes[h][w].id)){
                            snurky.add(Line(last!!.x.toFloat(), last!!.y.toFloat(), holes[h][w].x.toFloat(), holes[h][w].y.toFloat()))
                            length += Math.sqrt(distance2(last!!.x.toDouble(), last!!.y.toDouble(), holes[h][w].x.toDouble(), holes[h][w].y.toDouble()))
                            path.add(holes[h][w].id)
                            last = holes[h][w]
                        } else if (holes[h][w].id == first!!.id && path.size == 2 * SIZE && !complete){
                            snurky.add(Line(last!!.x.toFloat(), last!!.y.toFloat(), holes[h][w].x.toFloat(), holes[h][w].y.toFloat()))
                            length += Math.sqrt(distance2(last!!.x.toDouble(), last!!.y.toDouble(), holes[h][w].x.toDouble(), holes[h][w].y.toDouble()))
                            Toast.makeText(context, "laces are complete", Toast.LENGTH_SHORT).show()
                            complete = true
                        }
                    }
                }
            }
        }
        checkCross()
        invalidate()
        return true
    }

    fun changeSize(size : Int){
        SIZE = size
        clear()
        invalidate()
    }

    fun clear(){
        path = listOf<Int>().toMutableList()
        snurky = listOf<Line>().toMutableList()
        first = null
        last = null
        complete = false
        length = 0.0
        cross0 = 0
        cross1 = 0
        cross2 = 0
        invalidate()
    }

    fun save(){
        if (complete){
            if (path in saved){
                Toast.makeText(context, "laces are already saved", Toast.LENGTH_SHORT).show()
            } else {
                saved.add(path)
                Toast.makeText(context, "laces are being saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun distance(x1: Int, y1: Int, x2: Int, y2: Int, radius: Int) : Boolean =
        ((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)) < radius * radius

    private fun distance2(x1: Double, y1: Double, x2: Double, y2: Double) : Double =
        Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))

    private fun checkCross(){
        if (path.size <= 1){
            return
        }
        cross0 = 0
        cross1 = 0
        cross2 = 0
        for (i in 0 until path.size){
            if (i == 0){
                if (complete) {
                    if (path[path.size - 1] < 0 && path[1] < 0 && path[0] < 0 ||
                        path[path.size - 1] > 0 && path[1] > 0 && path[0] > 0
                    ) {
                        cross0++
                    } else if (path[path.size - 1] < 0 && path[1] < 0 && path[0] > 0 ||
                        path[path.size - 1] > 0 && path[1] > 0 && path[0] < 0
                    ) {
                        cross2++
                    } else {
                        cross1++
                    }
                }
            } else if (i == path.size - 1){
                if (complete) {
                    if (path[path.size - 2] < 0 && path[0] < 0 && path[path.size - 1] < 0 ||
                        path[path.size - 2] > 0 && path[0] > 0 && path[path.size - 1] > 0
                    ) {
                        cross0++
                    } else if (path[path.size - 2] < 0 && path[0] < 0 && path[path.size - 1] > 0 ||
                        path[path.size - 2] > 0 && path[0] > 0 && path[path.size - 1] < 0
                    ) {
                        cross2++
                    } else {
                        cross1++
                    }
                }
            } else {
                if (path[i-1] < 0 && path[i+1] < 0 && path[i] < 0 ||
                    path[i-1] > 0 && path[i+1] > 0 && path[i] > 0){
                    cross0++
                } else if (path[i-1] < 0 && path[i+1] < 0 && path[i] > 0 ||
                    path[i-1] > 0 && path[i+1] > 0 && path[i] < 0){
                    cross2++
                } else {
                    cross1++
                }
            }
        }
    }
}