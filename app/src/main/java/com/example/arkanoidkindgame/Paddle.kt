package com.example.arkanoidkindgame

class Paddle(var height: Int, var width: Int, var xPosition: Int, var yPosition: Int) {
    fun contains(x: Int, y: Int, R: Int): Boolean {
        if (x >= xPosition && x <= xPosition + width &&
            y - R >= yPosition && y - R <= yPosition + height
        )
            return true

        return false
    }

}