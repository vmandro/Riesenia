package com.example.jednocertovane.canvasobjects

import android.content.Context
import android.graphics.Canvas

abstract  class CanvasObject(internal var context: Context) {
    abstract fun draw(canvas: Canvas)
}