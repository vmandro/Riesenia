package com.example.du3arcanoid

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.content.Intent
import android.media.MediaPlayer
import android.widget.Switch
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    var mode = "easy"
    var sounds = true

    lateinit var music : MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        music = MediaPlayer.create(this@MainActivity, R.raw.bgmusic)
        music.isLooping = true
        music.setVolume(0.2f, 0.2f)
        music.start()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Switch>(R.id.switch1).setOnCheckedChangeListener { t, check ->
            check != check
            log("switched to hard: $check")
            mode = if (check) "hard" else "easy"
        }
    }

    fun start(v: View) {
        val intent = Intent(this, PlaygroundActivity::class.java)
        intent.putExtra("mode", mode)
        intent.putExtra("sounds", sounds)
        startActivity(intent)
    }

    fun quit(v: View) {
        finish()
    }

    fun mute(v: View) {
        sounds = !sounds
        val btn = findViewById<FloatingActionButton>(R.id.soundBtn)
        if (!sounds) {
            log("off")
            btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.soundoff))
            music.pause()
        } else {
            log("on")
            btn.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.soundon))
            music.start()
        }
    }

    override fun onPause() {
        super.onPause()
        music.stop()
    }
}

fun log(s : Any) {
    Log.i("PLS_HELP", s.toString())
}