package com.fmph.kai.patern

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.zadaj.*
import java.util.*
import kotlin.concurrent.schedule

class ZadajKod : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.zadaj)
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        val pg = findViewById<Canv>(R.id.imageView2)
        var kolko = intent.getIntExtra("kolko",3)-1
        fun vytvor(){
            val w = pg.width
            val h = pg.height

            val pomX = w/(kolko+2)
            Log.d("sss","$w,$pomX")
            for(i in 0..kolko){
                for(k in 0..kolko){
                    pg.addBod(pomX*(k+1),pomX*(i+1),i,k,kolko)
                }
            }
        }
        val timer = Timer().schedule(1000,20){
            runOnUiThread{
                pg.invalidate()
            }
        }
        timer.run()

        val timerr = Timer().schedule(1000,500){
            runOnUiThread{
                if(pg.mozem()){
                    vytvor()
                    this.cancel()
                }
            }
        }
        restart.setOnClickListener {
            for(i in pg.body){
                i.farba = Color.RED
                i.free = false
            }
            pg.pom = 0
            pg.ciara =emptyList<Float>().toMutableList()
            pg.KOD = emptyList<Int>().toMutableList()
            pg.last = emptyList<Int>().toMutableList()
            pg.lastt = 0
            pg.kontrola = false

        }
        ulozit.setOnClickListener {
            if(pg.KOD.size > 3) {
                var pom = pg.KOD.toString()
                intent = Intent(this, MainActivity::class.java)
                intent.putExtra("KOD", pom)
                intent.putExtra("kolko",kolko+1)
                startActivity(intent)
                finish()
            }else{
                Toast.makeText(this,"Najprv yadaj patern", Toast.LENGTH_SHORT).show()
            }
        }
    }

}