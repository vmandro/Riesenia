package com.olsinoo.unlockpattern

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.canvas_view.*

val TAG = "CANVAS"

class MainActivity : AppCompatActivity() {
    private var vis = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.canvas_view)
        fab_size3.visibility = FloatingActionButton.INVISIBLE
        fab_size4.visibility = FloatingActionButton.INVISIBLE
        fab_size5.visibility = FloatingActionButton.INVISIBLE

        fabMenu.setOnClickListener {
            if (vis) {
                fab_size3.visibility = FloatingActionButton.INVISIBLE
                fab_size4.visibility = FloatingActionButton.INVISIBLE
                fab_size5.visibility = FloatingActionButton.INVISIBLE
                vis = false
            } else {
                fab_size3.visibility = FloatingActionButton.VISIBLE
                fab_size4.visibility = FloatingActionButton.VISIBLE
                fab_size5.visibility = FloatingActionButton.VISIBLE
                vis = true
            }
        }
        fab_size3.setOnClickListener {
            N = 3
            fab_size3.visibility = FloatingActionButton.INVISIBLE
            fab_size4.visibility = FloatingActionButton.INVISIBLE
            fab_size5.visibility = FloatingActionButton.INVISIBLE
            vis = false
            Toast.makeText(this, "CLICK TO REFRESH", Toast.LENGTH_LONG).show()
        }
        fab_size4.setOnClickListener {
            N = 4
            fab_size3.visibility = FloatingActionButton.INVISIBLE
            fab_size4.visibility = FloatingActionButton.INVISIBLE
            fab_size5.visibility = FloatingActionButton.INVISIBLE
            vis = false
            Toast.makeText(this, "CLICK TO REFRESH", Toast.LENGTH_LONG).show()
        }
        fab_size5.setOnClickListener {
            N = 5
            fab_size3.visibility = FloatingActionButton.INVISIBLE
            fab_size4.visibility = FloatingActionButton.INVISIBLE
            fab_size5.visibility = FloatingActionButton.INVISIBLE
            vis = false
            Toast.makeText(this, "CLICK TO REFRESH", Toast.LENGTH_LONG).show()
        }
        buttonTest.setOnClickListener {
            val preferences = getSharedPreferences("secretUnlockPattern", MODE_PRIVATE)
            if (preferences != null) {
                if (preferences.getInt("size$N", 0) == 0) {
                    Toast.makeText(this, R.string.errorSize, Toast.LENGTH_LONG).show()
                } else {
                    val listOfCoords = mutableListOf<Pair<Int, Int>>()
                    val savedCoords = preferences.getString("coords$N", null)
                    if (savedCoords != null) {
                        for (valuePair in savedCoords.split(",")) {
                            Log.d(TAG, valuePair)
                            val str = valuePair.split("|")
                            listOfCoords.add(Pair(str[0].toInt(),str[1].toInt()))
                        }
                    }

                    if (listOfCoords.size > 0) {
                        Log.d(TAG, "Saved: $listOfCoords")
                        Log.d(TAG, "Current: ${visitedDots.toSet()}")
                        if (listOfCoords.size != visitedDots.size) {
                            Toast.makeText(this, "...INCORRECT...", Toast.LENGTH_LONG).show()
                        } else {
                            var corr = true
                            for (i in 0 until listOfCoords.size) {
                                if (visitedDots[i] != listOfCoords[i]) {
                                    Toast.makeText(this, "...INCORRECT...", Toast.LENGTH_LONG).show()
                                    corr = false
                                    break
                                }
                            }
                            if (corr) {
                                Toast.makeText(this, "-- CORRECT PATTERN --", Toast.LENGTH_LONG)
                                    .show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Pattern for this size is not saved", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        buttonSave.setOnClickListener {
            if (isPatternCorrect()) {
                val preferences = getSharedPreferences("secretUnlockPattern", MODE_PRIVATE)
                if (preferences != null) {
                    val editor = preferences.edit()
                    editor.putInt("size$N", N)
                    editor.putString("coords$N", visitedDots.joinToString(",") {"${it.first}|${it.second}"})
                    editor.apply()
                    Toast.makeText(this, "Pattern of size $N saved", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, R.string.errorMessage, Toast.LENGTH_LONG).show()
            }
        }
    }
}