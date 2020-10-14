package com.example.pexeso

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    val buttons = listOf(
        R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6,
        R.id.button7, R.id.button8, R.id.button9, R.id.button10, R.id.button11, R.id.button12,
        R.id.button13, R.id.button14, R.id.button15, R.id.button16, R.id.button17, R.id.button18,
        R.id.button19, R.id.button20, R.id.button21, R.id.button22, R.id.button23, R.id.button24,
        R.id.button25, R.id.button26, R.id.button27, R.id.button28, R.id.button29, R.id.button30,
        R.id.button31, R.id.button32, R.id.button33, R.id.button34, R.id.button35, R.id.button36
    )
    val pikas = listOf(
        R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4,
        R.drawable.img5, R.drawable.img6, R.drawable.img7, R.drawable.img8,
        R.drawable.img9, R.drawable.img10, R.drawable.img11, R.drawable.img12,
        R.drawable.img13, R.drawable.img14, R.drawable.img15, R.drawable.img16,
        R.drawable.img17, R.drawable.img18
    )
    val unknown = R.drawable.imgq
    var clickedImages = mutableListOf<View>()
    var revealedImages = mutableListOf<View>()
    var buttonImages = mutableMapOf<Int, Int>()
    var points = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        newgame.setOnClickListener { newGame() }
        newGame()
    }

    private fun newGame() {
        buttons.forEach { findViewById<Button>(it).setBackgroundResource(unknown) }
        val imageList = ((0..17).toList() + (0..17).toList()).shuffled()
        buttons.forEachIndexed { i, id -> buttonImages[id] = imageList[i] }
        points = 0
        score.text = "Score: 0"
        newgame.isEnabled = false
        revealedImages.clear()
    }

    fun onClick(v : View) {
        if (clickedImages.size > 1 || v in clickedImages || v in revealedImages) return
        if (v.id in buttons) {
            buttonImages[v.id]?.let { v.setBackgroundResource(pikas[it]) }
            clickedImages.add(v)
        }
        if (clickedImages.size == 2) {
            if (buttonImages[clickedImages[0].id] == buttonImages[clickedImages[1].id]) {
                revealedImages.add(clickedImages[0])
                revealedImages.add(clickedImages[1])
                clickedImages.clear()
                points++
                score.text = "Score: $points"
                if (revealedImages.size == buttons.size) {
                    newgame.isEnabled = true
                    Toast.makeText(applicationContext, "Congratulations! :)", Toast.LENGTH_LONG).show()
                }
            }
            else Handler().postDelayed(this::hideClickedImages, 2000)
        }
    }

    private fun hideClickedImages() {
        clickedImages[0].setBackgroundResource(unknown)
        clickedImages[1].setBackgroundResource(unknown)
        clickedImages.clear()
    }
}
