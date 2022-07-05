package com.example.arkanoidkindgame

import kotlin.random.Random

class Ball(var xPosition: Int, var yPosition: Int, var R: Int) {

    var dx = 0
    var dy = 10
    fun getBallPositionAndSize(): Triple<Int, Int, Int> {

        return Triple(xPosition, yPosition, R)
    }

    fun bounceVert() {
        var rand = (-1..1).random()
        dx = dx + rand
        dy = -dy

    }

    fun bounceHor() {
        var rand = (-1..1).random()
        dx = -dx + rand
        dy = dy

    }

    fun move() {
        xPosition += dx
        yPosition += dy
    }
}