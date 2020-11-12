package com.example.breakball

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        introImage.setImageDrawable(ContextCompat.getDrawable(applicationContext ,R.drawable.destruct_ball_title_pic))

        object: CountDownTimer(4000, 1000) {
            override fun onTick(p0: Long) {}
            override fun onFinish() {
                finish()
            }
        }.start()

    }
}