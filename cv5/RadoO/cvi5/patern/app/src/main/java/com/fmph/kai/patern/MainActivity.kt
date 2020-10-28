package com.fmph.kai.patern

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.android.synthetic.main.activity_main2.view.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        var mriezka = intent.getIntExtra("kolko",3)
        /**
         * mriezka = ?
         */
        seekBar.progress = mriezka-3
        text.setText("Vyber veľkosť mriežky: ${mriezka}x${mriezka}")
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(
                seekBar: SeekBar,
                progress: Int,
                fromUser: Boolean
            ) {
                Log.d("test","$progress")
                mriezka = progress+3
                intent.removeExtra("KOD")
                text.setText("Vyber veľkosť mriežky: ${progress+3}x${progress+3}")
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        odomkni.setOnClickListener {
            val a = intent.getStringExtra("KOD")
            if(a == null){
                Toast.makeText(this,"Najpv zadaj patern",Toast.LENGTH_SHORT).show()
            }else{
                intent = Intent(this,Odomkni::class.java)
                intent.putExtra("KOD",a)
                intent.putExtra("kolko",mriezka)
                startActivity(intent)
                finish()
            }
        }
        zadaj.setOnClickListener {
            val intent = Intent(this,ZadajKod::class.java)
            intent.putExtra("kolko",mriezka)
            startActivity(intent)
            finish()
        }
    }
}