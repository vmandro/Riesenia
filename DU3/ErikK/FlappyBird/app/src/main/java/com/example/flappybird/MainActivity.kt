package com.example.flappybird

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit

class MainActivity : AppCompatActivity() {
    private var HighScore = 0
    private lateinit var preferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferences = getSharedPreferences("life", Context.MODE_PRIVATE)
        HighScore = preferences.getInt("highscore", 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        val editor = preferences.edit()
        editor.putInt("highscore", HighScore)
        editor.commit()
    }

    fun sHighScore(score : Int){
        HighScore = score
        val editor = preferences.edit()
        editor.putInt("highscore", HighScore)
        editor.commit()
    }

    fun gHighScore() : Int = HighScore
}