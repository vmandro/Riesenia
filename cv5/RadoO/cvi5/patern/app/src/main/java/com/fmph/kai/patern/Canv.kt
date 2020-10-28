package com.fmph.kai.patern

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.View
import android.util.AttributeSet
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_DOWN
import android.view.MotionEvent.ACTION_UP
import android.widget.Toast


class Canv(internal var ctx : Context,atr : AttributeSet): View(ctx,atr) {
    var w = 0
    var h = 0
    var pom = 0
    var KOD = emptyList<Int>().toMutableList()
    var ciara = emptyList<Float>().toMutableList()
    var body = emptyList<Bod>().toMutableList()
    var last = emptyList<Int>().toMutableList()
    var lastt = 0
    var kontrola = false
    var farba = Color.BLUE
    init {
        setOnClickListener {
            Log.d("ss", "")
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(!kontrola) {
            Log.d("sss", "${event?.x}")
            for (i in body) {
                i.isIn(event!!.x.toInt(), event.y.toInt())
            }
            if (ciara.size > 2) {
                ciara[pom * 4 + 2] = event!!.x
                ciara[pom * 4 + 3] = event!!.y
            }
            if (event?.action == ACTION_UP) {
                for (i in 0..1) {
                    if (ciara.size > 2) {
                        ciara.remove(ciara.last())
                    }
                }
                if(KOD.size < 4){
                    for(i in body){
                        i.farba = Color.RED
                        i.free = false
                    }
                    pom = 0
                    ciara =emptyList<Float>().toMutableList()
                    KOD = emptyList<Int>().toMutableList()
                    last = emptyList<Int>().toMutableList()
                    lastt = 0
                    Toast.makeText(ctx,"Musíš spojiť aspoň štyri rozdielne body", Toast.LENGTH_SHORT).show()
                }else {
                    kontrola = true
                }
                Log.d("test", "OOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO")
            }
        }
        return super.onTouchEvent(event)

    }
    fun mozem():Boolean{
        if (w == 0){
            return false
        }
        return true
    }
    fun addBod(x:Int,y:Int,i:Int,k:Int,kolko:Int) : Boolean{
        if(w == 0){
            return false
        }
        body.add(Bod(ctx,x,y,this,i,k,kolko+1,w.toInt()))
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
            for(i in body){
                i.draw(canvas)
            }
        if(ciara.size > 2) {
            val mP = Paint()
            mP.color = farba
            mP.strokeWidth = 20.0F
            if(pom == 0) {

            }else{
                val l = emptyArray<Float>().toMutableList()
                for (i in ciara){
                    l.add(i)
                }
                canvas.drawLines(l.toFloatArray(),mP)
            }
        }
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        w = widthMeasureSpec
        h = heightMeasureSpec
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        this.w = w
        this.h =h
    }

}