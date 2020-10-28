package com.fmph.kai.patern

import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_odomkni.*
import java.util.*
import kotlin.concurrent.schedule

class Odomkni : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_odomkni)
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        val pg = findViewById<Canv>(R.id.imageView)
        Log.d("test","${pg.pom}")
        val kod = intent.getStringExtra("KOD")
        fun vytvor(){
            val w = pg.width
            val h = pg.height
            var kolko = intent.getIntExtra("kolko",3)-1

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
                if(pg.kontrola){
                    if(kod == pg.KOD.toString()){
                        pg.farba = Color.GREEN
                        for(i in pg.body){
                            if(i.free) {
                                i.farba = Color.GREEN
                                imageView3.setImageDrawable(ContextCompat.getDrawable(this@Odomkni,R.drawable.ic_unlock))
                                textView3.setText("Odomknuté")
                                textView3.setTextColor(Color.GREEN)
                            }
                        }
                    }else{
                        Toast.makeText(this@Odomkni,"Patern sa nezhoduje s uloženým",Toast.LENGTH_SHORT).show()
                        pg.kontrola = false
                        for(i in pg.body){
                            i.farba = Color.RED
                            i.free = false
                        }
                        pg.pom = 0
                        pg.ciara =emptyList<Float>().toMutableList()
                        pg.KOD = emptyList<Int>().toMutableList()
                        pg.last = emptyList<Int>().toMutableList()
                        pg.lastt = 0
                    }
                }
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
        spat.setOnClickListener {
            var a = intent.getStringExtra("KOD")
            val mriezka = intent.getIntExtra("kolko",3)
            intent = Intent(this,MainActivity::class.java)
            intent.putExtra("KOD",a)
            intent.putExtra("kolko",mriezka)
            startActivity(intent)
            finish()
        }

    }
}