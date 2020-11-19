package com.fmph.kai.mistyforest

import android.content.Context
import android.util.Log

class GameGrid(var context: Context, var _canvas_view: CanvasView) {

    var grid = mutableListOf<MutableList<Tile>>()
    var canvas_view = _canvas_view

    init {
        for (j in 0..5) {
            var tempList = mutableListOf<Tile>()
            for (i in 0..7) {
                if (j >= 2) {
                    var count_blocks = 0
                    for (b in 0..j-1) {
                        if (grid.get(b).get(i).IS_BLOCKED) {
                            count_blocks++
                        }
                    }
                    if (count_blocks < 2) {
                        var t = Tile(context, canvas_view, i.toFloat(), j.toFloat(), true)
                        if (i == 0) t.IS_CLEARED = true
                        tempList.add(t)
                    } else {
                        var t = Tile(context, canvas_view, i.toFloat(), j.toFloat(), false)
                        if (i == 0) t.IS_CLEARED = true
                        tempList.add(t)
                    }
                } else {
                    var t = Tile(context, canvas_view, i.toFloat(), j.toFloat(), true)
                    if (i == 0) t.IS_CLEARED = true
                    tempList.add(t)
                }
            }
            grid.add(tempList)
        }
    }

    fun moveLeft() {
        /*for (i in 0..grid.size-1) {
            for (j in 1..grid.get(i).size-1) {
                var t = grid[i][j]
                t.CELL_X -= 1
                t.countCoords()
                grid[i][j-1] = t
            }
        }*/
        var copyGrid = grid.toMutableList()

        for (j in 0..5) {
            var tempList = mutableListOf<Tile>()
            for (i in 0..6) {
                tempList.add(copyGrid.get(j).get(i+1))
            }
            grid.set(j, tempList)
        }
        for (i in 0..grid.size-1) {
            for (j in 0..grid[i].size-1) {
                grid[i][j].CELL_X -= 1
                grid[i][j].countCoords()
            }
        }
        var count_blocks = 0
        for (i in 0..5) {
            if (count_blocks < 2) {
                var t = Tile(context, canvas_view, 7f, i.toFloat(), true)
                grid.get(i).add(t)
                if (t.IS_BLOCKED) {
                    count_blocks++
                }
            } else {
                var t = Tile(context, canvas_view, 7f, i.toFloat(), false)
                grid.get(i).add(t)
            }


        }
    }
}