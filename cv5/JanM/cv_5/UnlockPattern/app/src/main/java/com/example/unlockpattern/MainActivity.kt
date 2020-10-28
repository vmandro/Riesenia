package com.example.unlockpattern

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.absoluteValue

var mode = 3 // BONUS 
var pattern = Pattern()
var savedPattern = Pattern()
var patternOrbs = mutableListOf<PatternOrb>()
var start = true

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setButtons()
    }

    fun setButtons() {
        setButton.setOnClickListener {
            if (pattern.orbSequence.size < 4) {
                Toast.makeText(this, "Your pattern is too short !", Toast.LENGTH_LONG).show()
                resetPattern()
            }
            else {
                savedPattern.orbSequence = pattern.orbSequence.toMutableList()
                Toast.makeText(this, "SAVED !", Toast.LENGTH_LONG).show()
                resetPattern()
            }
        }

        checkButton.setOnClickListener {
            if (pattern.orbSequence.size < 4) {
                Toast.makeText(this, "Your pattern is too short !", Toast.LENGTH_LONG).show()
                resetPattern()
            }
            else if (savedPattern.orbSequence.isEmpty()) {
                Toast.makeText(this, "NO DATA !", Toast.LENGTH_LONG).show()
                resetPattern()
            }
            else if (pattern.orbSequence == savedPattern.orbSequence) {
                Toast.makeText(this, "Your pattern is correct !", Toast.LENGTH_LONG).show()
                resetPattern()
            }
            else {
                Toast.makeText(this, "Your pattern is incorrect !", Toast.LENGTH_LONG).show()
                resetPattern()
            }
        }
    }

    fun resetPattern() {
        pattern.orbSequence.clear()
        for (orb in patternOrbs) {
            orb.marked = false
        }
        view.invalidate()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.title == resources.getString(R.string.menu_item_2)) {
            mode = 2
            start = true
            resetPattern()
            patternOrbs.clear()
            view.invalidate()
        }
        else if (item.title == resources.getString(R.string.menu_item_3)) {
            mode = 3
            start = true
            resetPattern()
            patternOrbs.clear()
            view.invalidate()
        }
        else if (item.title == resources.getString(R.string.menu_item_4)) {
            mode = 4
            start = true
            resetPattern()
            patternOrbs.clear()
            view.invalidate()
        }
        return true
    }

}

class PatternView(internal var context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    init {
        invalidate()
        setBackgroundColor(Color.TRANSPARENT)
    }

    fun setPatternOrbs() {
        val cellSize = width / (mode + 1)
        var row = 1
        var column = 1
        for (i in 0 until mode * mode) {
            Log.d("appTest", "${column} ${row}")
            patternOrbs.add(PatternOrb(column * cellSize, row * cellSize))
            column += 1
            if (column == (mode + 1)) {
                row += 1
                column = 1
            }
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (start) {
            setPatternOrbs()
            start = false
        }
        val orbPaint = Paint()
        orbPaint.style = Paint.Style.FILL
        orbPaint.color = Color.GRAY
        for (orb in patternOrbs) {
            if (orb.marked)
                orbPaint.color = Color.GREEN
            else
                orbPaint.color = Color.GRAY
            canvas?.drawCircle(orb.x.toFloat(), orb.y.toFloat(), ((width / (mode + 1) / 4)).toFloat(), orbPaint)
        }
        if (pattern.orbSequence.isNotEmpty()) {
            val path = Path()
            for (orbIndex in pattern.orbSequence.indices) {
                if (orbIndex == 0)
                    path.moveTo(pattern.orbSequence[0].x.toFloat(),
                        pattern.orbSequence[0].y.toFloat()
                    )
                else {
                    path.lineTo(
                        pattern.orbSequence[orbIndex].x.toFloat(),
                        pattern.orbSequence[orbIndex].y.toFloat()
                    )
                    path.moveTo(
                        pattern.orbSequence[orbIndex].x.toFloat(),
                        pattern.orbSequence[orbIndex].y.toFloat()
                    )
                }

            }
            var patternPaint = Paint()
            patternPaint.color = Color.BLACK
            patternPaint.style = Paint.Style.STROKE
            patternPaint.strokeWidth = 20.toFloat()
            canvas?.drawPath(path, patternPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if ( event != null ) {
            if (event.action == MotionEvent.ACTION_MOVE) {
                for (orb in patternOrbs) {
                    Log.d("appTest", "${event.x} ${event.y}")
                    if ((event.x - orb.x).absoluteValue < ((width / (mode + 1) / 4)) &&
                        (event.y - orb.y).absoluteValue < ((width / (mode + 1) / 4)) && !orb.marked
                    ) {
                        orb.marked = true
                        pattern.orbSequence.add(orb)
                        invalidate()
                    }

                }
            }
        }
        return true
    }
}

data class PatternOrb(val x: Int,val y: Int) {

    var marked = false
}

class Pattern() {

    var orbSequence = mutableListOf<PatternOrb>()
}