package com.fmph.kai.mistyforest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.util.AttributeSet
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import kotlin.math.max


class CanvasView(internal var context: Context, attrs: AttributeSet) : View(context, attrs) {
    //pocet snurok na 1 strane
    private val N = 4
    //medzera medzi snurkami na vysku(na sirku sa to pocita 1. rad do 1/3 sirky a 2. rad do 2/3 sirky canvasu)
    private val hMargin = 200f
    val attries = attrs
    lateinit var game: GameGrid
    var gamegridset: Boolean = false
    var width_of_one_tile: Float = -1f
    var height_of_one_tile: Float = -1f
    var tools = mutableListOf<Tool>()
    lateinit var hold: Bitmap
    var hold_x: Float = 10f
    var hold_y: Float = 10f
    var holds: Boolean = false
    var holds_tile: Int = 0
    var highlighted_tiles = mutableListOf<Tile>()
    var score: Int = 0
    var count_tool_single = 20
    var count_tool_column = 8
    var count_tool_adjacent = 7


    init {

    }

    override protected fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        width_of_one_tile = ((width.toFloat()*0.775f)/8f).toFloat() //0.775 //0.845
        height_of_one_tile = ((height.toFloat()*0.912f)/6f).toFloat() //0.912 //0.932
        if (gamegridset == false) {
            game = GameGrid(context, this)
            gamegridset = true
        }
        var paint = Paint()
        Log.d("DRAW", "kreslene")

        var tileboard_bcg = resources.getDrawable(R.drawable.tileboard_background).toBitmap(width, height, null)
        canvas.drawBitmap(tileboard_bcg, 0f, 0f, paint)

        var background = resources.getDrawable(R.drawable.window_background).toBitmap(width, height, null)
        canvas.drawBitmap(background, 0f, 0f, paint)

        //gridsize zacina v 18,6% sirky obrazovky
        //konci v 96,1%, teda sirka celej gridsize je 77,5%

        //gridsize zacina v 0.1% vysky obrazovky
        //konci v 91,3%, teda vyska celej gridsize je 91,2%

        tools = mutableListOf<Tool>()
        tools.add(Tool(context, this, 1, resources.getDrawable(R.drawable.tool_single).toBitmap()))
        tools.add(Tool(context, this, 2, resources.getDrawable(R.drawable.tool_column).toBitmap()))
        tools.add(Tool(context, this, 3, resources.getDrawable(R.drawable.tool_adjacent).toBitmap()))
        canvas.drawBitmap(tools[0].image, tools[0].getX(), tools[0].getY(), paint)
        canvas.drawBitmap(tools[1].image, tools[1].getX(), tools[1].getY(), paint)
        canvas.drawBitmap(tools[2].image, tools[2].getX(), tools[2].getY(), paint)

        for (i in 0 until game.grid.size) {
            for (j in 0 until game.grid.get(0).size) {
                if (game.grid.get(i).get(j).IS_BLOCKED) {
                    var obr = game.grid.get(i).get(j).image
                    canvas.drawBitmap(
                        obr,
                        game.grid.get(i).get(j).COORD_X,
                        game.grid.get(i).get(j).COORD_Y,
                        paint
                    )
                }
                if (game.grid.get(i).get(j).IS_CLEARED == false) {
                    var clearable = resources.getDrawable(R.drawable.tile_clearable).toBitmap(width_of_one_tile.toInt(), (height_of_one_tile*1.1).toInt(), null)
                    canvas.drawBitmap(
                        clearable,
                        game.grid.get(i).get(j).COORD_X,
                        game.grid.get(i).get(j).COORD_Y,
                        paint
                    )
                    if (game.grid[i][j].highlighted) {
                        var highlight = resources.getDrawable(R.drawable.tile_highlight).toBitmap(width_of_one_tile.toInt(), (height_of_one_tile*1.1).toInt(), null)
                        canvas.drawBitmap(highlight, game.grid[i][j].getX(), game.grid[i][j].getY(), paint)
                    }
                } else {
                    if (game.grid[i][j].HAS_REWARD) {
                        var reward = game.grid[i][j].reward
                        canvas.drawBitmap(reward, game.grid[i][j].getX()+width_of_one_tile*0.15f, game.grid[i][j].getY(), paint)
                    }
                }
            }
        }
        if (holds) {
            canvas.drawBitmap(hold, hold_x-hold.width/2, hold_y-hold.height/2, paint)
        }
        paint.setARGB(255, 204,204,0)
        paint.textSize = 65f
        canvas.drawText(score.toString(), 0.075f*width, 0.93f*height, paint)
        paint.setARGB(255, 237,184,121)
        paint.textSize = 20f
        canvas.drawText(count_tool_single.toString(), 0.086f*width, 0.46f*height, paint)
        canvas.drawText(count_tool_column.toString(), 0.086f*width, 0.64f*height, paint)
        canvas.drawText(count_tool_adjacent.toString(), 0.086f*width, 0.83f*height, paint)
        if (count_tool_single == 0 && count_tool_column == 0 && count_tool_adjacent == 0) {
            paint.setARGB(255, 0,255,128)
            paint.textSize = 30f
            canvas.drawText("RESTART", 0.05f*width, 0.22f*height, paint)
        }
        /*val displayMetrics = DisplayMetrics()
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics)
        val height = displayMetrics.heightPixels
        val width = displayMetrics.widthPixels*/
        //invalidate()
        Log.d("GAME", game.grid.size.toString())
        //canvas.drawPicture(resources.getDrawable(R.drawable.payback))

        /*var lengthText = rootView.findViewById(R.id.lengthText) as TextView
        lengthText.setText("Length: $length")*/
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        val y = event.y
        Log.d("TOUCH", ""+x+" "+y)
        Log.d("TOUCH", (x/width).toString())
        Log.d("TOUCH", (y/height).toString())
        //game.moveLeft()

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (x <= 0.2*width) {
                    if (y <= 0.22*height) {
                        //restart
                        if (count_tool_single == 0 && count_tool_column == 0 && count_tool_adjacent == 0) {
                            count_tool_single = 20
                            count_tool_column = 8
                            count_tool_adjacent = 7
                            score = 0
                            game = GameGrid(context, this)
                        }
                    } else {
                        //tuknutie na niektory z toolov
                        for (i in 0..2) {
                            if (x >= tools[i].getX() && x <= tools[i].getX() + tools[i].image.width &&
                                y >= tools[i].getY() && y <= tools[i].getY() + tools[i].image.height
                            ) {
                                hold = tools[i].image//.copy(null, true)
                                hold_x = x
                                hold_y = y
                                holds = true
                                holds_tile = i + 1
                                break
                            }
                        }
                    }
                } else {
                    //zbieranie odmien
                    for (i in 0..5) {
                        for (j in 0..7) {
                            if (x >= game.grid[i][j].getX() && x <= game.grid[i][j].getX()+width_of_one_tile &&
                                    y >= game.grid[i][j].getY() && y <= game.grid[i][j].getY()+height_of_one_tile &&
                                    game.grid[i][j].HAS_REWARD) {
                                game.grid[i][j].HAS_REWARD = false
                                score += 5
                            }
                        }
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                clearHighlightedTiles()
                //mozno len ak holds == true
                hold_x = x
                hold_y = y
                //zisti nad ktorym ho drzi
                for (i in 0..5) {
                    for (j in 0..7) {
                        var tile = game.grid[i][j]
                        if (x >= tile.getX() && x <= tile.getX()+width_of_one_tile &&
                                y >= tile.getY() && y <= tile.getY()+height_of_one_tile) {
                            when (holds_tile) {
                                1 -> {
                                    if (count_tool_single > 0 && tile.IS_BLOCKED == false && tile.IS_CLEARED == false &&
                                        ((i-1 >= 0 && game.grid[i-1][j].IS_CLEARED) || (j-1 >= 0 && game.grid[i][j-1].IS_CLEARED)
                                                || (j+1 <= 7 && game.grid[i][j+1].IS_CLEARED) || (i+1 <= 5 && game.grid[i+1][j].IS_CLEARED))) {
                                        highlighted_tiles.add(tile)
                                    }
                                }
                                2 -> {
                                    if (count_tool_column > 0 && tile.IS_CLEARED == true) {
                                        var stop_up = false; var stop_down = false;
                                        if (i-1 >= 0 && game.grid[i-1][j].IS_BLOCKED) stop_up = true
                                        if (i-1 >= 0 && stop_up == false && game.grid[i-1][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i-1][j])
                                        if (i-2 >= 0 && stop_up == false && game.grid[i-2][j].IS_BLOCKED) stop_up = true
                                        if (i-2 >= 0 && stop_up == false && game.grid[i-2][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i-2][j])
                                        if (i-3 >= 0 && stop_up == false && game.grid[i-3][j].IS_BLOCKED) stop_up = true
                                        if (i-3 >= 0 && stop_up == false && game.grid[i-3][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i-3][j])
                                        if (i-4 >= 0 && stop_up == false && game.grid[i-4][j].IS_BLOCKED) stop_up = true
                                        if (i-4 >= 0 && stop_up == false && game.grid[i-4][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i-4][j])
                                        if (i-5 >= 0 && stop_up == false && game.grid[i-5][j].IS_BLOCKED) stop_up = true
                                        if (i-5 >= 0 && stop_up == false && game.grid[i-5][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i-5][j])

                                        if (i+1 <= 5 && game.grid[i+1][j].IS_BLOCKED) stop_down = true
                                        if (i+1 <= 5 && stop_down == false && game.grid[i+1][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i+1][j])
                                        if (i+2 <= 5 && stop_down == false && game.grid[i+2][j].IS_BLOCKED) stop_down = true
                                        if (i+2 <= 5 && stop_down == false && game.grid[i+2][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i+2][j])
                                        if (i+3 <= 5 && stop_down == false && game.grid[i+3][j].IS_BLOCKED) stop_down = true
                                        if (i+3 <= 5 && stop_down == false && game.grid[i+3][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i+3][j])
                                        if (i+4 <= 5 && stop_down == false && game.grid[i+4][j].IS_BLOCKED) stop_down = true
                                        if (i+4 <= 5 && stop_down == false && game.grid[i+4][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i+4][j])
                                        if (i+5 <= 5 && stop_down == false && game.grid[i+5][j].IS_BLOCKED) stop_down = true
                                        if (i+5 <= 5 && stop_down == false && game.grid[i+5][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i+5][j])
                                    }
                                }
                                3 -> {
                                    if (count_tool_adjacent > 0 && tile.IS_CLEARED == true) {
                                        if (i-1 >= 0 && j-1 >= 0 && game.grid[i-1][j-1].IS_CLEARED == false) highlighted_tiles.add(game.grid[i-1][j-1])
                                        if (i-1 >= 0 && game.grid[i-1][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i-1][j])
                                        if (i-1 >= 0 && j+1 <= 7 && game.grid[i-1][j+1].IS_CLEARED == false) highlighted_tiles.add(game.grid[i-1][j+1])
                                        if (j-1 >= 0 && game.grid[i][j-1].IS_CLEARED == false) highlighted_tiles.add(game.grid[i][j-1])
                                        if (j+1 <= 7 && game.grid[i][j+1].IS_CLEARED == false) highlighted_tiles.add(game.grid[i][j+1])
                                        if (i+1 <= 5 && j-1 >= 0 && game.grid[i+1][j-1].IS_CLEARED == false) highlighted_tiles.add(game.grid[i+1][j-1])
                                        if (i+1 <= 5 && game.grid[i+1][j].IS_CLEARED == false) highlighted_tiles.add(game.grid[i+1][j])
                                        if (i+1 <= 5 && j+1 <= 7 && game.grid[i+1][j+1].IS_CLEARED == false) highlighted_tiles.add(game.grid[i+1][j+1])
                                    }
                                }
                            }

                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                if (holds) {
                    holds = false
                    clearTiles()
                    if (highlighted_tiles.size > 0) {
                        when (holds_tile) {
                            1 -> count_tool_single = max(count_tool_single - 1, 0)
                            2 -> count_tool_column = max(count_tool_column - 1, 0)
                            3 -> count_tool_adjacent = max(count_tool_adjacent - 1, 0)
                        }
                    }
                    holds_tile = 0
                    clearHighlightedTiles()
                }
            }
        }
        if (checkForMoveLeft()) {
            game.moveLeft()
        }
        highlight_tiles()
        invalidate()
        return true
    }

    fun checkForMoveLeft(): Boolean {
        for (i in 0..5) {
            if (game.grid[i][7].IS_CLEARED == true) {
                return true
            }
        }
        return false
    }

    fun clearTiles() {
        for (i in 0..highlighted_tiles.size-1) {
            highlighted_tiles[i].IS_CLEARED = true
        }
    }

    fun highlight_tiles() {
        for (i in 0..highlighted_tiles.size-1) {
            highlighted_tiles[i].highlighted = true
        }
    }

    fun clearHighlightedTiles() {
        for (i in 0..highlighted_tiles.size-1) {
            highlighted_tiles[i].highlighted = false
        }
        highlighted_tiles = mutableListOf<Tile>()
    }

    fun clearCanvas() {
        /*circles.clear()
        for (i in -N .. N) {
            if(i == 0) continue

            val c = Circle(i, hMargin)
            circles.add(c)
        }

        savedConnections.add(connectedCircles)
        connectedCircles = emptyList<Circle>().toMutableList()

        var infoText = rootView.findViewById(R.id.infoText) as TextView
        infoText.setText("")

        invalidate()*/
    }
}