package com.example.breakball

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.DisplayMetrics
import kotlinx.android.synthetic.main.activity_level.*

class LevelActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_level)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels
        val l = intent.getIntExtra("level", 1)

        var level = Level(this, width, height, l)

        /*
        resetBalls.setOnClickListener {
            level.resetBalls()
        }
        */
        setContentView(level)

        // 2 hodiny na jeden level
        object: CountDownTimer(2 * 60 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if(level.isLevelClear()) {
                    // stop timer
                    cancel()

                    // set intent
                    val intent = Intent(this@LevelActivity, ScoreActivity::class.java)
                    val score = level.numberOfTouches / level.moves
                    intent.putExtra("moves", level.moves)
                    intent.putExtra("score", score)

                    // update preferences
                    val preferences = getSharedPreferences("DestroyBall", Context.MODE_PRIVATE)

                    val highScore = preferences.getInt("LEVEL_HIGHSCORE_${level.level}", 0)
                    if(score > highScore) {
                        preferences.edit().putInt("LEVEL_HIGHSCORE_${level.level}", score).commit()
                    }
                    intent.putExtra("highScore", preferences.getInt("LEVEL_HIGHSCORE_${level.level}", 0))

                    startActivity(intent)
                    finish()
                }
            }
            override fun onFinish() {
                finish()
            }
        }.start()
    }
}