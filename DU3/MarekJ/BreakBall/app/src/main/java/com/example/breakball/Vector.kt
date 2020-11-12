package com.example.breakball

class Vector(x: Float, y: Float) {
    private var moveX: Float
    private var moveY: Float

    init {
        moveX = x
        moveY = y
    }

    fun getX() : Float {
        return moveX
    }

    fun getY() : Float {
        return moveY
    }

    fun invertX() {
        moveX = -moveX
    }

    fun invertY() {
        moveY = -moveY
    }
}