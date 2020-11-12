package com.example.breakball

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_score.*

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        movesCounterText.text = intent.getIntExtra("moves", 0).toString()
        scoreText.text = intent.getIntExtra("score", 0).toString()
        highScoreText.text = intent.getIntExtra("highScore", 0).toString()

        scoreImage.setImageDrawable(ContextCompat.getDrawable(applicationContext ,R.drawable.destruct_ball_score_pic))

        object: CountDownTimer(8000, 1000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                finish()
            }
        }.start()
    }
}