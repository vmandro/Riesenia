package com.fmph.kai.mistyforest

import android.content.pm.ActivityInfo
import android.graphics.Point
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display

class MainActivity : AppCompatActivity() {

    var SCREEN_WIDTH = 0
    var SCREEN_HEIGHT = 0
    private lateinit var background_music: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        val display: Display = getWindowManager().getDefaultDisplay()
        val size = Point()
        display.getSize(size)
        SCREEN_WIDTH = size.x
        SCREEN_HEIGHT = size.y
        background_music = MediaPlayer.create(applicationContext, R.raw.background_music)
        background_music.start()
        background_music.isLooping = true
    }
    /*override fun onPause() {
        super.onPause();
        background_music.stop();
    }

    override fun onResume() {
        super.onResume()
        Log.d("RESS", "RESUMEDDD")
        background_music.start()
        }*/
}