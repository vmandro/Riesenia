package com.fmph.kai.mistyforest

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.drawable.toBitmap
import java.util.*

class Tile(var context: Context, var _canvas_view: CanvasView, cell_x: Float, cell_y: Float, _allowed_block: Boolean) {

    var CELL_X: Float = -1f
    var CELL_Y: Float = -1f
    var COORD_X: Float = -1f
    var COORD_Y: Float = -1f
    var IS_SPECIAL: Boolean = false
    var IS_BLOCKED: Boolean = false
    var IS_CLEARED: Boolean = false
    var HAS_REWARD: Boolean = false
    lateinit var reward : Bitmap
    lateinit var image : Bitmap
    var visited = false
    var canvas_view = _canvas_view
    var allowed_block = _allowed_block
    var highlighted: Boolean = false

    init {
        CELL_X = cell_x
        CELL_Y = cell_y
        IS_SPECIAL = true
        countCoords()
        Log.d("COTO", (canvas_view.width_of_one_tile).toString())
        var tile_blocker_chance = (0..10).random()
        if (tile_blocker_chance >= 8 && allowed_block) {
            IS_BLOCKED = true
            var tile_blocker_item = (1..6).random()
            when (tile_blocker_item) {
                1 -> image = context.resources.getDrawable(R.drawable.tile_blocker_1).toBitmap((canvas_view.width_of_one_tile).toInt(), (canvas_view.height_of_one_tile).toInt(), null)
                2 -> image = context.resources.getDrawable(R.drawable.tile_blocker_2).toBitmap((canvas_view.width_of_one_tile).toInt(), (canvas_view.height_of_one_tile).toInt(), null)
                3 -> image = context.resources.getDrawable(R.drawable.tile_blocker_3).toBitmap((canvas_view.width_of_one_tile).toInt(), (canvas_view.height_of_one_tile).toInt(), null)
                4 -> image = context.resources.getDrawable(R.drawable.tile_blocker_4).toBitmap((canvas_view.width_of_one_tile).toInt(), (canvas_view.height_of_one_tile).toInt(), null)
                5 -> image = context.resources.getDrawable(R.drawable.tile_blocker_5).toBitmap((canvas_view.width_of_one_tile).toInt(), (canvas_view.height_of_one_tile).toInt(), null)
                6 -> image = context.resources.getDrawable(R.drawable.tile_blocker_6).toBitmap((canvas_view.width_of_one_tile).toInt(), (canvas_view.height_of_one_tile).toInt(), null)
            }
        } else {
            var reward_chance = (0..10).random()
            if (reward_chance >= 9) {
                HAS_REWARD = true
                reward = context.resources.getDrawable(R.drawable.payback).toBitmap((canvas_view.width_of_one_tile*0.7).toInt(), (canvas_view.height_of_one_tile).toInt(), null)
            }
        }
    }

    fun countCoords() {
        COORD_X = ((0.186*(canvas_view.width).toFloat())+(CELL_X*canvas_view.width_of_one_tile)).toFloat()
        COORD_Y = ((0.01*(canvas_view.height).toFloat())+CELL_Y*canvas_view.height_of_one_tile).toFloat()
    }

    fun changeImage(bmp: Bitmap) {
        image = bmp
    }

    fun getX() : Float {
        return COORD_X
    }

    fun getY() : Float {
        return COORD_Y
    }

    override fun toString() : String {
        if (IS_BLOCKED) {
            return "[("+CELL_X+","+CELL_Y+")]"
        }
        return "["+CELL_X+","+CELL_Y+"]"
    }

}