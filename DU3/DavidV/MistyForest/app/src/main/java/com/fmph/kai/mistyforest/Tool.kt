package com.fmph.kai.mistyforest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas

class Tool(var context: Context, var _canvas_view: CanvasView, var _shell_y: Int, var _image: Bitmap) {

    lateinit var canvas_view: CanvasView
    var SHELL_Y: Int = -1
    lateinit var image : Bitmap
    var COORD_X: Float = -1f
    var COORD_Y: Float = -1f

    init {
        canvas_view = _canvas_view
        SHELL_Y = _shell_y
        image = _image

        //stred SHELL_1 je 130,190 (konkretne v 0.9% sirky a 33% vysky)
        //stred SHELL_2 je v 0.9% sirky a 53% vysky
        //stred SHELL_3 je v 0.9% sirky a 70% sirky
        COORD_X = (0.09*canvas_view.width.toFloat()).toFloat()-image.width/2
        when (SHELL_Y) {
            1 -> COORD_Y = (0.33*canvas_view.height.toFloat()).toFloat()-image.height/2
            2 -> COORD_Y = (0.53*canvas_view.height.toFloat()).toFloat()-image.height/2
            3 -> COORD_Y = (0.7*canvas_view.height.toFloat()).toFloat()-image.height/2
        }
    }

    fun getX(): Float {
        return COORD_X
    }

    fun getY(): Float {
        return COORD_Y
    }
}