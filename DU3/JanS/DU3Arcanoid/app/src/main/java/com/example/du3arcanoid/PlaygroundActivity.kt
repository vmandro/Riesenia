package com.example.du3arcanoid

import android.app.Dialog
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class PlaygroundActivity : AppCompatActivity() {

    lateinit var winSound: MediaPlayer
    lateinit var loseSound: MediaPlayer

    companion object {
        var myThread: Thread? = null
        var paused = false
        var sounds = false

        var resultTxt = "Result txt"
        var detailTxt = "Detail txt"
    }

    lateinit var pg: Playground
    var time = 60*1000  // 60 sec

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playground)
        pg = findViewById(R.id.playground)

        Playground.mode = intent.getStringExtra("mode")?:"easy"
        sounds = intent.getBooleanExtra("sounds", true)

        winSound = MediaPlayer.create(this@PlaygroundActivity, R.raw.win)
        loseSound = MediaPlayer.create(this@PlaygroundActivity, R.raw.lose)

        myThread = getThread()
    }

    fun getThread() = object : Thread() {
        override fun run() {
            Playground.run = true
            while (Playground.run) {
                if (!Playground.load) {
                    continue
                }
                if (!paused) {
                    pg.update()
                    time -= 20
                    runOnUiThread {
                        findViewById<TextView>(R.id.timerTxt).text = "${time/1000}"
                    }
                }
                checkGameState()
                try { sleep(50) }
                catch (e: InterruptedException) { e.printStackTrace() }
            }
            runOnUiThread { showDialog() }
        }
    }

    fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.custom_dialog)
        dialog.setCancelable(false)

        if (sounds) {
            if (resultTxt == "YOU WIN") winSound.start()
            if ((resultTxt == "YOU LOSE")) loseSound.start()
        }

        dialog.findViewById<TextView>(R.id.resultTxt).text = resultTxt
        dialog.findViewById<TextView>(R.id.detailTxt).text = detailTxt

//        val btnYes = dialog.findViewById<Button>(R.id.btn_yes);
//        btnYes.setOnClickListener {
//            Playground.started = false
//            pg.initGame()
//            time = 600*1000
//            dialog.dismiss()
//        }

        val btnNo = dialog.findViewById<Button>(R.id.btn_no);
        btnNo.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        dialog.show()
    }

    fun checkGameState() {
        if (Playground.bricks.isEmpty()) {
            Playground.run = false
            resultTxt = "YOU WIN"
            detailTxt = "Congratulation"
        }
        if (time == 0) {
            Playground.run = false
            resultTxt = "YOU LOSE"
            detailTxt = "Time's up"
        }
    }

    fun reload(v: View) {
        pg.initGame()
        paused = true
        changeIcon()
        Playground.started = false
        time = 60*1000
    }

    fun pause(v: View) {
        paused = !paused
        changeIcon()
    }

    fun changeIcon() {
        val btn = findViewById<FloatingActionButton>(R.id.pauseBtn)
        if (!paused) {
            btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pause))
        } else {
            btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.play))
        }
    }

    override fun onPause() {
        super.onPause()
        Playground.run = false
        Playground.load = false
        Playground.started = false
        paused = false
    }
}