package com.example.jednocertovane

import android.content.Context
import com.example.jednocertovane.canvasobjects.Guest
import kotlin.random.Random

class GuestGenerator(val context: Context, val N: Int) {
    val rnd = Random
    val levelPoints = 150
    /*kazdych 150 bodov pribudne maximum ludi o 1
    * do 150 bodov     ... 1 guest
    * 150 - 300 bodov ... 2 guesti
    * ...
    * */

    fun generateGuests(score: Int, actualNumberOfGuests: Int): List<Guest> {

        val guestsToAdd = emptyList<Guest>().toMutableList()
        val maxNumberOfGuests = score / levelPoints + 1

        val guestsToGenerate = maxNumberOfGuests - actualNumberOfGuests

        for(i in 0 until guestsToGenerate) {
            guestsToAdd.add(generateRandomGuest())
        }

        return guestsToAdd
    }

    private fun generateRandomGuest(): Guest {
        val id = rnd.nextInt(5)
        val i =  rnd.nextInt(N)

        return Guest(context, id, i, N)
    }
}