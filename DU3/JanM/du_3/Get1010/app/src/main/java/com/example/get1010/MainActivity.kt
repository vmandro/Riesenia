package com.example.get1010

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        listenersSetter()
        scoreSetter()
    }

    fun listenersSetter() {
        playButton.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }

        exitButton.setOnClickListener {
            finish()
            exitProcess(0)
        }
    }

    fun scoreSetter() {
        preferences = getSharedPreferences("main", Context.MODE_PRIVATE)
        scoreText.text = "BEST: ${Integer.toBinaryString(preferences.getInt("score", 0))}" // best score
    }
}