package com.example.milionar

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //val obrazky = resources.getStringArray(R.array.obrazky)
        val otazky = resources.getStringArray(R.array.otazky)
        val odpovedeA = resources.getStringArray(R.array.odpovedeA)
        val odpovedeB = resources.getStringArray(R.array.odpovedeB)
        val odpovedeC = resources.getStringArray(R.array.odpovedeC)
        val odpovedeD = resources.getStringArray(R.array.odpovedeD)
        val spravneOdpovede = resources.getStringArray(R.array.spravneOdpovede)
        val obrazky = listOf(
            R.drawable.obr1,
            R.drawable.obr2,
            R.drawable.obr3,
            R.drawable.obr4,
            R.drawable.obr5,
            R.drawable.obr6,
            R.drawable.obr7,
            R.drawable.obr8,
            R.drawable.obr9,
            R.drawable.obr10
        )
        // odtialto uz programujte vy, tu uz asi nezostane kamen na kameni...
        var otazka = 0
        respA.text = odpovedeA[otazka]
        respB.text = odpovedeB[otazka]
        respC.text = odpovedeC[otazka]
        respD.text = odpovedeD[otazka]
        editTextTextMultiLine.setText(otazky[otazka])
        imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, obrazky[otazka]))

        var fiftyFiftyCount = 2

        val mp1: MediaPlayer = MediaPlayer.create(this, R.raw.correct)
        //mp1.start()
        val mp2: MediaPlayer = MediaPlayer.create(this, R.raw.incorrect)

        val timer = object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                runOnUiThread {
                    time.setText("${millisUntilFinished/1000}")
                }
            }

            override fun onFinish() {
                Toast.makeText(this@MainActivity, "Nestihli ste odpovedat, prehrali ste", Toast.LENGTH_SHORT).show()
//                again()
                fiftyFiftyCount = 2
                for (i in 0..(linearLayout.childCount-1)) {
                    (linearLayout.getChildAt(linearLayout.childCount - 1 - i) as CheckBox).isChecked = false
                }
                otazka = 0

                respA.text = odpovedeA[otazka]
                respB.text = odpovedeB[otazka]
                respC.text = odpovedeC[otazka]
                respD.text = odpovedeD[otazka]
                editTextTextMultiLine.setText(otazky[otazka])
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, obrazky[otazka]))
                //v linearLayout budu iba checkboxy
                (linearLayout.getChildAt(linearLayout.childCount-1 - otazka) as CheckBox).isChecked = true
                //again() - pri again nastavit na 2 tie pocty...
                if(fiftyFiftyCount > 0) {
                    fiftyfiftyBtn.isEnabled = true
                }

                //enable radiobtns
                for (i in 0..(radioGroup.childCount-1)) {
                    radioGroup.getChildAt(i).isEnabled = true
                }
                this.cancel()
                this.start()
                mp2.start()
            }
        }



        timer.start()

        fiftyfiftyBtn.setOnClickListener {
            //na kazdu otazku mozme pouzit 50:50 iba raz
            if(fiftyFiftyCount > 0) {
                var i = 2
                //cyklus bezi kym sa nevyberu 2 nespravne a rozdielne odpovede
                while(i > 0) {
                    val rnd = (0..(radioGroup.childCount-1)).random()
                    val optionToDisable = radioGroup.getChildAt(rnd)
                    if(resources.getResourceEntryName(optionToDisable.id)[4].toString() != spravneOdpovede[otazka] && optionToDisable.isEnabled == true) {
                        optionToDisable.isEnabled = false
                        optionToDisable.clearFocus()

                        i--
                    }
                }

                fiftyfiftyBtn.isEnabled = false
                fiftyFiftyCount--
            }
        }


        takeithomeBtn.setOnClickListener {
            //vynuluju s checkboxy a nastavi sa prva otazka... to iste sa stane ked sa zodpovie otazka zle alebo posledna spravne a nastavi sa fiftyfifty na 2
            Toast.makeText(this, "Zobrali ste si vyhru", Toast.LENGTH_SHORT).show()

            fiftyFiftyCount = 2
            for (i in 0..(linearLayout.childCount-1)) {
                (linearLayout.getChildAt(linearLayout.childCount - 1 - i) as CheckBox).isChecked = false
            }
            otazka = 0

            respA.text = odpovedeA[otazka]
            respB.text = odpovedeB[otazka]
            respC.text = odpovedeC[otazka]
            respD.text = odpovedeD[otazka]
            editTextTextMultiLine.setText(otazky[otazka])
            imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, obrazky[otazka]))
            //v linearLayout budu iba checkboxy
            (linearLayout.getChildAt(linearLayout.childCount-1 - otazka) as CheckBox).isChecked = true
            //again()
            if(fiftyFiftyCount > 0) {
                fiftyfiftyBtn.isEnabled = true
            }

            //enable radiobtns
            for (i in 0..(radioGroup.childCount-1)) {
                radioGroup.getChildAt(i).isEnabled = true
            }
        }




        answerBtn.setOnClickListener {
            if(radioGroup.checkedRadioButtonId != -1 && resources.getResourceEntryName(radioGroup.checkedRadioButtonId)[4].toString() == spravneOdpovede[otazka]) {
                mp1.start()
                timer.cancel()
                timer.start()
                //next()
                otazka++
                if(otazka == otazky.size) {
                    otazka = 0
                    Toast.makeText(this, "Vyhrali ste", Toast.LENGTH_SHORT).show()
                    fiftyFiftyCount = 2

                    for (i in 0..(linearLayout.childCount-1)) {
                        (linearLayout.getChildAt(linearLayout.childCount - 1 - i) as CheckBox).isChecked = false
                    }
                }
                else {
                    Toast.makeText(this, "Spravna odpoved", Toast.LENGTH_SHORT).show()
                }

                respA.text = odpovedeA[otazka]
                respB.text = odpovedeB[otazka]
                respC.text = odpovedeC[otazka]
                respD.text = odpovedeD[otazka]
                editTextTextMultiLine.setText(otazky[otazka])
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, obrazky[otazka]))
                //v linearLayout budu iba checkboxy
                (linearLayout.getChildAt(linearLayout.childCount-1 - otazka) as CheckBox).isChecked = true

                //next()
            } else {
                mp2.start()
                Toast.makeText(this, "Nespravna odpoved, prehrali ste", Toast.LENGTH_SHORT).show()
                //again()
                fiftyFiftyCount = 2
                for (i in 0..(linearLayout.childCount-1)) {
                    (linearLayout.getChildAt(linearLayout.childCount - 1 - i) as CheckBox).isChecked = false
                }
                otazka = 0

                respA.text = odpovedeA[otazka]
                respB.text = odpovedeB[otazka]
                respC.text = odpovedeC[otazka]
                respD.text = odpovedeD[otazka]
                editTextTextMultiLine.setText(otazky[otazka])
                imageView.setImageDrawable(ContextCompat.getDrawable(applicationContext, obrazky[otazka]))
                //v linearLayout budu iba checkboxy
                (linearLayout.getChildAt(linearLayout.childCount-1 - otazka) as CheckBox).isChecked = true
                //
            }

            if(fiftyFiftyCount > 0) {
                fiftyfiftyBtn.isEnabled = true
            }
            //enable radiobtns
            for (i in 0..(radioGroup.childCount-1)) {
                radioGroup.getChildAt(i).isEnabled = true
            }

        }

    }
    
}