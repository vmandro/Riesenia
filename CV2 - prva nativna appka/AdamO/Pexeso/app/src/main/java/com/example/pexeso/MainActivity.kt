package com.example.pexeso

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.IntegerRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.time.Duration
import java.util.*
import kotlin.random.Random


class MainActivity : AppCompatActivity() {

    val buttons = listOf(
        R.id.button1,
        R.id.button2,
        R.id.button3,
        R.id.button4,
        R.id.button5,
        R.id.button6,
        R.id.button7,
        R.id.button8,
        R.id.button9,
        R.id.button10,
        R.id.button11,
        R.id.button12,
        R.id.button13,
        R.id.button14,
        R.id.button15,
        R.id.button16,
        R.id.button17,
        R.id.button18,
        R.id.button19,
        R.id.button20,
        R.id.button21,
        R.id.button22,
        R.id.button23,
        R.id.button24,
        R.id.button25,
        R.id.button26,
        R.id.button27,
        R.id.button28,
        R.id.button29,
        R.id.button30,
        R.id.button31,
        R.id.button32,
        R.id.button33,
        R.id.button34,
        R.id.button35,
        R.id.button36
    )
    val pikas = listOf(
        R.drawable.img1, R.drawable.img2, R.drawable.img3, R.drawable.img4,
        R.drawable.img5, R.drawable.img6, R.drawable.img7, R.drawable.img8,
        R.drawable.img9, R.drawable.img10, R.drawable.img11, R.drawable.img12,
        R.drawable.img13, R.drawable.img14, R.drawable.img15, R.drawable.img16,
        R.drawable.img17, R.drawable.img18
    )

    val cards = mutableMapOf<Int, Pexeso>()
//    var rotated = 0     // max 2
    var rotatedCards = mutableListOf<Pexeso>()
    var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        textViewScore.text = score.toString()

        val rnd = Random
        val valueSet = mutableSetOf<Int>()
        var index = 0
        while (index != pikas.size) {
            val v = rnd.nextInt(pikas.size) + 1
            if (!valueSet.contains(v)) { index++ }
            valueSet.add(v)
        }
        val values = mutableListOf<Int>()
        values.addAll(valueSet)
        values.addAll(valueSet)
        Log.d("PIKAS", "Original $values")
        values.shuffle()
        Log.d("PIKAS", "Len: ${values.size} Shuffle: $values")

        for (butID in buttons) {
            val button = findViewById<Button>(butID)
            val number = rnd.nextInt(values.size)
            button.background = ContextCompat.getDrawable(applicationContext, R.drawable.backside)
            cards[butID] = Pexeso(pikas[values[number] - 1], butID, false, true)
            values.removeAt(number)
        }
    }

    fun onClick(v : View) {
        for((index, butID) in buttons.withIndex()) {
            if (v.id == butID && cards[butID]?.visible == false && cards[butID]?.clickable == true) {
                Log.d("PIKAS", "klikol si na ${index + 1}")
                Log.d("PIKAS", "${cards[butID]}")
                if (rotatedCards.size < 2) {
                    if (cards.containsKey(butID)) {
                        cards[butID]?.visible = !cards[butID]?.visible!!
                        if (cards[butID] != null) {
                            cards[butID]?.let { rotateCard(it) }
                        }
                        if (rotatedCards.size == 2) {
                            Log.d("PIKAS", "${rotatedCards.size}||${rotatedCards}")
                            if (rotatedCards[0].pic == rotatedCards[1].pic) {
                                Log.d("PIKAS", "pic == pic")

                                rotatedCards[0].clickable = false
                                rotatedCards[1].clickable = false
                                score++
                                textViewScore.text = score.toString()
                                if (score == pikas.size) {
                                    Log.d("PIKAS", "CONGRATS")
                                    val toast = Toast.makeText(applicationContext, "~~~ CONGRATULATIONS ~~~", Toast.LENGTH_LONG)
                                    toast.show()
                                }
                                rotatedCards.clear()
                            } else {
                                Log.d("PIKAS", "back")
                                rotatedCards[0].visible = false
                                rotatedCards[1].visible = false
                                object : CountDownTimer(1500,1000) {
                                    override fun onTick(p0: Long) {}
                                    override fun onFinish() {
                                        Log.d("PIKAS", "Finished timer ${rotatedCards}")
                                        rotateCard(rotatedCards[0])
                                        rotateCard(rotatedCards[0])
                                        Log.d("PIKAS", "DELETED finished ${rotatedCards.size}||${rotatedCards}")
                                    }
                                }.start()
                            }
                            Log.d("PIKAS", "${rotatedCards.size}||${rotatedCards}")

                            Log.d("PIKAS", "DELETED ${rotatedCards.size}||${rotatedCards}")
                        }
                    }
                }

//                val toast = Toast.makeText(applicationContext, "${index + 1}", Toast.LENGTH_SHORT)
//                toast.show()
            }

        }
    }

    data class Pexeso(val pic : Int, val butID: Int, var visible : Boolean, var clickable : Boolean) {}
    fun rotateCard(card : Pexeso) {
        val button = findViewById<Button>(card.butID)
        if (card.visible) {
            Log.d("PIKAS", "VIS")
            button.background = ContextCompat.getDrawable(applicationContext, card.pic)
            cards[button.id]?.let { rotatedCards.add(it) }
        } else {
            Log.d("PIKAS", "INVIS")
            button.background =
                ContextCompat.getDrawable(applicationContext, R.drawable.backside)
            cards[button.id]?.let { rotatedCards.remove(it) }
        }
    }
}
