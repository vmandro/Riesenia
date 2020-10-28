package com.example.snurky

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.snurky4 -> playground.changeSize(4)
            R.id.snurky5 -> playground.changeSize(5)
            else -> playground.changeSize(6)
        }
        return true
    }

    fun clear(v : View){
        playground.clear()
    }

    fun save(v : View){
        playground.save()
    }
}