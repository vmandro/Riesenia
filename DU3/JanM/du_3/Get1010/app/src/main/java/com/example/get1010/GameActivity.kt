package com.example.get1010

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.view.*
import kotlin.random.Random

class GameActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)
        listenersSetter()
    }

    fun listenersSetter() {
        menuButton.setOnClickListener {
            if (GameTable.gameWinLose == 1) { // if win, update best score
                preferences = getSharedPreferences("main", Context.MODE_PRIVATE)
                if (preferences.getInt("score", 0) < GameTable.score) {
                    Toast.makeText(this, "NEW BEST SCORE !", Toast.LENGTH_LONG).show()
                    val editor = preferences.edit()
                    editor.putInt("score", GameTable.score)
                    editor.apply()
                }
            }
            startActivity(Intent(this, MainActivity::class.java))
        }
        gameView.setOnClickListener { // update score
                scoreText2.text = Integer.toBinaryString(GameTable.score)
        }
    }
}

class GameTable(internal var context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    val tableCells: MutableList<MutableList<TableCell>> = mutableListOf()
    var maxValueIndex = 2 // highest value

    companion object {
        var score = 0
        var gameWinLose = 0 // 1 == win, 2 == lose
    }

    init { // 1. init ... 2. draw
        gameWinLose = 0
        score = 0
        createTable()
    }

    fun createTable() {
        for (row in 0..4) {
            tableCells.add(mutableListOf())
            for (column in 0..4) {
                tableCells[row].add(TableCell(TableCell.values[Random.nextInt(0, maxValueIndex + 1)]))
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        val cellSize = (width / 7).toFloat()
        var x = cellSize
        var y = cellSize

        if (gameWinLose != 0) { // win or lose
            var paint = Paint()
            paint.color = Color.BLACK
            paint.style = Paint.Style.FILL
            canvas?.drawRect(x, y, x * 6, y * 6, paint)

            paint.textAlign = Paint.Align.CENTER
            paint.textSize = x * 5 / 7
            paint.color = Color.WHITE
            if (gameWinLose == 1) {
                canvas?.drawText(
                    "YOU WIN !",
                    x + ((x * 5) / 2),
                    y + (((y * 5) / 2) - ((paint.descent() + paint.ascent()) / 2)), // align center
                    paint
                )
            } else {
                canvas?.drawText(
                    "GAME OVER !",
                    x + ((x * 5) / 2),
                    y + (((y * 5) / 2) - ((paint.descent() + paint.ascent()) / 2)), // align center
                    paint
                )
            }

        } else {

            var textPaint = Paint()
            textPaint.color = Color.BLACK
            textPaint.style = Paint.Style.FILL
            textPaint.textSize = cellSize / 3
            textPaint.textAlign = Paint.Align.CENTER

            for (row in tableCells) {
                for (cell in row) {
                    var rectPaint = Paint()
                    if (cell.value == "empty") {
                        rectPaint.color = Color.BLACK
                    } else {
                        rectPaint.color = TableCell.colors[TableCell.values.indexOf(cell.value)]
                    }
                    rectPaint.style = Paint.Style.FILL

                    canvas?.drawRect(x, y, x + cellSize, y + cellSize, rectPaint)
                    canvas?.drawText(
                        cell.value,
                        x + (cellSize / 2),
                        y + ((cellSize / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)),
                        textPaint
                    )
                    x += cellSize
                }
                y += cellSize
                x = cellSize
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (gameWinLose == 0 && event != null && event.action == MotionEvent.ACTION_DOWN) {
            val cellSize = (width / 7).toFloat()
            val cellColumn = (event.x / cellSize).toInt()
            val cellRow = (event.y / cellSize).toInt()
            if (cellColumn in 1..5 && cellRow in 1..5) {
                val foundCells = findCells(cellRow - 1, cellColumn - 1)
                if (foundCells.size > 1) {
                    score += TableCell.values.indexOf(foundCells[0].value) + 1
                    foundCells[0].increaseValue()
                    maxValueSetter(foundCells[0].value)
                    for (cell in 1 until foundCells.size) {
                        score += TableCell.values.indexOf(foundCells[cell].value) + 1
                        foundCells[cell].value = "empty"
                    }
                }
                invalidate()
                postDelayed({ // animation
                    winChecker()
                    invalidate()
                    loseChecker()
                    invalidate()
                    gravityMaker()
                    fillCells()
                    invalidate()
                }, 250)
            }
        }
        return super.onTouchEvent(event)
    }

    fun findCells(cellRow: Int, cellColumn: Int): List<TableCell> { // hladanie susednych buniek s rovnakou hodnotou

        val ret = mutableListOf<TableCell>()
        val value = tableCells[cellRow][cellColumn].value

        fun findCellsRecursion(row: Int, column: Int) { // rekurzia
            if (column in 0..4 && row in 0..4 && tableCells[row][column].value == value && tableCells[row][column] !in ret) {
                ret.add(tableCells[row][column])
                findCellsRecursion(row - 1, column)
                findCellsRecursion(row + 1, column)
                findCellsRecursion(row, column - 1)
                findCellsRecursion(row, column + 1)
            }
        }

        findCellsRecursion(cellRow, cellColumn)
        return ret
    }

    fun gravityMaker() { // padanie buniek smerom dole
        for (i in 1..5) { // najhorsi scenar ...
            for (row in 0 until tableCells.size - 1) {
                for (column in tableCells[row].indices) {
                    if (tableCells[row + 1][column].value == "empty") {
                        tableCells[row + 1][column].value = tableCells[row][column].value
                        tableCells[row][column].value = "empty"
                    }
                }
            }
        }
    }

    fun fillCells() { // vyplnenie prazdnych buniek
        for (row in tableCells.indices) {
            for (cell in tableCells[row]) {
                if (cell.value == "empty") {
                    cell.value = TableCell.values[Random.nextInt(0, maxValueIndex)]
                }
            }
        }
    }

    fun winChecker() { // najdi 10
        for (row in tableCells.indices) {
            for (cell in tableCells[row]) {
                if (cell.value == "1010") {
                    gameWinLose = 1
                    return
                }
            }
        }
    }

    fun loseChecker() { // nenajdi skupinu
        for (row in tableCells.indices) {
            for (column in tableCells[row].indices) {
                if (findCells(row, column).size > 1) {
                    return
                }
            }
        }
        gameWinLose = 2
    }

    fun maxValueSetter(value: String) { // update highest value
        if (value in TableCell.values && TableCell.values.indexOf(value) > maxValueIndex) {
            maxValueIndex = TableCell.values.indexOf(value)
        }
    }
}

class TableCell(var value: String) {

    companion object {
        val values = listOf("0001", "0010", "0011", "0100", "0101", "0110", "0111", "1000", "1001", "1010")
        val colors = listOf(
            Color.rgb(255, 204, 204),
            Color.rgb(230, 230, 230),
            Color.rgb(255, 128, 128),
            Color.rgb(191, 191, 191),
            Color.rgb(255, 51, 51),
            Color.rgb(153, 153, 153),
            Color.rgb(229, 0, 0),
            Color.rgb(115, 115, 115),
            Color.rgb(153, 0, 0),
            Color.rgb(76, 76, 76),
        )
    }

    fun increaseValue() {
        value = values[values.indexOf(value) + 1]
    }
}