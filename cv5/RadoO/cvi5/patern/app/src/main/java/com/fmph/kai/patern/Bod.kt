package com.fmph.kai.patern

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import kotlin.random.Random

class Bod(ctx: Context,val xx: Int, val yy : Int, v:Canv,i:Int,k:Int,kolko:Int,w:Int) {
    var size = w/9
    init {
        if(kolko > 5){
            size = w/13
        }
    }
    var x = xx-(size/2)
    var y = yy-size
    var row = i
    var col = k
    var farba = Color.RED
    var v = v
    var free = false
    var text = i*kolko+k
    init {
        if(kolko == 5){
            size = w/13
        }
    }
    fun draw(cavas : Canvas){
        val mP = Paint()
        mP.color = farba
        cavas.drawCircle((x+size/2).toFloat(),(y+size/2).toFloat(),(size/2).toFloat(),mP)
    }

    fun isIn(eventx: Int, eventy: Int){
        if((x <= eventx && eventx <= x+size) and (y <= eventy && eventy <= y+size)){
            farba = Color.BLUE
            var medzi = emptyArray<Int>().toMutableList()
            if(v.pom == 0){
                v.last.add(row)
                v.last.add(col)
                v.lastt = text
                v.ciara.add((x + size / 2).toFloat())
                v.ciara.add((y + size / 2).toFloat())
                v.ciara.add((x + size / 2).toFloat())
                v.ciara.add((y + size / 2).toFloat())

            }else{
                check(medzi)
                Log.d("test", "$text,${v.lastt},${v.pom}")
                Log.d("test", "$medzi")
                if(text < v.lastt){
                    medzi = medzi.sortedDescending().toMutableList()
                }
                Log.d("test", "$medzi")
                for(c in medzi){
                    v.KOD.add(c)
                }
                Log.d("sss", "$medzi")
            }
            if(!free) {
                free = true
                v.ciara[v.pom*4+2] = (x + size / 2).toFloat()
                v.ciara[v.pom*4+3] = (y + size / 2).toFloat()
                v.ciara.add((x + size / 2).toFloat())
                v.ciara.add((y + size / 2).toFloat())
                v.ciara.add(eventx.toFloat())
                v.ciara.add(eventy.toFloat())
                v.pom++
                v.last[0] = row
                v.last[1] = col
                v.lastt = text
                v.KOD.add(text)

            }
            Log.d("ss", "KKKK")
        }
    }
    fun check(medzi: MutableList<Int>) {
        for (i in v.body) {
            if (!i.free) {
                    //stlpce
                if (v.last[1] == col) {
                    if (v.last[0] < row) {
                        if ((i.col == col) and (i.row > v.last[0]) and (i.row < row)) {
                            medzi.add(i.text)
                            i.free = true
                            i.farba = Color.BLUE
                            v.ciara[v.pom*4+2] = (i.x + size / 2).toFloat()
                            v.ciara[v.pom*4+3] = (i.y + size / 2).toFloat()
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.pom++
                        }
                    }else{
                        if ((i.col == col) and (i.row < v.last[0]) and (i.row > row)) {
                            medzi.add(i.text)
                            i.free = true
                            i.farba = Color.BLUE
                            v.ciara[v.pom*4+2] = (i.x + size / 2).toFloat()
                            v.ciara[v.pom*4+3] = (i.y + size / 2).toFloat()
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.pom++
                        }
                    }
                }
                //riadky
                if (v.last[0] == row) {
                    if (v.last[1] < col) {
                        if ((i.row == row) and (i.col > v.last[1]) and (i.col < col)) {
                            medzi.add(i.text)
                            i.free = true
                            i.farba = Color.BLUE
                            v.ciara[v.pom*4+2] = (i.x + size / 2).toFloat()
                            v.ciara[v.pom*4+3] = (i.y + size / 2).toFloat()
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.pom++
                        }
                    }else{
                        if ((i.row == row) and (i.col < v.last[1]) and (i.col > col)) {
                            medzi.add(i.text)
                            i.free = true
                            i.farba = Color.BLUE
                            v.ciara[v.pom*4+2] = (i.x + size / 2).toFloat()
                            v.ciara[v.pom*4+3] = (i.y + size / 2).toFloat()
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.pom++
                        }
                    }
                }
                //uhlopriecky pl
                if(v.last[0]+v.last[1] == row+col){
                    if(v.last[0]>row){
                        if((i.row+i.col == row+col) and (i.row < v.last[0]) and (i.row > row)){
                            medzi.add(i.text)
                            i.free = true
                            i.farba = Color.BLUE
                            v.ciara[v.pom*4+2] = (i.x + size / 2).toFloat()
                            v.ciara[v.pom*4+3] = (i.y + size / 2).toFloat()
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.pom++
                        }
                    }else{
                        if((i.row+i.col == row+col) and (i.row > v.last[0]) and (i.row < row)){
                            medzi.add(i.text)
                            i.free = true
                            i.farba = Color.BLUE
                            v.ciara[v.pom*4+2] = (i.x + size / 2).toFloat()
                            v.ciara[v.pom*4+3] = (i.y + size / 2).toFloat()
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.pom++
                        }
                    }
                }
                //uhlopriecky lp
                if(v.last[0]-v.last[1] == row-col){
                    if(v.last[0]>row){
                        if((i.row-i.col == row-col) and (i.row < v.last[0]) and (i.row > row)){
                            medzi.add(i.text)
                            i.free = true
                            i.farba = Color.BLUE
                            v.ciara[v.pom*4+2] = (i.x + size / 2).toFloat()
                            v.ciara[v.pom*4+3] = (i.y + size / 2).toFloat()
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.pom++
                        }
                    }else{
                        if((i.row-i.col == row-col) and (i.row > v.last[0]) and (i.row < row)){
                            medzi.add(i.text)
                            i.free = true
                            i.farba = Color.BLUE
                            v.ciara[v.pom*4+2] = (i.x + size / 2).toFloat()
                            v.ciara[v.pom*4+3] = (i.y + size / 2).toFloat()
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.ciara.add((i.x + size / 2).toFloat())
                            v.ciara.add((i.y + size / 2).toFloat())
                            v.pom++
                        }
                    }
                }
            }
        }
    }
}