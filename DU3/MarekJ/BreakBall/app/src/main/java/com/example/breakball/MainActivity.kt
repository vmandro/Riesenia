package com.example.breakball

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var MAX_COUNT_OF_LEVELS = 4
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var intent: Intent

        var preferences = getSharedPreferences("DestroyBall", Context.MODE_PRIVATE)
        if(preferences.contains("WAS_CREATED").not()) {
            setPreferences(preferences)
        }

        // Intro
        intent = Intent(this, IntroActivity::class.java)
        startActivity(intent)


        val levels = arrayListOf<String>()
        for(level in 1..MAX_COUNT_OF_LEVELS) {
            levels.add("Level $level")
        }

        val adapter: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line, levels
        )
        listOfLevels.adapter = adapter

        listOfLevels.setOnItemClickListener { adapterView, view, i, l ->
            println("Index = $i")
            intent = Intent(this, LevelActivity::class.java)
            intent.putExtra("level", i + 1)
            startActivity(intent)
        }

        highscore.setOnClickListener {
            intent = Intent(this, LevelActivity::class.java)
            startActivity(intent)
        }
    }

    fun setPreferences(preferences: SharedPreferences) {
        var editor = preferences.edit()

        // For Levels
        for(level in 1..MAX_COUNT_OF_LEVELS) {
            editor.putInt("LEVEL_HIGHSCORE_$level", 0)
        }
        editor.putBoolean("WAS_CREATED", true)

        editor.commit()
    }
}